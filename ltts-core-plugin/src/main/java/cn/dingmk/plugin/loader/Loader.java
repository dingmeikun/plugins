package cn.dingmk.plugin.loader;

public abstract class Loader
{
	private static Loader instance;

	public Loader() {}

	public static Loader getInstance()
	{
		if (instance != null) {
			return instance;
		}
		try {
			String loaderImpl = System.getProperty("edsp.loader.clazz");
			if (loaderImpl != null) {
			instance = (Loader)Class.forName(loaderImpl).newInstance();
			} else {
			instance = new ConsoleLoader();
			}
		} catch (Throwable e) {
			throw new RuntimeException("init Loader failed", e);
		}
		return instance;
	}

	public abstract void loading(String paramString);
	
	public abstract void error(String paramString, Throwable paramThrowable);
	
	public abstract void loaded(GlobalContext.SystemStatus paramSystemStatus);
}