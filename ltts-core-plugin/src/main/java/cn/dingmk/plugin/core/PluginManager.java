package cn.dingmk.plugin.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.dingmk.plugin.loader.Loader;
import cn.dingmk.plugin.model.PluginConf;
import cn.dingmk.plugin.plugin.IPlugin;
import cn.dingmk.plugin.utils.CoreUtil;

public class PluginManager {

	private static Logger BOOT_LOGGER = LogManager.getLogger(PluginManager.class);
	
	private static final List<PluginWrapper> plugins_enable = new ArrayList<>();
	
	
	public static void initPluginServices()
	{
		PluginConfManager.loadPluginConfs();
		_initPluginServices();
	}
	
	private static void _initPluginServices()
	{
		BOOT_LOGGER.info("所有插件初始化开始.");
	     
		Loader.getInstance().loading("所有插件初始化开始.");
	 
		plugins_enable.clear();
	     
		List<PluginWrapper> plugins = PluginConfManager.getPluginsOrdered();
		for (int i = 0; i < plugins.size(); i++) {
			PluginWrapper pluginConfWrapper = (PluginWrapper)plugins.get(i);
			PluginConf pluginConf = pluginConfWrapper.getPluginConf();
		       
			long start = System.currentTimeMillis();
			try {
				IPlugin plugin = pluginConfWrapper.getPluginActivatorObj();
		 
				boolean isInitSuccess = plugin.initPlugin();
		         
				if (isInitSuccess)
				{
					Loader.getInstance().loading(CoreUtil.formatForLog("插件[{}]初始化耗时：{} s", new Object[] { pluginConf.getDisplayName(), Double.valueOf((System.currentTimeMillis() - start) / 1000.0D) }));
		           
					plugins_enable.add(pluginConfWrapper);
				}
			} catch (Throwable e) {
				if (pluginConf.isEnable())
				{
					BOOT_LOGGER.error("初始化插件出错:id={}", e, pluginConf.getId());
		           
					if ((e instanceof RuntimeException))
						throw ((RuntimeException)e);
						throw new IllegalStateException("初始化插件出错:id=[" + pluginConf.getId() + "]" + e.getMessage(), e);
		         }
			}
		}
	}
	
	public static void startAllPluginServices()
	{
		for (PluginWrapper plugin : plugins_enable)
		{
			BOOT_LOGGER.info("插件[{}] ... 正在启动.", plugin.getPluginConf().getDisplayName());
			try {
				plugin.getPluginActivatorObj().startupPlugin();
				
				BOOT_LOGGER.info("插件[{}] ... 启动完成.", plugin.getPluginConf().getDisplayName());
	         
				Loader.getInstance().loading(CoreUtil.formatForLog("插件[{}] ... 启动完成.", new Object[] { plugin.getPluginConf().getDisplayName() }));
	       }
	       catch (Throwable e)
	       {
	    	   BOOT_LOGGER.error("插件[{}] ... 启动错误.", e, plugin.getPluginConf().getDisplayName());
	    	   
	    	   Loader.getInstance().loading(CoreUtil.formatForLog("插件[{}] ... 启动失败. {}", new Object[] { plugin.getPluginConf().getDisplayName(), e.getMessage() }));
	 
	    	   throw new RuntimeException(CoreUtil.formatForLog("插件[{}] ... 启动错误.", new Object[] { plugin.getPluginConf().getDisplayName() }), e);
	       }
		}
	 
		BOOT_LOGGER.info("所有插件启动完成.");
	     
		Loader.getInstance().loading("所有插件启动完成.");
	}
}
