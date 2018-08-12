package jk.querynex;

public class Util {
	public static int safeInt(String s, Integer defVal) {
		return s.isInteger() ? s as Integer : defVal
	}
}
