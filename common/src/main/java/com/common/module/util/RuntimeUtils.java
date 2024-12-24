
package com.common.module.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

public class RuntimeUtils {
	private static Logger log = LoggerFactory.getLogger(RuntimeUtils.class);

	/**
	 * 执行系统命令,如果是自定义的执行linux的脚本, 请在command前面加上sh
	 * 
	 * @param command
	 *            必须是可执行文件 windows下.exe或者.bat文件; linux 下.sh文件
	 * @param params
	 *            执行时带入的参数
	 * @return Process
	 */
	public static Process running(String command, String... params) {

		String exe = null;
		Process process = null;
		try {
			StringBuilder builder = null;
			if (params != null && params.length > 0) {
				builder = new StringBuilder().append(" ");
				for (int i = 0; i < params.length; i++) {
					builder.append(params[i]);
					if (i < params.length - 1) {
						builder.append(" ");
					}
				}
			}
			exe = builder == null ? command : command + builder.toString();
			log.info("run :" + exe);
			process = Runtime.getRuntime().exec(exe);
			int result = process.waitFor();
			if (0 != result) {
				log.error("执行系统命令失败 :" + command + "\t" + Arrays.toString(params) + "," + result);
				return null;
			}
			log.info("run :" + exe + "," + process);
			return process;
		} catch (Exception e) {
			log.error(command + "\t" + Arrays.toString(params));
			return null;
		}
	}

	/**
	 * 执行系统脚本并且得到执行结果,支持返回数据
	 * 
	 * @param command 脚本名
	 * @param params 参数列表
	 * @return String
	 */
	public static String runAs(String command, String... params) {

		try {
			Process process = running(command, params);
			if (process == null) {
				return "err:" + command + "\t" + Arrays.toString(params);
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				builder.append(line);
			}
			br.close();
			String ret = builder.toString();
			log.info("run >>>" + command + "\t" + Arrays.toString(params) + " <<<" + ret);
			return ret;
		} catch (Exception e) {
			log.error(command + "\t" + Arrays.toString(params));
			return null;
		}
	}

	public static void main(String[] args) throws Exception {
		String result = runAs("C:/Program Files/Notepad++/notepad++.exe");
		System.err.println(result);
	}
}
