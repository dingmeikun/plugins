package cn.sunline.odc.hsm.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import cn.sunline.ltts.core.api.exception.LttsServiceException;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.odc.hsm.constant.HsmConstant;
import cn.sunline.odc.hsm.util.HsmStringUtil;

/**
 * <p>
 * 功能说明：
 *   Hsm服务端模拟方法
 * </p>
 * 
 * @author dingmk
 *         <p>
 *         <li>2018年6月8日-下午2:04:51</li>
 *         <li>标记：修订内容</li>
 *         <li>2018年6月10日 dingmk：增加注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class HsmCommServer {

	private static BizLog bizlog = BizLogUtil.getBizLog(HsmCommServer.class);
	ServerSocket serverSocket = null;
	
	public HsmCommServer() {
		try {
			serverSocket = new ServerSocket(HsmConstant.LOCALHOST_PORT);
			bizlog.info("Opening Server at Port " + HsmConstant.LOCALHOST_PORT + "");
			bizlog.info("setup new socket .. " + serverSocket);
		} catch (IOException e) {
			bizlog.info("Could not listen on port: " + HsmConstant.LOCALHOST_PORT + ", " + e);
			System.exit(1);
		}
	}
	
	public HsmCommServer(int port) {
		try {
			serverSocket = new ServerSocket(port);
			bizlog.info("Opening Server at Port " + HsmConstant.LOCALHOST_PORT + "");
			bizlog.info("setup new socket .. " + serverSocket);
		} catch (IOException e) {
			bizlog.info("Could not listen on port: " + port + ", " + e);
			System.exit(1);
		}
	}

	public void startServer() throws Exception {
		Socket clientSocket = null;
			while (true) {
				try {
					clientSocket = serverSocket.accept();
					bizlog.info("Client Connected!" + clientSocket.getInetAddress());
				} catch (IOException e) {
					bizlog.error("Accept failed: " + HsmConstant.LOCALHOST_PORT + ", " + e);
					throw new LttsServiceException("EsmCommServer.startServer01", "ServerSocket Listener Exception!");
				}
				try {
					OutputStream outputToBack = clientSocket.getOutputStream();
					InputStream in = clientSocket.getInputStream();
					ByteArrayOutputStream result = new ByteArrayOutputStream();
					String outputLine;

					byte[] buf = new byte[1024];

					int len = 0;

					while ((len = in.read(buf, 0, buf.length)) != -1) {
						result.write(buf, 0, len);
						bizlog.debug(" ------receving-----");
						String inputString = HsmStringUtil.byteArrayToHexString(result.toByteArray());
						bizlog.debug("SERVER RECEIVING:" + inputString);
						HsmMsg esmMsg = new HsmMsg(inputString);
						esmMsg.setResponse(HsmConstant.PENDING_STRING00);
						outputLine = esmMsg.toString();
						bizlog.debug("SERVER RESPONDING:" + outputLine);
						byte[] out = HsmStringUtil.byteToBcd(outputLine);
						outputToBack.write(out);
						outputToBack.flush();
					}
				} catch (IOException e) {
					throw new LttsServiceException("EsmCommServer.startServer02", "Handler package Exception!");
				} finally {
					try {
						clientSocket.close();
					} catch (IOException e) {
						bizlog.error("Could not close client socket. " + e);
						throw new LttsServiceException("EsmCommServer.startServer03", "Could not close client socket!");
					}
				}
			}
	}
	
	public boolean closeSocket() {
		try {
			serverSocket.close();
			bizlog.info("socket closed successfully " + serverSocket);
			return true;
		} catch (Exception ex) {
			bizlog.info("error closing connection : " + ex);
			return false;
		}
	}

	public static void main(String[] args) {
		HsmCommServer comm = null;
		try {
			int port = HsmConstant.LOCALHOST_PORT; //Default
			if(args.length == 1) {
				port = Integer.parseInt(args[0]);
			} else {
				bizlog.debug("ESMCommServer usage: java -cp BosEsmTester.jar ESMCommServer <port>");
				bizlog.debug("No port provided, default to " + HsmConstant.LOCALHOST_PORT + "");
			}
			comm = new HsmCommServer(port);
			comm.startServer();
		} catch (Exception e) {
			bizlog.info("Start HsmServer failed!");
		} finally {
			comm.closeSocket();
		}
	}
}
