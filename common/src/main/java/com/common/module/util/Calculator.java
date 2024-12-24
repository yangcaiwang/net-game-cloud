
package com.common.module.util;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 数学计算相关</br>
 * 超大数值计算:</br>
 * 1:BigInteger 大整数的加、减、乘、除和其他运算</br>
 * （ 1 ） public BigInteger add(BigInteger val) ：                     做加法运算</br>
 * （ 2 ） public BigInteger subtract(BigInteger val) ：                做减法运算</br>
 * （ 3 ） public BigInteger multiply(BigInteger val) ：                做乘法运算</br>
 * （ 4 ） public BigInteger divide(BigInteger val) ：                  做除法运算</br>
 * （ 5 ） public BigInteger remainder(BigInteger val) ：               做取余运算</br>
 * （ 6 ） public BigInteger[] divideAndRemainder (BigInteger val)
 * ：   用数组返回余数和商，结果数组中第一个值为商，第二个值为余数</br>
 * （ 7 ） public BigInteger pow(int exponent) ：                       进行取参数的
 * exponent 次方操作</br>
 * （ 8 ） public BigInteger negate() ：                                取相反数</br>
 * （ 9 ） public BigInteger shiftLeft(int n) ：                        将数字左移 n
 * 位，如果 n 为负数，做右移操作</br>
 * （ 10 ）public BigInteger shiftRight(int n) ：                       将数字右移 n
 * 位，如果 n 为负数，做左移操作</br>
 * （ 11 ）public BigInteger and(BigInteger val) ：                     做与操作</br>
 * （ 12 ）public BigInteger or(BigInteger val)  ：                      做或操作</br>
 * （ 13 ）public int compareTo(BigInteger val)
 * ：                      做数字比较操作</br>
 * （ 14 ）public boolean equals(Object x) ：                           当参数 x 是
 * BigInteger 类型的数字并且数值相等时，返回 true </br>
 * （ 15 ）public BigInteger min(BigInteger
 * val)   ：                    返回较小的数值</br>
 * （ 16 ）public BigInteger max(BigInteger
 * val)   ：                    返回较大的数值</br>
 * 2:BigDecimal 用来做超大的浮点数的运算</br>
 * public BigDecimal add(BigDecimal augend) ：做加法操作</br>
 * public BigDecimal subtract(BigDecimal subtrahend) ：做减法操作</br>
 * public BigDecimal multiply(BigDecimal multiplicand) ：做乘法操作</br>
 * public BigDecimal divide(BigDecimal divisor , int sacle ,int roundingMode)
 * ：做除法操作，方法中 3 个参数分别代表除数、商的小数点后的位数、近似处理模式</br>
 * 
 * @author lijishun
 */
public class Calculator {

	public static void main(String[] args) {

		Map<String, Object> m = Maps.newHashMap();
		m.put("a", BigDecimal.valueOf(1));
		m.put("x", BigDecimal.valueOf(.2f));
		m.put("b", BigDecimal.valueOf(5));
		m.put("c", BigDecimal.valueOf(77.9f));
		System.err.println(eval("a*(x*x)+b+c-(2/10000)", m));
		System.err.println(eval("4/5"));
		m = Maps.newHashMap();
		m.put("a", BigDecimal.valueOf(4));
		m.put("b", BigDecimal.valueOf(5));
		System.err.println(eval("a/b", m));
	}

	private final static Logger log = LoggerFactory.getLogger(Calculator.class);

	private final static ThreadLocal<ScriptEngine> SCRIPT_ENGINE_THREAD_LOCAL = new ThreadLocal<ScriptEngine>() {
		protected ScriptEngine initialValue() {
			return new ScriptEngineManager(Thread.currentThread().getContextClassLoader()).getEngineByName("js");
		};
	};

