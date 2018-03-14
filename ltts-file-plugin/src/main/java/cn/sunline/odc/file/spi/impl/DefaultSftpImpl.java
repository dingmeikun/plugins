package cn.sunline.odc.file.spi.impl;

import java.util.HashMap;
import java.util.Map;

import cn.sunline.odc.file.client.impl.SftpFileClientImpl;
import cn.sunline.odc.file.model.SftpProtocol;
import cn.sunline.odc.file.spi.FileServiceExtension;

/**
 * <p>
 * Sftp默认实现：文件服务插件扩展点默认实现
 * </p>
 * 
 * @Author Dingmk
 *         <p>
 *         <li>2018年3月1日-上午10:00:00</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class DefaultSftpImpl implements FileServiceExtension{
	
	private static Map<String, SftpProtocol> protocolMap = new HashMap<>();
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T creatFileClient(String protocolId) {
		return (T) new SftpFileClientImpl(protocolMap.get(protocolId));
	}
	
	public void addProtocolInfo(SftpProtocol protocol) {
		protocolMap.put(protocol.getId(), protocol);
	}
	
}
