package cn.sunline.odc.ftp.impl;

import java.util.HashMap;
import java.util.Map;

import cn.sunline.odc.file.spi.FileServiceExtension;
import cn.sunline.odc.ftp.comm.FtpProtocol;

public class FtpExtensionImpl implements FileServiceExtension{
	
	private static Map<String, FtpProtocol> protocolMap = new HashMap<String, FtpProtocol>();
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T creatFileClient(String protocolId) {
		return (T) new FtpFileClientImpl(protocolMap.get(protocolId));
	}
	
	public void addProtocolInfo(FtpProtocol protocol) {
		protocolMap.put(protocol.getId(), protocol);
	}

}
