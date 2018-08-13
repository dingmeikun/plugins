package cn.dingmk.plugin.plugin.impl;

import cn.dingmk.plugin.plugin.PluginService;


public class DmkPlugin extends PluginService{

	@Override
	public boolean initPlugin() {
		// TODO 解析对象到对用的model
		System.out.println("dmk插件初始化了！！");
		return true;
	}

	@Override
	public void shutdownPlugin() {
		super.shutdownPlugin();
	}

	@Override
	public void startupPlugin() {
		System.out.println("dmk插件启动了！！");
	}
	
}
