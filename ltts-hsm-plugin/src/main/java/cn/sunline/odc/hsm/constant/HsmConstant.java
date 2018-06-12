package cn.sunline.odc.hsm.constant;

/**
 * <p>
 * 文件功能说明：全局常量定义
 * </p>
 * 
 * @Author dingmk
 *         <p>
 *         <li>2018年06月08日-下午15:02:22</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20180608 dingmk：创建</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class HsmConstant {
	
	/** 密码服务扩展点ID */
	public static final String HSM_SERVICE_EXTENSION_POINT = "hsm.hsm_server";
	
	/** 密码服务扩展ID */
	public static final String HSM_SERVICE_EXTENSION_POINT_IMPL = "hsm.impl";
	
	/** 转加密时，如果无账号，则默认使用12个0 */
    public static final String DEFAULT_ACCTNO = "000000000000";
	
	/** CUP */
	public static final String CUP_KEYSET_ID = "CUP";
	
	/** VISA */
	public static final String VISA_KEYSET_ID = "VISA";
	
	/** MASTER */
	public static final String MASTER_KEYSET_ID = "MASTER";
	
    /** CVK */
    public static final String ISSKEY_ID_CVK = "CVK";
    
    /** ICVK */
    public static final String ISSKEY_ID_ICVK = "ICVK";
    
    /** CVK2 */
    public static final String ISSKEY_ID_CVK2 = "CVK2";
    
    /** ZPK */
    public static final String ISSKEY_ID_ZPK = "ZPK";
    
    /** PVK */
    public static final String ISSKEY_ID_PVK = "PVK";
    
    /** OPVK */
    public static final String ISSKEY_ID_OFFL_PVK = "OPVK";
    
    /** EDK */
    public static final String ISSKEY_ID_EDK = "EDK";
    
    /** ZEK */
    public static final String ISSKEY_ID_ZEK = "ZEK";
    
    /** MDK-AC */
    public static final String ISSKEY_ID_MDK_AC = "MDK-AC";
	
	/** CVV类型 */
	public static final String CVVTYPE_CVV = "1";
	
	/** CVV2类型 */
	public static final String CVVTYPE_CVV2 = "2";
	
	/** ICVV类型 */
	public static final String CVVTYPE_ICVV = "3";
	
	/** DES加密类型 */
	public static final String KEYTYPE_DES = "DES";
	
	/** MS加密类型 */
	public static final String KEYTYPE_MS = "MS";
	
	/** DES和MS加密 */
	public static final String KEYTYPE_BOTH = "BOTH";
	
	/** PIN卡号格式 */
	public static final String PINFORMAT_WITHACCTNO = "1";
	
	/** PIN无卡号格式 */
	public static final String PINFORMAT_WITHOUTACCTNO = "2";
	
	/** 默认本地ip */
	public static final String LOCALHOST_IP = "127.0.0.1";
	
	/** 默认本地port */
	public static final int LOCALHOST_PORT = 4444;
	
	/** 加密机连接超时时间 */
	public static final int CONNECT_TIMEOUT = 60;
	
	/** pending String '0' */
	public static final String PENDING_STRING0 = "0";
	
	/** pending String '8' */
	public static final String PENDING_STRING8 = "8";
	
	/** pending String '00' */
	public static final String PENDING_STRING00 = "00";
	
	/** pending String '01' */
	public static final String PENDING_STRING01 = "01";
	
	/** pending String '02' */
	public static final String PENDING_STRING02 = "02";
	
	/** pending String '05' */
	public static final String PENDING_STRING05 = "05";
	
	/** pending String '06' */
	public static final String PENDING_STRING06 = "06";
	
	/** pending String '11' */
	public static final String PENDING_STRING11 = "11";
	
	/** pending String '14' */
	public static final String PENDING_STRING14 = "14";
	
	/** pending String '18' */
	public static final String PENDING_STRING18 = "18";
	
	/** pending String '5A5A' */
	public static final String PENDING_STRING5A5A = "5A5A";
	
	/** pending String 'A5A5' */
	public static final String PENDING_STRINGA5A5 = "A5A5";
	
	/** 校验串'0101' */
	public static final String VERIFY_STRING0101 = "0101";
	
	/** 映射码'0100' */
	public static final String VERIFY_STRING0100 = "0100";
	
	/** 映射码'0200' */
	public static final String VERIFY_STRING0200 = "0200";
	
	/** FunctionCode:EE0600 */
	public static final String HSM_HEADEREE0600 = "EE0600";
	
	/** FunctionCode:EE0E04 */
	public static final String HSM_HEADEREE0E04 = "EE0E04";
	
	/** FunctionCode:EE0603 */
	public static final String HSM_HEADEREE0603 = "EE0603";
	
	/** FunctionCode:EE0604 */
	public static final String HSM_HEADEREE0604 = "EE0604";
	
	/** FunctionCode:EE0802 */
	public static final String HSM_HEADEREE0802 = "EE0802";
	
	/** FunctionCode:EE0803 */
	public static final String HSM_HEADEREE0803 = "EE0803";
	
	/** FunctionCode:EE3019 */
	public static final String HSM_HEADEREE3019 = "EE3019";
	
	/** FunctionCode:EE3000 */
	public static final String HSM_HEADEREE3000 = "EE3000";
	
	/** PIN加密结果 */
	public static final String HSM_ENCRIPT_RESULT_WITHPIN = "a39gd5";
	
	/** pending char 0 */
	public static final char PENDING_CHAR0 = '0';
	
}
