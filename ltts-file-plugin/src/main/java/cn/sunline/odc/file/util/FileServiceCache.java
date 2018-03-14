package cn.sunline.odc.file.util;

import java.util.HashMap;
import java.util.Map;

import cn.sunline.odc.file.model.BaseModel;

/**
 * <p>
 * 文件服务说明：服务协议缓存
 * </p>
 * 
 * @Author Dingmk
 *         <p>
 *         <li>2018年3月1日-上午10:00:00</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class FileServiceCache {

	private static FileServiceCache instance = null;
	
	private static final String DEFAULT_ID = "default";
	
	private static Map<String, BaseModel> fileProtocols = new HashMap<>();
	
	public static FileServiceCache get(){
		
		if(null == instance){
			instance = new FileServiceCache();
		}
		return instance;
	}
	
	/**
	 * 根据ID获取协议信息
	 * 
	 * @param protocolId 协议ID
	 * @return 
	 * @return 
	 * @return
	 */
	public BaseModel getProtocolInfoById(String protocolId){
		
		if(hasInfo(protocolId)){
			return fileProtocols.get(protocolId);
		}else{
			return getDefaultProtocolInfo();
		}
	}
	
	/**
	 * 获取默认协议信息
	 * 
	 * @return 
	 * @return
	 */
	public BaseModel getDefaultProtocolInfo(){
		
		return fileProtocols.get(DEFAULT_ID);
	}
	
	/**
	 * 获取全部文件协议信息
	 * 
	 * @return
	 */
	public Map<String, BaseModel> getAllFileService(){
		
		return fileProtocols;
	}
	
	/** 
     * 获取缓存数量 
     * 
     * @return 
     */  
    public int getSize(){
    	
        return fileProtocols.size();
    }
    
    /**
     * 是否存在一个协议信息
     * 
     * @param key
     * @return
     */
    private synchronized static boolean hasInfo(String key){
    	
    	return fileProtocols.containsKey(key);
    }
    
    /**
     * 设置协议信息
     * 
     * @param protocolId
     * @param info
     * @return 
     */
    public synchronized void setProtocolInfo(String protocolId, BaseModel info){
    	
    	if(fileProtocols.get(protocolId) == null) {
    		fileProtocols.put(protocolId, info);
    	}
    }
}