	/**
	 * 执行脚本,如果是执行数学表达式并且浮点计算,,如果要准确计算参数类型应该用BigDecimal
	 * 
	 * @param script
	 *            脚本内容,可以是数学表达式,也可以是js脚本
	 * @param m
	 *            参数
	 * @return
	 */
	public static Object eval(String script, Map<String, Object> m) {
		ScriptEngine engine = SCRIPT_ENGINE_THREAD_LOCAL.get();
		if (!CollectionUtils.isEmpty(m)) {
			Bindings bindings = engine.createBindings();
			m.forEach((key, value) -> bindings.put(key, value));
			engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
		}
		try {
			Object result = engine.eval(script);
			return result;
		} catch (ScriptException e) {
			log.error("eval[" + script + "],params[" + (m == null ? "null" : StringUtils.toString(m)) + "]", e);
			return null;
		}
	}

	/**
	 * 执行脚本,如果是执行数学表达式并且浮点计算,,如果要准确计算参数类型应该用BigDecimal
	 * 
	 * @param script
	 *            脚本内容,可以是数学表达式,也可以是js脚本
	 * @return
	 */
	public static Object eval(String script) {

		return eval(script, null);
	}

	/**
	 * 计算格子
	 * 
	 * @param x
	 *            坐标
	 * @param y
	 *            坐标
	 * @param unit
	 *            单位格子大小
	 * @param cols
	 *            总列数
	 * @return
	 */
	public static int getGrid(int x, int y, int unit, int cols) {

		return (y / unit) * cols + (x / unit);
	}

	/**
	 * 转换x,y
	 * 
	 * @param grid
	 *            格子
	 * @param cols
	 *            总列数
	 * @param unit
	 *            单位格子大小
	 * @return
	 */
	public static Point getPoint(int grid, int cols, int unit) {

		// 当前列 = 格子% 总列数
		int currentCol1 = grid % cols;
		// 当前行=(格子-当前列)/总列数
		int currentRow1 = (grid - currentCol1) / cols;
		int x = unit * currentCol1;
		int y = unit * currentRow1;
		return new Point(x, y);
	}

	/**
	 * 两个格子间的直线距离
	 * 
	 * @param grid1
	 *            格子
	 * @param grid2
	 *            格子
	 * @param unit
	 *            单位格子大小
	 * @param cols
	 *            总列数
	 * @return
	 */
	public static int linearDistance(int grid1, int grid2, int unit, int cols) {

		// 当前列 = 格子% 总列数
		int currentCol1 = grid1 % cols;
		// 当前行=(格子-当前列)/总列数
		int currentRow1 = (grid1 - currentCol1) / cols;
		double x1 = unit * currentCol1;
		double y1 = unit * currentRow1;
		// 当前列 = 格子% 总列数
		int currentCol2 = grid2 % cols;
		// 当前行=(格子-当前列)/总列数
		int currentRow2 = (grid2 - currentCol2) / cols;
		double x2 = unit * currentCol2;
		double y2 = unit * currentRow2;
		double cx = Math.abs(x2 - x1);// x距离
		double cy = Math.abs(y2 - y1);// y距离
		// 直线距离
		double cz = Math.sqrt(Math.pow(cx, 2) + Math.pow(cy, 2));
		cz = Math.ceil(cz);
		return (int) cz;
	}

	/**
	 * 如果想判断一个点是否在线段上，那么要满足以下两个条件： <br/>
	 * （1）（Q - P1） * （P2 - P1）= 0；<br/>
	 * （2）Q在以P1，P2为对角顶点的矩形内；<br/>
	 * 第一点通俗点理解就是要求Q、P1、P2三点共线；<br/>
	 * 当第一个满足后，就应该考虑是否会出现Q在P1P2延长线或反向延长线这种情况。<br/>
	 * 此时第二个条件就对Q点的横纵坐标进行了限制，要求横纵坐标要在P1P2两点的最小值和最大值之间，也就是说保证了Q在P1P2之间。
	 * 
	 * @param Pi
	 * @param Pj
	 * @param Q
	 * @return
	 */
	public static boolean inLine(Point Pi, Point Pj, Point Q) {

		if ((Q.x - Pi.x) * (Pj.y - Pi.y) == (Pj.x - Pi.x) * (Q.y - Pi.y) // 叉乘
				// 保证Q点坐标在pi,pj之间
				&& inRect(Pi, Pj, Q))
			return true;
		else
			return false;
	}

