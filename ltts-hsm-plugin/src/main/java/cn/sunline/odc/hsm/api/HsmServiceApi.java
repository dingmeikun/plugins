package cn.sunline.odc.hsm.api;

import cn.sunline.edsp.microcore.util.ExtensionUtil;
import cn.sunline.odc.hsm.constant.HsmConstant;
import cn.sunline.odc.hsm.spi.HsmServiceExtension;

/**
* @Author dingmk
*         <p>
*         <li>2018年06月12日-上午10:11:09</li>
*         <li>功能说明：创建密码服务实例</li>
*         </p>
* @return 密码服务实例
*/
public class HsmServiceApi {

	@SuppressWarnings("unchecked")
	public static <T> T creatHsmSecurity() {
		HsmServiceExtension hsmServiceExtension = ExtensionUtil.getExtensionPointImpl(HsmConstant.HSM_SERVICE_EXTENSION_POINT,
				HsmConstant.HSM_SERVICE_EXTENSION_POINT_IMPL);
		return (T) hsmServiceExtension.creatHsmSecurity();
	}
}
