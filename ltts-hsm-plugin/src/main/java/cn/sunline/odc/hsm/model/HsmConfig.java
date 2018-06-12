package cn.sunline.odc.hsm.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import cn.sunline.ltts.frw.model.annotation.Index;

@Index
@XmlRootElement(name = "HsmConfig")
public class HsmConfig {
    
    private String hsmIp;
    private String hsmPort;
    private String cvkIndex;
	private String iwkIndex;
    private String pwkIndex;
    private String ppkIndex;
    private String rsaIndex;
    
    @XmlAttribute
    public String getHsmIp() {
        return hsmIp;
    }
    public void setHsmIp(String hsmIp) {
        this.hsmIp = hsmIp;
    }
    
    @XmlAttribute
    public String getHsmPort() {
        return hsmPort;
    }
    public void setHsmPort(String hsmPort) {
        this.hsmPort = hsmPort;
    }
    
    @XmlAttribute
    public String getCvkIndex() {
        return cvkIndex;
    }
    public void setCvkIndex(String cvkIndex) {
        this.cvkIndex = cvkIndex;
    }
    
    @XmlAttribute
    public String getIwkIndex() {
		return iwkIndex;
	}
	public void setIwkIndex(String iwkIndex) {
		this.iwkIndex = iwkIndex;
	}
	
	@XmlAttribute
	public String getPwkIndex() {
		return pwkIndex;
	}
	public void setPwkIndex(String pwkIndex) {
		this.pwkIndex = pwkIndex;
	}
	
	@XmlAttribute
	public String getPpkIndex() {
		return ppkIndex;
	}
	public void setPpkIndex(String ppkIndex) {
		this.ppkIndex = ppkIndex;
	}
	
	@XmlAttribute
	public String getRsaIndex() {
		return rsaIndex;
	}
	
	public void setRsaIndex(String rsaIndex) {
		this.rsaIndex = rsaIndex;
	}
	
}
