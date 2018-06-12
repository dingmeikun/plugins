package cn.sunline.odc.hsm.client;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import cn.sunline.ltts.core.api.exception.LttsServiceException;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.odc.hsm.constant.HsmConstant;

/**
 * <p>
 * 功能说明：
 *   Hsm客户端
 * </p>
 * 
 * @author dingmk
 *         <p>
 *         <li>2018年6月8日-下午2:04:51</li>
 *         <li>标记：修订内容</li>
 *         <li>2018年6月10日 dingmk：增加默认空实现抽象类</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class HsmCommClient {
	
	private static BizLog bizlog = BizLogUtil.getBizLog(HsmCommClient.class);
	protected Socket theSocket = null;
	
	public HsmCommClient() {
		try {
			theSocket.setSoTimeout(HsmConstant.CONNECT_TIMEOUT * 1000);
			theSocket = new Socket(HsmConstant.LOCALHOST_IP, HsmConstant.LOCALHOST_PORT);
		} catch (Exception e) {
			bizlog.error(e.getMessage(), e);
		}
	}
	
	public HsmCommClient(int port, String hostname) {
		try {
			theSocket = new Socket(hostname, port);
			theSocket.setSoTimeout(HsmConstant.CONNECT_TIMEOUT * 1000);
		} catch (Exception e) {
			bizlog.error(e.getMessage(), e);
		}
	}
	
	public boolean closeSocket(){
		try {
			theSocket.close();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
	
    public byte[] send(byte[] baos) throws Exception  {
    	try {
    		if (theSocket == null) {
    			throw new LttsServiceException("EsmCommClient.send", "Could not allocated a socket not ready");
    		}
    		// 可以捕获内存缓冲区的数据,转换成字节数组
    		ByteArrayOutputStream result = new ByteArrayOutputStream();
    		OutputStream outputToBack = theSocket.getOutputStream();

    		InputStream in = theSocket.getInputStream();
    		
    		outputToBack.write(baos);
    		outputToBack.flush();
    		byte[] buf = new byte[512];
    		int len = 0;
    		if ((len = in.read(buf, 0, buf.length)) != -1) {
            	result.write(buf, 0, len);
    		}

    		byte[] resultF=result.toByteArray();

    		return resultF;

    	} catch (Exception interEx) {
    		bizlog.error(interEx.getMessage(), interEx);
    		throw interEx;
    	}
    }


//	public static void main(String[] args) 
//	{
//		HsmCommClient comm = null;
//		try {
//
//			int port = HsmConstant.LOCALHOST_PORT; //Default
//			String inputMsg = "010100000017ee08020002009997040331918935941809101000000000";
//			//String inputMsg = "010100000017"+"EE0802"+"00"+"020001"+"010203040506070809000102"+"00060009";
//			//String inputMsg = "010100000037ee060300077ca3ba67a86a0ab1111bcc57b492ae62c7c67970dffb78d7058010023450197140200256250023450197146000000ffffff06";
//			//String inputMsg = "010100000038EE060200E2ADEDE0688198E411118E30EBEEAA3BBFCC845EE25EFE6CA573010948000000020111117974E49AD89B9A9E166EDDFD71CD1C4D";
//			//String inputMsg = "01010000015CE2256200091111111112060109081243617264204E756D62657220456E64696E670811103131313203330A3135313031360B0828435550205052455041494420434152442031202020202020202020202020202020202020202020200340063030303030310D0828202020202020202020202020202020202020202020202020202020202020202020202020202020200E082820202020202020202020202020202020202020202020202020202020202020202020202020202020101D2820202020202020202020202020202020202020202020202020202020202020202020202020202020111D2820202020202020202020202020202020202020202020202020202020202020202020202020202020";
//			//String inputMsg = "0101000000FCE2256200091111111112060103330A313531303136034006303030303031081110313131320B0828435550205052455041494420434152442031202020202020202020202020202020202020202020200D0828202020202020202020202020202020202020202020202020202020202020202020202020202020200E082820202020202020202020202020202020202020202020202020202020202020202020202020202020101D2820202020202020202020202020202020202020202020202020202020202020202020202020202020111D2820202020202020202020202020202020202020202020202020202020202020202020202020202020";
//			//String inputMsg = "010100000029EE06020055652ED2388A713A02002201094200000000011111C8BBF6801973A2065A0ED0317EE96D20";
//			//String inputMsg = "01010000001aEE0602009F504A31E0C4C0D50200220199900122920701020081";
//			//String inputMsg = "010100000017EE08020002000735648051385545261909201000000000";
//			//String inputMsg = "010100000017ee08020002009997040331918935941809101000000000";
//			String ipAddress = HsmConstant.LOCALHOST_IP;
//			if(args.length == 3) {
//				port = Integer.parseInt(args[1]);
//				ipAddress = args[0];
//			} else {
//				bizlog.info("ESMCommClientTester usage: java -cp BosEsmTester.jar ESMCommClientTester <ip address> <port> <cmdstring>");
//				bizlog.info("No ip provided, default to localhost");
//				bizlog.info("No port provided, default to " + HsmConstant.LOCALHOST_PORT + "");
//				bizlog.info("No ESM Command String provided, default to 010100000017ee08020002009997040331918935941809101000000000");
//			}
//			byte[] out=HsmStringUtil.byteToBcd(inputMsg);
//			bizlog.debug("Message Length:"+out.length);
//			comm = new HsmCommClient(port,ipAddress);  //Instantiate the connection to ESM
//			byte[] strMsgAfterSend = comm.send(out); //Sends the message to ESM and stores the response to strMsgAfterSend
//			bizlog.debug("Receive Size: "+strMsgAfterSend.length);
//			String receiveMsg=HsmStringUtil.dump(strMsgAfterSend,0,strMsgAfterSend.length); //Converts to HEX with spaces every 4 char
//			bizlog.debug("result:["+receiveMsg+"]");
//          
//            String arrayRes = HsmStringUtil.byteArrayToHexString(strMsgAfterSend); //Converts to HEX with with no spaces
//            bizlog.debug("ArrayRes:["+arrayRes+"]");
//		} catch (Exception e) {
//			bizlog.error("Handle Package Failed!", e, e.getMessage());
//		} finally {
//			comm.closeSocket();
//		}
//	}
}
