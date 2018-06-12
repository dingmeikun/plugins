package cn.sunline.ltts.test;

import org.junit.Test;

import cn.sunline.edsp.microcore.Bootstrap;
import cn.sunline.odc.hsm.api.HsmServiceApi;
import cn.sunline.odc.hsm.client.HsmCommClient;
import cn.sunline.odc.hsm.hsm.HsmSecurity;
import cn.sunline.odc.hsm.model.HsmConfig;
import cn.sunline.odc.hsm.plugin.HsmPlugin;
import cn.sunline.odc.hsm.util.HsmStringUtil;

public class StartServerTest {
	
	static {
		System.setProperty("setting.file", "setting.dev.properties");
		System.setProperty("log4j.configurationFile", "ltts_log_dev.xml");

		System.setProperty("ltts.home", "");
		System.setProperty("ltts.log.home", "C:\\ltts");
		System.setProperty("ltts.vmid", "UnitTestApp");
		
		Bootstrap.main(new String[]{"start"});
	}
	
	@Test
	public void invokeHsmServerTest(){
		HsmConfig conf = HsmPlugin.get().getConf();
		HsmCommClient comm = new HsmCommClient(Integer.valueOf(conf.getHsmPort()), conf.getHsmIp());
		try{
			String inputMsg = "010100000017ee08020002009997040331918935941809101000000000";
			byte[] out=HsmStringUtil.byteToBcd(inputMsg);
			System.out.println("Message Length:"+out.length);
			
			byte[] strMsgAfterSend = comm.send(out); //Sends the message to ESM and stores the response to strMsgAfterSend
			System.out.println("Receive Size: "+strMsgAfterSend.length);
			String receiveMsg=HsmStringUtil.dump(strMsgAfterSend, 0, strMsgAfterSend.length); //Converts to HEX with spaces every 4 char
			System.out.println("result:["+receiveMsg+"]");
	      
	        String arrayRes = HsmStringUtil.byteArrayToHexString(strMsgAfterSend); //Converts to HEX with with no spaces
	        System.out.println("ArrayRes:["+arrayRes+"]");
		} catch (Exception except) {
			System.out.println("handle package Failed!");
		} finally {
			comm.closeSocket();
		}
	}
	
	@Test
	public void getPublicKeyTest(){
		HsmSecurity hsmSecurity = HsmServiceApi.creatHsmSecurity();
    	String publicKey = hsmSecurity.getPublicKey("63");
    	System.err.println("publicKey:" + publicKey);
	}
}
