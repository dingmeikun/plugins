package cn.sunline.odc.file.spi;

import cn.sunline.edsp.microcore.plugin.IReplaceExtension;

public interface FileServiceExtension extends IReplaceExtension{

	// 扩展点 {插件ID}.{扩展点ID}
	public static final String POINT = "file.comm_server";
	
	public <T> T creatFileClient(String protocolId);
	
}
