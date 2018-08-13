package cn.dingmk.plugin.utils;

import java.io.IOException;
import java.net.URL;

import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class CoreUtil {

	private static final char DELIM_START = '{';
	private static final char DELIM_STOP = '}';
	
	public CoreUtil() {}
	
	public static RuntimeException wrapThrow(String format, Throwable e, String... args)
	{
		return new RuntimeException(formatForLog(format, args), e);
	}
	
	public static RuntimeException wrapThrow(String format, String... args) {
		throw new RuntimeException(formatForLog(format, args));
	}
	
	public static URL[] findResources(String pattern)
	{
		PathMatchingResourcePatternResolver rr = new PathMatchingResourcePatternResolver();
		try {
			org.springframework.core.io.Resource[] resources = rr.getResources(pattern);
			URL[] ret = new URL[resources.length];
			
			for (int i = 0; i < resources.length; i++) {
				ret[i] = resources[i].getURL();
			}
			return ret;
		} catch (IOException e) {
			throw new RuntimeException("classpath search " + pattern + " error", e);
		}
	}
	
	public static String formatForLog(String format, Object... args)
	{
		StringBuilder destSB = new StringBuilder();
		format(destSB, format, args);
		return destSB.toString();
	}
	
	public static Class classForName(String className) throws ClassNotFoundException {
		return Class.forName(className, true, Thread.currentThread().getContextClassLoader());
	}
	
	public static boolean isEmpty(String s) {
		if (s == null)
			return true;
		if (s.trim().length() == 0) {
			return true;
		}
		return false;
	}
	
	private static void format(StringBuilder destSB, String messagePattern, Object... arguments)
	{
		if ((arguments == null) || (arguments.length == 0)) {
			destSB.append(messagePattern);
			return;
		}
		int argsIndex = 0;
		for (int i = 0; i < messagePattern.length(); i++) {
			char c = messagePattern.charAt(i);
			if (('{' == c) && (messagePattern.charAt(i + 1) == '}')) {
				destSB.append(String.valueOf(arguments[argsIndex]));
				argsIndex++;
				i++;
			} else { 
				if (('{' == c) && (messagePattern.charAt(i + 1) != '}')) {
					throw new RuntimeException("messagePattern error");
				}
				destSB.append(c);
			}
		}
	}
}
