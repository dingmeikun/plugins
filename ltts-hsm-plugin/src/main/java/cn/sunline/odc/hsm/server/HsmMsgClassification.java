package cn.sunline.odc.hsm.server;

public interface HsmMsgClassification {
	
	public static interface HsmMsgTypeIf {
	}

	public enum EsmMsgType implements HsmMsgTypeIf {
		 EE0802("EE0802"), E2("E2"), EE0803("EE0803"), EE0603("EE0603"), EE0701("EE0701"),
		 EE0800("EE0800"), EE0702("EE0702");
		
		private final String code;

		EsmMsgType(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	}
}
