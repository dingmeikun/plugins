package cn.dingmk.plugin.loader;

public class GlobalContext
{
	public static final String plugin_global_conf_path = "edsp.plugin.global.conf.path";
	public static final String loader_clazz = "edsp.loader.clazz";
	public static final String log4j_conf_path = "log4j.configurationFile";
	private static GlobalContext instance = new GlobalContext();
	
	public GlobalContext() { 
		this.status = SystemStatus.stopped; 
	}
	
	public static GlobalContext get() {
		return instance;
	}
	
	public SystemStatus getStatus() {
		return this.status;
	}
	
	public synchronized void setStatus(SystemStatus status) {
		this.status = status;
	}
	
	private volatile SystemStatus status;
	
	public static enum SystemStatus
	{
		failed("SystemStatus:Error"), 
		
		stopping("SystemStatus:Stopping"), 
		
		stopped("SystemStatus:Stopped"), 
		
		starting("SystemStatus:Starting"), 
		
		started("SystemStatus:Started");
		
		private String name;
		
		private SystemStatus(String name) {
			this.name = name;
		}
		
		public String toString()
		{
			return this.name;
		}
	}
}