	/**
	 * 检查点Q是否在pi,pj为首尾节点的矩形内
	 * 
	 * @param Pi
	 * @param Pj
	 * @param Q
	 * @return
	 */
	public static boolean inRect(Point Pi, Point Pj, Point Q) {

		return Math.min(Pi.x, Pj.x) <= Q.x && Q.x <= Math.max(Pi.x, Pj.x) && Math.min(Pi.y, Pj.y) <= Q.y && Q.y <= Math.max(Pi.y, Pj.y);
	}

	/**
	 * @description 射线法判断点是否在多边形内部
	 * @param {Object}
	 *            p 待判断的点，格式：{ x: X坐标, y: Y坐标 }
	 * @param {Array}
	 *            poly 多边形顶点，数组成员的格式同 p
	 * @return {String} 点 p 和多边形 poly 的几何关系
	 */
	public static boolean inPolygon(Point p, Point[] poly) {

		int px = p.x;
		int py = p.y;
		int flag = 0;
		for (int i = 0, size = poly.length, j = size - 1; i < size; j = i, i++) {
			int sx = poly[i].x, sy = poly[i].y, tx = poly[j].x, ty = poly[j].y;
			// 点与多边形顶点重合
			if ((sx == px && sy == py) || (tx == px && ty == py)) {
				return true;
			}
			// 判断线段两端点是否在射线两侧
			if ((sy < py && ty >= py) || (sy >= py && ty < py)) {
				// 线段上与射线 Y 坐标相同的点的 X 坐标
				int x = sx + (py - sy) * (tx - sx) / (ty - sy);
				// 点在多边形的边上
				if (x == px) {
					return true;
				}
				// 射线穿过多边形的边界
				if (x > px) {
					flag++;
				}
			}
		}
		// 射线穿过多边形边界的次数为奇数时点在多边形内
		return flag % 2 == 1;
	}

	/**
	 * 判断x,y是否在0，0点的第radius圈
	 *
	 * @param radius
	 *            圈数-半径
	 * @param x
	 *            x坐标
	 * @param y
	 *            y坐标
	 * @return
	 */
	public static boolean isOnCircle(int radius, int x, int y) {
		int zero = radius * 2 + 1;
		int abs = Math.abs(x);
		int count = zero - abs;
		int min, max = 0;
		if (count % 2 == 0) {
			min = -1 * count / 2;
			max = count / 2 - 1;
		} else {
			min = -1 * (count - 1) / 2;
			max = (count - 1) / 2;
		}
		if (abs == radius) {
			if (y >= min && y <= max)
				return true;
		} else if (abs < radius && (y == min || y == max)) {
			return true;
		}
		return false;
	}

	public static boolean isOnCircle(int x1, int y1, int x2, int y2, int radius) {
		x2 = x2 - x1;
		if (x2 % 2 == 0) {
			y2 = y2 - y1;
		} else {
			y2 = y2 - y1 - 1;
		}
		return isOnCircle(radius, x2, y2);
	}

	public static int distance(int x1, int y1, int x2, int y2) {
		for (int i = 1; i < 5000; i++)
			if (isOnCircle(x1, y1, x2, y2, i))
				return i;
		return -1;
	}

	// int x;//圆心x坐标
	// int y;//圆心y坐标
	// int r;//圆的半径
	// int x1;点的x坐标
	// int y1;点得y坐标
	// if((x1>=x-r||x1<=x+r)&&(y1>=y-r||y1<=y+r))//判断
	public static boolean inRound(int x, int y, int r, int x1, int y1) {
		return (x1 >= x - r || x1 <= x + r) && (y1 >= y - r || y1 <= y + r);
	}

	// 点与圆的关系
	public static RoundType roundType(int x1, int y1, int x2, int y2, int r1, int r2) {
		if (Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) == (r1 + r2)) {
			return RoundType.TANGENT;
		}
		if (Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) < (r1 + r2)) {
			return RoundType.SEPARATE;
		}
		if (Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) > (r1 + r2)) {
			return RoundType.INTERSECT;
		}
		return null;
	}

	public static enum RoundType {
		INTERSECT, // 相交
		SEPARATE, // 相离
		TANGENT,// 相切
	}
}
