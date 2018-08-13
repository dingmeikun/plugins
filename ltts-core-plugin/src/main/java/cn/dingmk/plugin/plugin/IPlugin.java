package cn.dingmk.plugin.plugin;

import cn.dingmk.plugin.model.PluginContext;

/**
 * 定义顶层插件服务接口
 * @author dingmk
 *
 */
public abstract class IPlugin {
	
	protected PluginContext context;

	public abstract boolean initPlugin();
	
	public abstract void startupPlugin();
	
	public abstract void shutdownPlugin();
}
