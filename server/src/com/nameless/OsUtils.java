package com.nameless;

final class OsUtils
{
	private static String OS = null;
	private static String getOsName()
	{
		if(OS == null) OS = System.getProperty("os.name");
		return OS;
	}
	public static boolean isWindows()
	{
		return getOsName().startsWith("Windows");
	}
}
