package cn.dingmk.plugin.temp;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import cn.dingmk.plugin.plugin.PluginService;

public class pluginTemp {
	
	private URLClassLoader urlClassLoader;

	public pluginTemp(List<Plugin> plugins) throws MalformedURLException{
		initPlugin(plugins);
	}

	private void initPlugin(List<Plugin> plugins) throws MalformedURLException {
		int size = plugins.size();
		URL[] urls = new URL[size];
		
		for(int i = 0; i < size; i++) {
			Plugin plugin = plugins.get(i);
			String filePath = plugin.getJar();

			urls[i] = new URL("file:" + filePath);
		}
		
		// jar包路径变为文件数组，创建一个URLClassLoader
		urlClassLoader = new URLClassLoader(urls);
	}
	
	public PluginService getInstance(String classname) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		Class<?> clazz = urlClassLoader.loadClass(classname);
		Object instance = clazz.newInstance();
		
		return (PluginService) instance;
	}
}
