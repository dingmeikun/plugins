package cn.dingmk.plugin.core;

import java.lang.reflect.Field;
import java.net.URL;

import cn.dingmk.plugin.model.PluginConf;
import cn.dingmk.plugin.model.PluginContext;
import cn.dingmk.plugin.plugin.IPlugin;

/**
 * 插件包装类，包装插件配置
 * @author dingmk
 */
public class PluginWrapper {

	private PluginConf pluginConf;
	private URL url;
	private IPlugin pluginActivatorObj;
	
	public PluginWrapper(PluginConf pluginConf, URL url) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException, SecurityException{
		this.pluginConf	= pluginConf;
		
		if(null == pluginConf.getActivator()){
			throw new RuntimeException("Activator can not be null!");
		}
		
		Class<?> type = Class.forName(pluginConf.getActivator(), true, Thread.currentThread().getContextClassLoader());
		if(!IPlugin.class.isAssignableFrom(type)){
			throw new RuntimeException("插件[{" + pluginConf.getId() + "}]的activator必须是cn.sunline.ltts.core.plugin.IPlugin的子类");
		}
		
		PluginContext ctx = new PluginContext(pluginConf);
		this.pluginActivatorObj = ((IPlugin)type.newInstance());
		
		Field field = IPlugin.class.getDeclaredField("context");
		field.setAccessible(true);
		field.set(this.pluginActivatorObj, ctx);
		
		this.url = url;
	}

	public PluginConf getPluginConf() {
		return pluginConf;
	}

	public void setPluginConf(PluginConf pluginConf) {
		this.pluginConf = pluginConf;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public IPlugin getPluginActivatorObj() {
		return pluginActivatorObj;
	}

	public void setPluginActivatorObj(IPlugin pluginActivatorObj) {
		this.pluginActivatorObj = pluginActivatorObj;
	}
	
}
