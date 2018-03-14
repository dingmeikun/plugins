package cn.sunline.odc.file.comm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import cn.sunline.ltts.frw.model.annotation.Index;
import cn.sunline.odc.file.model.SftpProtocol;

@Index
@XmlRootElement(name = "SftpConfig")
public class SftpConfig{
	
	private List<SftpProtocol> sftpProtocols;
	
	@XmlElements({@XmlElement(name="SftpProtocol", type=SftpProtocol.class)})
	public List<SftpProtocol> getSftpProtocols() {
		return this.sftpProtocols;
	}

	public void setSftpProtocols(List<SftpProtocol> sftpProtocols) {
		this.sftpProtocols = sftpProtocols;
	}

}
