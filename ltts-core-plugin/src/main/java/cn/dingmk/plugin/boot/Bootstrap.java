package cn.dingmk.plugin.boot;

import cn.dingmk.plugin.core.PluginManager;

public class Bootstrap {
	
	public Bootstrap(){}
	
	public static void main(String[] args) {
		String command = "start";
		try{
			if(args.length > 0){
				command = args[args.length -1];
			}
			if(command.equals("start")){
				start();
			}
		}catch (Throwable t) {
			throw new RuntimeException(command + "error！ 启动失败！", t);
		}
	}
	
	private static void start() {
		
		PluginManager.initPluginServices();
		
		PluginManager.startAllPluginServices();
	}
}
