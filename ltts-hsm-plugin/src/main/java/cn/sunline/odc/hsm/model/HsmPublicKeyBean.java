package cn.sunline.odc.hsm.model;

public class HsmPublicKeyBean {

	private String publicKey = "";
	private String exponent = "";
	
	public HsmPublicKeyBean(String data) {
		publicKey = data.substring(10,266);
		exponent = data.substring(data.length()-20 ,data.length()-16);
	}
	
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public String getExponent() {
		return exponent;
	}
	public void setExponent(String exponent) {
		this.exponent = exponent;
	}
}
