package cn.sunline.odc.file.comm;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import cn.sunline.ltts.frw.model.annotation.Index;

@Index
@XmlRootElement(name = "CommConfig")
public class CommConfig {

	private String defaultProtocolId;
	
	public CommConfig() {}
	
	@XmlAttribute
	public String getDefaultProtocolId() {
		return defaultProtocolId;
	}

	public void setDefaultProtocolId(String defaultProtocolId) {
		this.defaultProtocolId = defaultProtocolId;
	}
	
}
