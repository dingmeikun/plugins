package cn.dingmk.plugin.core;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.dingmk.plugin.model.PluginConf;
import cn.dingmk.plugin.parser.XmlConfigManager;
import cn.dingmk.plugin.utils.CoreUtil;

public class PluginConfManager {
	
	private static final Logger PLUGIN_LOGGER = LogManager.getLogger(PluginConfManager.class);

	private static List<PluginWrapper> pluginsOrdered = new ArrayList<PluginWrapper>();
	
	private static Map<String, Object> globalPluginConfigs = new HashMap<>();
	
	private static XmlConfigManager xcm = new XmlConfigManager(new Class[] { PluginConf.class });
	
	public static void loadPluginConfs(){
		try
		{
			loadGlobalConf();
			
			URL[] resources = CoreUtil.findResources("classpath*:/plugin/*.plugin.xml");
			
			Map<String, URL> repeatCheck = new HashMap<>();
			for (URL resource : resources) {
				try {
					InputStream is = resource.openStream();
					PluginConf pluginConf = (PluginConf)xcm.load(is);
						
					if ((globalPluginConfigs != null) && (globalPluginConfigs.containsKey(pluginConf.getId()))) {
						if ("false".equals(globalPluginConfigs.get(pluginConf.getId()))) {
							PLUGIN_LOGGER.info("插件[{}]已经禁止启动", pluginConf.getId());
							continue;
						}
					} else {
						if (!pluginConf.isEnable()) {
							continue;
						}
					}
					if (repeatCheck.containsKey(pluginConf.getId())) {
						URL r = (URL)repeatCheck.get(pluginConf.getId());
						throw CoreUtil.wrapThrow("插件ID[{}]重复. 插件[{}]与插件[{}]", new String[] { pluginConf.getId(), r.getFile(), resource.getFile() });
					}
					repeatCheck.put(pluginConf.getId(), resource);
					
					
					PluginWrapper pluginWrapper = new PluginWrapper(pluginConf, resource);
						pluginsOrdered.add(pluginWrapper);
						is.close();
					}
				catch (Exception ex) {
					PLUGIN_LOGGER.error("加载插件[{}]失败", ex, resource.getFile());
					throw CoreUtil.wrapThrow("加载插件[{}]失败", ex, new String[] { resource.getFile() });
				}
			}
			repeatCheck.clear();
			
			//调用原生的Collections工具类的sort方法进行排序，采用装潢模式，增加AscComparator排序的功能，实现Comparator接口
			//Collections.sort(pluginsOrdered, new AscComparator());
			
		}
		catch (Throwable e) {
			PLUGIN_LOGGER.error("加载插件信息失败", e);
			throw CoreUtil.wrapThrow("加载插件信息失败", e, new String[0]);
		}
	}
	
	public static List<PluginWrapper> getPluginsOrdered() {
		return pluginsOrdered;
	}
	
	private static void loadGlobalConf()
	{
		String pluginGlobalConfFile = null;
		try {
			pluginGlobalConfFile = System.getProperty("edsp.plugin.global.conf.path", "plugin-global-conf.properties");
			
			PLUGIN_LOGGER.info("插件全局配置文件：" + pluginGlobalConfFile);
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(pluginGlobalConfFile);
			if (is == null)
			{
				PLUGIN_LOGGER.info("未找到插件全局配置文件[{}],系统将不加载改配置", pluginGlobalConfFile);
				return;
			}
			Properties pp = new Properties();
			pp.load(is);
			for (Object pluginId : pp.keySet()) {
			String temp = String.valueOf(pluginId);
			String id = temp.substring(0, temp.lastIndexOf("."));
			globalPluginConfigs.put(id, pp.get(pluginId));
		}
		is.close();
		} catch (Throwable e) {
			throw CoreUtil.wrapThrow("加载全局配置文件[{}]出错！", new String[] { pluginGlobalConfFile });
		}
	}
}
