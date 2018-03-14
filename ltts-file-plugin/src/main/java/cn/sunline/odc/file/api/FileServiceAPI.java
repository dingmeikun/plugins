package cn.sunline.odc.file.api;

import cn.sunline.edsp.microcore.util.ExtensionUtil;
import cn.sunline.odc.file.model.BaseModel;
import cn.sunline.odc.file.spi.FileServiceExtension;
import cn.sunline.odc.file.util.FileServiceCache;

/**
 * <p>
 * 文件功能说明：文件服务API，作为调用客户端的入口
 * </p>
 * 
 * @Author dingmk
 *         <p>
 *         <li>2018年02月26日-上午10:11:09</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20180226 dingmk：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class FileServiceAPI {

	private static final String FILE_SERVICE_EXTENSION_POINT = "file.comm_server";
	private static final String FILE_SERVICE_EXTENSION_POINT_IMPL = ".impl";

	/**
	* @Author dingmk
	*         <p>
	*         <li>2018年02月26日-上午10:11:09</li>
	*         <li>功能说明：根据文件协议Id获取文件服务客户端</li>
	*         </p>
	* @param protocolId
	* @return
	*/
	public static <T> T createClient(String protocolId) {

		BaseModel protocolInfo = FileServiceCache.get().getProtocolInfoById(protocolId);

		FileServiceExtension fileServiceImpl = (FileServiceExtension) ExtensionUtil
				.getExtensionPointImpl(FILE_SERVICE_EXTENSION_POINT,
						protocolInfo.getProtocolType() + FILE_SERVICE_EXTENSION_POINT_IMPL);
		
		return fileServiceImpl.creatFileClient(protocolId);
	}
}
