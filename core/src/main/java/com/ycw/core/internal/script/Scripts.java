
package com.ycw.core.internal.script;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.ycw.core.internal.event.EventBusesImpl;
import com.ycw.core.util.AnnotationUtil;
import com.ycw.core.util.ClassUtils;
import com.ycw.core.util.FileUtils;
import com.ycw.core.util.StringUtils;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <热更脚本工具类>
 * <p>
 * ps:
 * 脚本执行流程控制,低版本groovy不支持拉姆达表达式,需要在热更用groovy代替java类执行时,
 * 需要把拉姆达表达式替换成闭包方式或者原生java代码
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class Scripts {

    private static final Logger log = LoggerFactory.getLogger(Scripts.class);

    private static final Map<String, Object> objectCache = Maps.newHashMap();
    private static final Map<String, Long> timeCache = Maps.newHashMap();

    private static final Map<String, ScriptProcessor> scriptProcessorMap = new ConcurrentHashMap<>();

    private static GroovyClassLoader groovyClassLoader = null;

    static boolean removeCachedScript(File file) {
        return timeCache.remove(file.getName()) != null && objectCache.remove(file.getName()) != null;
    }

    /**
     * 从缓存获取一个脚本,当脚本不存在或者发生变化会重新加载新脚本放入缓存
     *
     * @param scriptPathname groovy文件的路径
     * @return
     */
    synchronized public static <T> T getScript(String scriptPathname) {
        Validate.isTrue(!StringUtils.isEmpty(scriptPathname), "pathname can not be null");
        Validate.isTrue(scriptPathname.endsWith(".groovy") || scriptPathname.endsWith(".java"), "pathname [%s] is not a groovy", scriptPathname);
        File script = new File(scriptPathname);
        Validate.isTrue(script.exists(), "script [%s] is not exists", script);
        if (!timeCache.containsKey(script.getName())) {
            timeCache.put(script.getName(), script.lastModified());
            objectCache.put(script.getName(), createScript(script));
            log.info("create script [{}]", script);
        } else {
            if (timeCache.get(script.getName()) != script.lastModified()) {
                timeCache.put(script.getName(), script.lastModified());
                objectCache.put(script.getName(), createScript(script));
                log.info("update script [{}]", script);
            }
        }
        return (T) objectCache.get(script.getName());
    }

    public static boolean checkFileModify(File file) {
        Long aLong = timeCache.get(file.getName());
        if (aLong == null || aLong != file.lastModified()) {
            return true;
        }
        return false;
    }

    public static <T> boolean isScript(T obj) {
        return ClassUtils.isAssignableFrom(GroovyObject.class, obj.getClass());
    }

    /**
     * 执行脚本,</br>
     * 如果直接调用就根据下面参数说明使用就行了</br>
     *
     * @param scriptPath 脚本所在的目录
     * @param request    请求内容</br>
     *                   如果是json格式,那么必须包含cmd=脚本名称,onlyOnce=true or false
     *                   表示是否脚本只能执行一次</br>
     *                   如果是字符串格式那么必须用空格分开几个参数,</br>
     *                   第一个参数(cmd)表示脚本名称,</br>
     *                   第二个参数(onlyOnce)表示是否只执行一次,</br>
     *                   后面的参数就是传递给脚本要用的参数,</br>
     *                   同时在脚本里按参数顺序打上注解(@ScriptParams)表明参数类型,如:@ScriptParams(
     *                   paramTypes=[String.class,Boolean.class])</br>
     * @return
     */
    public static String execute(String scriptPath, String request) {
        File file = null;
        String cmd = null;// 脚本名称
        boolean onlyOnce = true;// 是否只执行一次
        JSONObject params = null;// 逻辑参数
        ScriptProcessor scriptProcessor = null;
        try {
            if (request.startsWith("{") && request.endsWith("}")) {// json格式
                JSONObject jsonObject = JSONObject.parseObject(request);
                cmd = jsonObject.getString("cmd");
                onlyOnce = jsonObject.getBooleanValue("onlyOnce");
                params = new JSONObject(jsonObject);
                params.remove("cmd");
                params.remove("onlyOnce");
                file = getScriptFile(cmd, scriptPath);
                scriptProcessor = getScript(file.getAbsolutePath());
                return scriptProcessor.process(params);
            } else {// 字符串数格式
                String[] ss = request.split("\\s++");
                cmd = ss[0];
                onlyOnce = Boolean.parseBoolean(ss[1]);
                file = getScriptFile(cmd, scriptPath);
                scriptProcessor = getScript(file.getAbsolutePath());
                if (ss.length == 2) {// 没有其他参数
                    return scriptProcessor.process(params);
                }
                String[] _params = new String[ss.length - 2];
                System.arraycopy(ss, 2, _params, 0, _params.length);
                ScriptParams scriptParams = AnnotationUtil.findAnnotation(scriptProcessor.getClass(), ScriptParams.class);
                if (scriptParams == null) {
                    return String.format("脚本[%s]没有指定参数类型", scriptProcessor);
                }
                Class<?>[] paramTypes = scriptParams.paramTypes();
                if (paramTypes.length != _params.length) {
                    return String.format("脚本[%s]参数类型错误,请求的是[%s],配置的是[%s]", scriptProcessor, StringUtils.toString(_params), StringUtils.toString(paramTypes));
                }
                params = new JSONObject();
                for (int i = 0; i < paramTypes.length; i++) {
                    Class<?> clz = paramTypes[i];
                    params.put(StringUtils.merged(clz.getSimpleName(), (i + 1)), _params[i]);// 之所以在参数的key-class后面加上数字序号,是为了处理连续多个相同类型的参数
                }
                return scriptProcessor.process(params);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return e.getMessage();
        } finally {
            if (onlyOnce && file != null) {
                removeCachedScript(file);
                FileUtils.renameWithDate(file);
            }
        }
    }

    /**
     * 创建一个脚本,可以是任意类型
     *
     * @param file
     * @return
     */
    private static <T> T createScript(File file) {
        try {
            Validate.isTrue(file != null && file.exists());
//			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (groovyClassLoader == null) {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                groovyClassLoader = new GroovyClassLoader(classLoader);
            }
            Class<T> clz = groovyClassLoader.parseClass(file);

            Constructor<T> constructor = (Constructor<T>) clz.getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            T script = (T) constructor.newInstance();
            EventBusesImpl.getInstance().syncPublish(HotScriptEvent.valueOf(script));
            return script;
        } catch (Exception e) {
            log.error(String.format("创建脚本失败,file=[%s],error=[%s]", file, e.getMessage()), e);
            return null;
        } finally {
            groovyClassLoader.clearCache();
        }
    }

    private static File getScriptFile(String cmd, String scriptPath) {
        if (!scriptPath.endsWith("/"))
            scriptPath += "/";
        if (!cmd.endsWith(".groovy"))
            cmd += ".groovy";
        String pathname = scriptPath + cmd;
        File file = new File(pathname);
        if (!file.exists())
            throw new RuntimeException(String.format("脚本文件[%s]不存在", pathname));
        return file;
    }

    //////
    public static void addScriptProcess(String name, ScriptProcessor processor) {
        ScriptProcessor put = scriptProcessorMap.put(name, processor);
        if (put != null) {
            log.info("update script:{}", name);
        }
    }

    public static ScriptProcessor getScriptProcess(String name) {
        return scriptProcessorMap.get(name);
    }

    public static String executeHttpReq(Map<String, Object> map) throws Exception {
        JSONObject params = new JSONObject(map);
        String string = params.getString("cmd");
        if (string == null) {
            return "not found cmd";
        }

        ScriptProcessor scriptProcess = getScriptProcess(string);
        if (scriptProcess == null) {
            return "not found cmd script";
        }
        params.remove("cmd");
        params.remove("onlyOnce");
        return scriptProcess.process(params);
    }
}
