package cn.sunline.odc.ftp.comm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import cn.sunline.ltts.frw.model.annotation.Index;

@Index
@XmlRootElement(name = "FtpConfig")
public class FtpConfig{

	private List<FtpProtocol> ftpProtocols;
	
	@XmlElements({@XmlElement(name="FtpProtocol", type=FtpProtocol.class)})
	public List<FtpProtocol> getFtpProtocols() {
		return this.ftpProtocols;
	}

	public void setFtpProtocols(List<FtpProtocol> ftpProtocols) {
		this.ftpProtocols = ftpProtocols;
	}

}
