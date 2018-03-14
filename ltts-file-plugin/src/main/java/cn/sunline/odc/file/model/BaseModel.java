package cn.sunline.odc.file.model;

import javax.xml.bind.annotation.XmlAttribute;

public class BaseModel{
    
	private String id;
	private String protocolType;
	private int connTimeoutInMs;
	
	@XmlAttribute
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@XmlAttribute
	public String getProtocolType() {
		return protocolType;
	}
	public void setProtocolType(String protocolType) {
		this.protocolType = protocolType;
	}
	
	@XmlAttribute
	public int getConnTimeoutInMs() {
		return connTimeoutInMs;
	}
	public void setConnTimeoutInMs(int connTimeoutInMs) {
		this.connTimeoutInMs = connTimeoutInMs;
	}
}
