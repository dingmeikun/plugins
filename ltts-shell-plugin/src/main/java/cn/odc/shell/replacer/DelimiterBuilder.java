package cn.odc.shell.replacer;

public class DelimiterBuilder {

	private String start = "${";
	  private String end = "}";
	  private static DelimiterBuilder defInstance = new DelimiterBuilder();
	  
	  public DelimiterBuilder() {}
	  
	  public DelimiterBuilder(String start, String end)
	  {
	    this.start = start;
	    this.end = end;
	  }
	  
	  public static DelimiterBuilder defInstance()
	  {
	    return defInstance;
	  }
	  
	  public String apply(String token)
	  {
	    if ((token == null) || (token.length() == 0)) {
	      return token;
	    }
	    return String.format("%s%s%s", new Object[] { this.start, token, this.end });
	  }
}
