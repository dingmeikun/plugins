package cn.sunline.odc.hsm.plugin;

import cn.sunline.edsp.microcore.plugin.IPlugin;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.util.LttsCoreBeanUtil;
import cn.sunline.odc.hsm.model.HsmConfig;

public class HsmPlugin extends IPlugin {
	
	private static final BizLog bizlog = BizLogUtil.getBizLog(HsmPlugin.class);
	
	private static HsmPlugin instance;
	private HsmConfig conf;
	
	public static HsmPlugin get(){
        return instance;
    }

	@Override
	public boolean initPlugin() {
		instance = this;
        
        LttsCoreBeanUtil.getConfigManagerFactory().create(HsmConfig.class, "server/server-hsm.xml");
        conf = LttsCoreBeanUtil.getConfigManagerFactory().getDefaultConfigManager().getConfig(HsmConfig.class);
        
        if(conf == null){
            bizlog.equals("Cann't find the HsmConfig configuration!");
            return false;
        }
        return true;
	}

	@Override
	public void shutdownPlugin() {
		
	}

	@Override
	public void startupPlugin() {
		
	}

	public HsmConfig getConf() {
        return conf;
    }

    public void setConf(HsmConfig conf) {
        this.conf = conf;
    }
}
