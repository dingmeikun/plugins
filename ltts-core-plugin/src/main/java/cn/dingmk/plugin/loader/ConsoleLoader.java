package cn.dingmk.plugin.loader;

public class ConsoleLoader extends Loader
{
	public ConsoleLoader() {}
	
	public void loading(String message)
	{
		System.out.println("INFO:" + message);
	}
	
	public void error(String message, Throwable t)
	{
		System.out.println("ERROR:" + message);
		t.printStackTrace();
	}
	
	public void loaded(GlobalContext.SystemStatus status)
	{
		System.out.println(status);
	}
}