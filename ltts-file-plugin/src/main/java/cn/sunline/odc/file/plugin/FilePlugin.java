package cn.sunline.odc.file.plugin;

import java.util.ArrayList;
import java.util.List;

import cn.sunline.edsp.microcore.plugin.IPlugin;
import cn.sunline.ltts.core.api.exception.LttsServiceException;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.util.LttsCoreBeanUtil;
import cn.sunline.odc.file.comm.CommConfig;
import cn.sunline.odc.file.comm.SftpConfig;
import cn.sunline.odc.file.model.SftpProtocol;
import cn.sunline.odc.file.spi.impl.DefaultSftpImpl;
import cn.sunline.odc.file.util.FileServiceCache;

public class FilePlugin extends IPlugin{
	
	private static final BizLog bizlog = BizLogUtil.getBizLog(FilePlugin.class);
	private static DefaultSftpImpl defaultExtension = new DefaultSftpImpl();
	private static List<SftpProtocol> protocolConfigs = new ArrayList<>();
	private static final String DEFAULT_ID = "default";
	
	private static FilePlugin instance;
	
	private static CommConfig conf;
	private static SftpConfig sftpConf;
	
	public static FilePlugin get(){
		return instance;
	}

	@Override
	public boolean initPlugin() {
		
		instance = this;
		
		LttsCoreBeanUtil.getConfigManagerFactory().create(CommConfig.class, "server/server-manager.xml");
		LttsCoreBeanUtil.getConfigManagerFactory().create(SftpConfig.class, "server/server-sftp.xml");
        conf = LttsCoreBeanUtil.getConfigManagerFactory().getDefaultConfigManager().getConfig(CommConfig.class);
        sftpConf = LttsCoreBeanUtil.getConfigManagerFactory().getDefaultConfigManager().getConfig(SftpConfig.class);
        
        if(conf == null || sftpConf.getSftpProtocols() == null){
        	
        	bizlog.error("Cann't find the file server configuration!");
            throw new LttsServiceException("FilePlugin.initPlugin", "Cann't find the file server configuration!");
        }
        
        protocolConfigs.addAll(sftpConf.getSftpProtocols());
        
		return true;
	}

	@Override
	public void startupPlugin() {
		
		// 设置协议信息
		setConfigInfo(protocolConfigs, FileServiceCache.get(), defaultExtension);
	}

	@Override
	public void shutdownPlugin() {
		
	}
	
	/**
	 * 设置文件协议缓存信息
	 * 
	 * @param protocolConfigs
	 * @param fileServerCache
	 */
	private static void setConfigInfo(List<SftpProtocol> protocolConfigs, FileServiceCache fileServerCache, DefaultSftpImpl defaultExtension){
		for(SftpProtocol protocolConfig : protocolConfigs){
			if(null != protocolConfig.getId() && !"".equals(protocolConfig.getId())){
				if(conf.getDefaultProtocolId().equals(protocolConfig.getId())){
					fileServerCache.setProtocolInfo(DEFAULT_ID, protocolConfig);
				}else{
					fileServerCache.setProtocolInfo(protocolConfig.getId(), protocolConfig);
				}
				defaultExtension.addProtocolInfo(protocolConfig);
			}
		}
	}
}
