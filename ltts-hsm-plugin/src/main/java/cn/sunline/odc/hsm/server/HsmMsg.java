package cn.sunline.odc.hsm.server;

public class HsmMsg {

	private String header = "";
	private String response = "";
	private String cvv = "612f";
	public HsmMsg(String message) {
		header = message.substring(0, 18);
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	
	public String toString(){
		
		if(header.equalsIgnoreCase(HsmMsgClassification.EsmMsgType.EE0802.getCode())) {
			return header+response+cvv;	
		} else if (header.equalsIgnoreCase(HsmMsgClassification.EsmMsgType.E2.getCode())) {
			return header+response;
		} else if (header.equalsIgnoreCase(HsmMsgClassification.EsmMsgType.EE0803.getCode())) {
			return header+response;
		} else if (header.equalsIgnoreCase(HsmMsgClassification.EsmMsgType.EE0603.getCode())) {
			return header+response;
		} else {
			return header+response;
		}
		
	}
}
