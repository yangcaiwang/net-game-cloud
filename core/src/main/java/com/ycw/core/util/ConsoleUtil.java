
package com.ycw.core.util;

import com.ycw.core.network.netty.message.SocketChannelManage;

import java.util.Scanner;

/**
 * <控制台输入工具类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class ConsoleUtil {

	/**
	 * 控制台内容捕获器
	 * 
	 * @author lijishun
	 */
	static public interface Catcher {

		/**
		 * 接受并处理控制台输出的内容
		 * 
		 * @param input
		 *            控制台输入的内容分割组成的数组
		 * @return
		 */
		void accept(String[] input);
	}

	public static void handleInput(Catcher catcher) {
		new Thread(() -> {
			Scanner scanner = new Scanner(System.in);
			for (;;) {
				try {
					String line = scanner.nextLine();
					System.err.println("catch input : " + line);
					if (!StringUtils.isEmpty(line)) {
						String[] input = line.split("\\s++");
						catcher.accept(input);
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (e instanceof InterruptedException) {
						break;
					}
				}
			}
			scanner.close();
		}, ConsoleUtil.class.getName()).start();
	}

	public static void start() {
		handleInput(args -> {
			if (args != null && args.length >= 1) {
				String lowerCase = args[0].toLowerCase();
				if (lowerCase.equalsIgnoreCase("shutdown") || lowerCase.equalsIgnoreCase("exit") || lowerCase.equalsIgnoreCase("quit")) {
					if (SystemUtils.isWindows()) {
						System.exit(0);
					}
				}
				if (lowerCase.equalsIgnoreCase("online")) {
					if (SystemUtils.isWindows()) {
						int onlineSize = SocketChannelManage.getInstance().onlineSize();
						System.out.println("在线数:" + onlineSize);
					}
				}
			}
		});
	}
}
