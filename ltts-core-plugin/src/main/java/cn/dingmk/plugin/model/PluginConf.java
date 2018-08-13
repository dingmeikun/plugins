package cn.dingmk.plugin.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * 插件配置，对应 xxx.plugin.xml文件，解析插件配置信息
 * 对应使用的JAXB解析 plugin文件
 * 
 * @author dingmk
 */
@XmlRootElement(name="plugin")
@JSONType(ignores={"activatorObj"})
@XmlType(propOrder={"id", "displayName", "activator", "enable", "failOnInitError"})
public class PluginConf {

	private String id = "";
	
	private String displayName = "";
	
	private String activator = "";
	
	private boolean enable = true;
	
	private boolean failOnInitError = true;

	@XmlAttribute
	public String getId() {
		return this.id; 
	}
		
	public void setId(String id)
	{
		this.id = id;
	}
	
	@XmlAttribute
	public String getDisplayName() {
		return this.displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@XmlAttribute
	public boolean isEnable() {
		return this.enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	@XmlAttribute
	public boolean isFailOnInitError() {
		return this.failOnInitError;
	}
	
	public void setFailOnInitError(boolean failOnInitError) {
		this.failOnInitError = failOnInitError;
	}

	@XmlAttribute(name="activator")
	public String getActivator() {
		return this.activator;
	}

	public void setActivator(String activator) {
		this.activator = activator;
	}
	
}
