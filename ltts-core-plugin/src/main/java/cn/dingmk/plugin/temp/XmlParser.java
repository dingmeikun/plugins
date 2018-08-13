package cn.dingmk.plugin.temp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XmlParser {

	public static List<Plugin> getPlugins() throws Exception {
		
		List<Plugin> pluginlist = new ArrayList<>();
		
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(new File("/plugin/plugin.xml"));
		Element root = document.getRootElement();
		List<Plugin> plugins = root.elements("plugin");
		for(Plugin plugin : plugins){
			Element element = (Element) plugin;
			Plugin pl = new Plugin();
			pl.setName(element.elementText("name"));
			pl.setJar(element.elementText("jar"));
			pluginlist.add(pl);
		}
		
		return pluginlist;
	}
}
