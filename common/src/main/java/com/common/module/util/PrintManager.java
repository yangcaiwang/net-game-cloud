/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.common.module.util;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public final class PrintManager {

	private static final PrintStream out_def = System.out;

	private static final PrintStream err_def = System.err;

	private static final PrintStream noPrint = new PrintStream(new MyOutputStream());

	private PrintManager() {

	}

	public static void showPrint(boolean show) {

		if (show) {
			System.setErr(err_def);
			System.setOut(out_def);
		} else {
			System.setErr(noPrint);
			System.setOut(noPrint);
			LoggerFactory.getLogger(PrintManager.class).warn("屏蔽系统输出");
		}
	}

	private static final class MyOutputStream extends OutputStream {

		@Override
		public void write(int b) throws IOException {

		}
	}
}
