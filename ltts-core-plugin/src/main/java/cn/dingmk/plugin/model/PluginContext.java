package cn.dingmk.plugin.model;


public class PluginContext {
	
	private PluginConf confModel;

	public PluginContext(PluginConf confModel) {
		this.confModel = confModel;
	}

	public PluginConf getConfModel() {
		return this.confModel;
	}
}