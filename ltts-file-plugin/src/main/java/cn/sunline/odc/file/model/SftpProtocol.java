package cn.sunline.odc.file.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import cn.sunline.ltts.frw.model.annotation.Index;

@Index
@XmlRootElement
public class SftpProtocol extends BaseModel{

	private String serverIp;
	private int serverPort;
    private String userName;
    private String password;
    private String filePath;
    private String keyPath;
    
    public SftpProtocol(){}
    
    @XmlAttribute
    public String getServerIp() {
		return serverIp;
	}
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	
	@XmlAttribute
	public int getServerPort() {
		return serverPort;
	}
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	
	@XmlAttribute
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@XmlAttribute
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@XmlAttribute
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	@XmlAttribute
	public String getKeyPath() {
		return keyPath;
	}
	public void setKeyPath(String keyPath) {
		this.keyPath = keyPath;
	}

}
