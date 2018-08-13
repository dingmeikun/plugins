package cn.dingmk.plugin.plugin;

/**
 * 定义插件功能集合
 * 
 * @author dingmk
 */
public abstract class PluginService extends IPlugin{

	/**
	 * 抽象方法，不确定该方法的具体实现，采用抽象方法，使用上和不抽象的一样
	 * @return
	 */
	@Override
	public boolean initPlugin() {
		return false;
	}
	
	@Override
	public void shutdownPlugin() {
	}
	
	@Override
	public void startupPlugin() {
		System.out.println("PluginService has start!");
	}
}
