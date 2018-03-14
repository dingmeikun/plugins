package cn.sunline.odc.ftp.comm;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import cn.sunline.ltts.frw.model.annotation.Index;
import cn.sunline.odc.file.model.BaseModel;

@Index
@XmlRootElement
public class FtpProtocol extends BaseModel{

	private String serverIp;
	private int serverPort;
    private String userName;
    private String password;
    private String filePath;
    private int retryTime;
    private int retryInterval;
    private int dataTimeoutInMs;
    
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
	public int getRetryTime() {
		return retryTime;
	}
	public void setRetryTime(int retryTime) {
		this.retryTime = retryTime;
	}
	
	@XmlAttribute
	public int getRetryInterval() {
		return retryInterval;
	}
	public void setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval;
	}
	
	@XmlAttribute
	public int getDataTimeoutInMs() {
		return dataTimeoutInMs;
	}
	public void setDataTimeoutInMs(int dataTimeoutInMs) {
		this.dataTimeoutInMs = dataTimeoutInMs;
	}

}
