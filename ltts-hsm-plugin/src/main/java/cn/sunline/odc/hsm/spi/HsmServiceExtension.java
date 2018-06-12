package cn.sunline.odc.hsm.spi;

import cn.sunline.edsp.microcore.plugin.IReplaceExtension;

public interface HsmServiceExtension extends IReplaceExtension{

	/** 扩展点 {插件ID}.{扩展点ID} */
	public static final String POINT = "hsm.hsm_server";
	
	/**
	* @Author dingmk
	*         <p>
	*         <li>2018年06月10日-下午02:21:09</li>
	*         <li>功能说明：创建密码服务实例</li>
	*         </p>
	* @return 密码客户端实例
	*/
	public <T> T creatHsmSecurity();
}
