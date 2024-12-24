
package com.common.module.util;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FileUtils {

	/**
	 * key-value格式文件配置,=号分隔,#是行注释
	 * 
	 * @param pathname 文件路径
	 * @return Map<String, String>
	 */
	public static Map<String, String> readFileToMap(String pathname) {
		Validate.isTrue(!StringUtils.isEmpty(pathname));
		Map<String, String> map = Maps.newHashMap();
		List<String> lines = readFileToLines(pathname);
		for (String line : lines) {
			line.replaceAll(" ", "").trim();
			if (StringUtils.isEmpty(line) || line.startsWith("#"))
				continue;
			String[] ss = line.split("=");
			if (ss.length != 2) {
				throw new RuntimeException(pathname + ":" + line);
			}
			String key = ss[0].trim().replaceAll(" ", "").trim();
			String value = ss[1].trim().replaceAll(" ", "").trim();
			map.put(key, value);
		}
		return map;
	}

	public static List<String> readFileToLines(String pathname) {
		Validate.isTrue(!StringUtils.isEmpty(pathname));
		return readFileToLines(pathname, StringUtils.CHARSET_NAME);
	}

	public static List<String> readFileToLines(String pathname, String charset) {
		try {
			Validate.isTrue(!StringUtils.isEmpty(pathname));
			return org.apache.commons.io.FileUtils.readLines(new File(pathname), charset);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 指定后缀重命名文件
	 * 
	 * @param file 文件对象
	 * @param suffix 后缀
	 */
	public static void renamedFile(File file, String suffix) {
		Objects.requireNonNull(file);
		Validate.isTrue(file.exists());
		Objects.requireNonNull(suffix);
		Validate.isTrue(!StringUtils.isEmpty(suffix));
		file.renameTo(new File(file.getPath() + "." + suffix));
	}

	/**
	 * 重命名文件,并且在后缀加上时间
	 * 
	 * @param file 文件对象
	 */
	public static void renameWithDate(File file) {
		Objects.requireNonNull(file);
		Validate.isTrue(file.exists());
		renamedFile(file, DateUnit.getDateTemplate().getStringTemplete());
	}

	public static File makeSureFile(String pathname) {
		Validate.isTrue(!StringUtils.isEmpty(pathname));
		File file = new File(pathname);
		String path = file.getPath();
		String[] more = path.split("\\" + File.separator);
		return makeSureFile(more);
	}

	public static File makeSureFile(String... more) {
		Objects.requireNonNull(more);
		Validate.isTrue(more.length > 0);
		for (int i = 0; i < more.length; i++) {
			String pathname = more[i];
			Validate.isTrue(!StringUtils.isEmpty(pathname));
		}
		try {
			String pathname = "";
			for (int i = 0; i < more.length - 1; i++) {
				pathname += more[i];
				pathname += File.separator;
			}
			Path path = Paths.get(pathname);
			if (path == null || !path.toFile().exists()) {
				Files.createDirectories(path);
			}
			path = Paths.get(pathname + File.separator + more[more.length - 1]);
			if (path == null || !path.toFile().exists()) {
				Files.createFile(path);
			}
			return path.toFile();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取文件所在的目录
	 * 
	 * @param file 文件对象
	 * @return File
	 */
	public static File getDir(File file) {
		Objects.requireNonNull(file);
		Validate.isTrue(file.exists());
		if (file.isDirectory())
			return file;
		String path = file.getPath();
		String[] more = path.split("\\" + File.separator);
		StringBuilder dirPath = new StringBuilder();
		for (int i = 0; i < more.length - 1; i++) {
			String string = more[i];
			dirPath.append(string).append(File.separator);
		}
		String dirPathName = dirPath.toString();
		return new File(dirPathName);
	}

	public static void main(String[] args) throws Exception {

		List<Map<String, Object>> list = null;
		StringBuilder builder = new StringBuilder();
		list.forEach(map -> {
			map.forEach((k, v) -> {
				builder.append(k).append("=").append(v).append(" ");
			});
			builder.append("\n");
		});
		File file = new File("./progress_" + DateUnit.timeFormat("yyyyMMdd", System.currentTimeMillis()));
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.write(builder.toString());

		fileWriter.flush();
		fileWriter.close();

	}
}
