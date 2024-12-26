package com.gm.server.admin.web.controller.protoApi;

import com.common.module.cluster.property.PropertyConfig;
import com.gm.server.common.core.controller.BaseController;
import com.gm.server.common.core.domain.AjaxResult;
import com.gm.server.common.utils.ExecuteShellUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author wishcher tree
 * @date 2022/12/12 18:11
 */
@RestController
@RequestMapping("/devTools/config")
public class ServerConfigApiController extends BaseController {

    private AtomicBoolean reOpt = new AtomicBoolean(false);

    @PostMapping("/commitOpt")
    public AjaxResult commitOpt(@Validated @RequestBody Map<String, String> optMap) {

        if (!PropertyConfig.isDebug()) {
            return AjaxResult.error("开发模式下才可以操作");
        }
        if (reOpt.get()) {
            return AjaxResult.error("正在更新，请稍等再操作！");
        }
        String msg = "操作成功";
        String resultData = "";
        if (reOpt.compareAndSet(false, true)) {
            try {
                String opt = optMap.get("opt");
                if ("1".equals(opt)) {
                    String pathName = "./src/main/resources/bat/foot_sg.bat";
                    File batFile = new File(pathName);
                    boolean batFileExist = batFile.exists();
                    if (batFileExist) {
                        String s = ExecuteShellUtil.callCmd(batFile.getPath());
                        resultData = s;
                        logger.info(s);
                        msg = "svn update success";
                    } else {
                        msg = "svn update fail";
                    }
                } else if ("2".equals(opt)) {
                    String  pathName = "./src/main/resources/bat/sg_145.bat";
                    File batFile = new File(pathName);
                    boolean batFileExist = batFile.exists();
                    if (batFileExist) {
                        String s = ExecuteShellUtil.callCmd(batFile.getPath());
                        resultData = s;
                        logger.info(s);
                        msg = "svn update success";
                    } else {
                        msg = "svn update fail";
                    }
                } else {
                        reOpt.compareAndSet(true, false);
                        return AjaxResult.error("操作不存在");
                    }
            } catch (Exception e) {
                reOpt.compareAndSet(true, false);
                return AjaxResult.error("更新出错，联系开发人员");
            }
            reOpt.compareAndSet(true, false);
        } else {
            return AjaxResult.error("正在更新，请稍等再操作！");
        }

        return AjaxResult.success(msg, resultData);
    }

}
