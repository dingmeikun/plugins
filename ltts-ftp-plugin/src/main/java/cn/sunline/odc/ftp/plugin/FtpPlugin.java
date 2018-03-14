package cn.sunline.odc.ftp.plugin;

import java.util.ArrayList;
import java.util.List;

import cn.sunline.edsp.microcore.plugin.IPlugin;
import cn.sunline.ltts.core.api.exception.LttsServiceException;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.util.LttsCoreBeanUtil;
import cn.sunline.odc.file.util.FileServiceCache;
import cn.sunline.odc.ftp.comm.FtpConfig;
import cn.sunline.odc.ftp.comm.FtpProtocol;
import cn.sunline.odc.ftp.impl.FtpExtensionImpl;

public class FtpPlugin extends IPlugin{
	
	private static BizLog bizlog = BizLogUtil.getBizLog(FtpPlugin.class);
	private static List<FtpProtocol> protocolConfigs = new ArrayList<FtpProtocol>();
	private static FtpExtensionImpl ftpExtension = new FtpExtensionImpl();
	
	private static FtpPlugin instance;
	
	private static FtpConfig ftpConf;
	
	public static FtpPlugin get(){
		return instance;
	}

	@Override
	public boolean initPlugin() {
		instance = this;
		
		LttsCoreBeanUtil.getConfigManagerFactory().create(FtpConfig.class, "server/server-ftp.xml");
		ftpConf = LttsCoreBeanUtil.getConfigManagerFactory().getDefaultConfigManager().getConfig(FtpConfig.class);
		
		if(ftpConf.getFtpProtocols() == null){
			bizlog.error("Cann't find the file server configuration!");
			throw new LttsServiceException("FtpPlugin.initPlugin", "Cann't find the file server configuration!");
        }
		
		protocolConfigs.addAll(ftpConf.getFtpProtocols());
		return true;
	}

	@Override
	public void startupPlugin() {
		
		// 设置协议信息
		setConfigInfo(protocolConfigs, FileServiceCache.get(), ftpExtension);
	}
	
	@Override
	public void shutdownPlugin() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 设置文件协议缓存信息
	 * 
	 * @param protocolConfigs
	 * @param fileServerCache
	 */
	private static void setConfigInfo(List<FtpProtocol> protocolConfigs, FileServiceCache fileServerCache, FtpExtensionImpl ftpExtension){
		for(FtpProtocol protocolConfig : protocolConfigs){
			if(null != protocolConfig.getId() && !"".equals(protocolConfig.getId())){
				fileServerCache.setProtocolInfo(protocolConfig.getId(), protocolConfig);
				ftpExtension.addProtocolInfo(protocolConfig);
			}
		}
	}

}
