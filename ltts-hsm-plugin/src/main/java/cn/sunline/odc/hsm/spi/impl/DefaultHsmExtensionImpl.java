package cn.sunline.odc.hsm.spi.impl;

import cn.sunline.odc.hsm.hsm.HsmSecurityFactory;
import cn.sunline.odc.hsm.spi.HsmServiceExtension;

public class DefaultHsmExtensionImpl implements HsmServiceExtension{

	@SuppressWarnings("unchecked")
	@Override
	public <T> T creatHsmSecurity() {
		return (T) HsmSecurityFactory.get().getHsmSecurity();
	}

}
