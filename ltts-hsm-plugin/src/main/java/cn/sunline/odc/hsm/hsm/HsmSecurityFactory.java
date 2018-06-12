package cn.sunline.odc.hsm.hsm;

import cn.sunline.odc.hsm.hsm.impl.HsmDefaultSecurityImpl;

/**
 * 
 * <p>
 * 功能说明：
 *   安全管理工厂，方便后续扩展    			
 * </p>
 * 
 * @author heyong
 *         <p>
 *         <li>2017年5月3日-下午5:07:26</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>2017年5月3日 heyong：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class HsmSecurityFactory {

	private static HsmSecurity hsmSecurity = null;
    private static HsmSecurityFactory instance;
    
    private HsmSecurityFactory() {
        hsmSecurity = new HsmDefaultSecurityImpl(); // 对外代理类
    }
    
    public static HsmSecurityFactory get() {
    	if(null == instance){
    		instance = new HsmSecurityFactory();
    	}
        return instance;
    }
    
    public HsmSecurity getHsmSecurity() {
        return hsmSecurity;
    }
}
