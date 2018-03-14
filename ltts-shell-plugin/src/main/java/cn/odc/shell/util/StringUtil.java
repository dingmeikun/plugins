package cn.odc.shell.util;


import cn.odc.shell.replacer.Replacement;

public class StringUtil {

	private static final String EMPTY = "";
	  
	  public static String replace(String content, Replacement replacement)
	  {
	    return replaceNonRegex(content, replacement.getToken(), replacement.getValue());
	  }
	  
	  private static String replaceNonRegex(String content, String token, String value)
	  {
	    if (isEmpty(content)) {
	      return content;
	    }
	    return content.replace(token, defaultString(value));
	  }
	  
	  public static boolean isNotEmpty(String content)
	  {
	    return (content != null) && (!"".equals(content));
	  }
	  
	  public static boolean isEmpty(String content)
	  {
	    return !isNotEmpty(content);
	  }
	  
	  public static void main(String[] args)
	  {
	    System.out.println(isEmpty(null) == true);
	    System.out.println(!isNotEmpty(null));
	    
	    System.out.println(!isEmpty("a"));
	    System.out.println(isNotEmpty("a") == true);
	  }
	  
	  public static String defaultString(String value)
	  {
	    return value == null ? "" : value;
	  }
}
