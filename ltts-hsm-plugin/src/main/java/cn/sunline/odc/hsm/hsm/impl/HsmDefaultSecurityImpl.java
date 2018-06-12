package cn.sunline.odc.hsm.hsm.impl;

import cn.sunline.ltts.core.api.exception.LttsServiceException;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.odc.hsm.client.HsmCommClient;
import cn.sunline.odc.hsm.constant.HsmConstant;
import cn.sunline.odc.hsm.hsm.HsmSecurity;
import cn.sunline.odc.hsm.model.HsmConfig;
import cn.sunline.odc.hsm.model.HsmPublicKeyBean;
import cn.sunline.odc.hsm.plugin.HsmPlugin;
import cn.sunline.odc.hsm.util.HsmStringUtil;

/**
 * <p>
 * 功能说明：
 *   平台常用安全校验方法·改进    			
 * </p>
 * 
 * @author heyong
 * @author wangtk
 * @author dingmk
 *         <p>
 *         <li>2017年5月3日-下午5:04:51</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>2017年5月3日 heyong：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>2018年6月8日 dingmk：重构</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class HsmDefaultSecurityImpl implements HsmSecurity {
    
    private static BizLog bizlog = BizLogUtil.getBizLog(HsmDefaultSecurityImpl.class);
    
    private HsmSecurity hsmSecurity;
    private static HsmCommClient comm = null;
    private static HsmConfig hsmConf = HsmPlugin.get().getConf();
    
    /** 依赖于HsmDefaultMessage实现，代理实现 */
    public HsmDefaultSecurityImpl(){
    	this.hsmSecurity = new HsmDefaultMessageImpl();
    }
    
    public HsmDefaultSecurityImpl(HsmSecurity hsmSecurity){
    	this.hsmSecurity = hsmSecurity;
    }
    
    @Override
	public String getPublicKey(String pksIndex) {
    	//生成头信息
    	String inputHeadMsg = hsmSecurity.GenPublicKey(pksIndex);
    	//生成报文头 
    	StringBuffer msgHdr = generateHead(inputHeadMsg);
    	//组装数据
    	String inputMsg = msgHdr + inputHeadMsg ;
    	
    	//process
    	String result = validationPCV(inputMsg);
    	
    	String CompareRes = result.substring(18, 20);
    	String keyData = result.substring(20);
    	HsmPublicKeyBean publicKey = new HsmPublicKeyBean(keyData);
    	if(HsmConstant.PENDING_STRING00.equals(CompareRes)){ 
    		return publicKey.getPublicKey();
    	}else{
    		bizlog.error("加密机校验失败", "");
    		throw new LttsServiceException("EsmDefaultSecurityImpl.getPublicKey", "异常");
    	}
	}
        
    @Override
    public String generateCvv(String cvk_type, String cardNo, String expiryDate, String serviceCode, String cvv_type) {
        
		if (cvv_type == HsmConstant.CVVTYPE_CVV2) {
			String expiryYY = expiryDate.substring(0, 2);
			String expiryMM = expiryDate.substring(2, 4);
			expiryDate = expiryMM + expiryYY;
		}
    	
        String keyIndex = hsmConf.getCvkIndex();

        // 截取后的数据 
        String inputHeadMsg = hsmSecurity.createMsg(cardNo, expiryDate, keyIndex, serviceCode);
        
        // 开始计算生成 head 数据
        StringBuffer msgHdr = generateHead(inputHeadMsg);
		
		// 拼接数据 开始校验
		String inputMsg = msgHdr + inputHeadMsg ;
        
        String result = validationPCV(inputMsg);
        //010100000006EE080200480F
        
        String CompareRes = result.substring(18, 20);
        // 组装数据 送到加密机
        if(HsmConstant.PENDING_STRING00.equals(CompareRes)){
        	return result.substring(20,23);
        }else{
        	bizlog.error("加密机校验失败", "");
        	throw new LttsServiceException("EsmDefaultSecurityImpl.generateCvv", "错误信息");
        }
    }

    @Override
    public boolean checkCvv(String cvk_type, String cardNo, String expiryDate, String serviceCode, String cvv_type, String checkCvv) {
    	
        //Handle CVV2 Generation / Verification
        if (cvv_type == HsmConstant.CVVTYPE_CVV2) {
        	String expiryYY = expiryDate.substring(0, 2);
        	String expiryMM = expiryDate.substring(2, 4);
        	expiryDate = expiryMM + expiryYY;
        }
        
        String keyIndex = hsmConf.getCvkIndex();
        
        // 截取后的数据 
        String inputHeadMsg = hsmSecurity.createMsg(cardNo, expiryDate, keyIndex, serviceCode, checkCvv);
        // 010100000019  EE08030002000162262701000000112102201000000000678F
        // 开始计算生成 head 数据
        StringBuffer msgHdr = generateHead(inputHeadMsg);
		
		// 拼接数据 开始校验
		String inputMsg = msgHdr + inputHeadMsg ;
        
        String result = validationPCV(inputMsg);
        bizlog.info("result>>>>>>>>>>>>>>>[%s]",result);
//        String CompareRes = result.substring(6, 8); (18,20 is the correct one)
        String CompareRes = result.substring(18, 20);
        // 组装数据 送到加密机
        if(HsmConstant.PENDING_STRING00.equals(CompareRes)){
        	return true;
        }else{
        	bizlog.error("加密机校验失败", "");
        	return false;
        }

    }
    
    // 加密密码
    @Override
    public String encryptPin(String accNO, String plainPin) {
        
        StringBuffer sb = new StringBuffer();
        String pinLength = HsmConstant.PENDING_STRING06;
		String genHeader = hsmSecurity.genHeader(HsmConstant.HSM_HEADEREE0600);
		String keyIndex = hsmConf.getCvkIndex();
        sb.append(genHeader); //CMD 
		sb.append(HsmConstant.PENDING_STRING00); //FM
		sb.append(pinLength); //PIN-Length
		sb.append(plainPin); // Pin
		sb.append(accNO); // ANB
		sb.append(keyIndex); //PPK-Spec
		String cmdStr = sb.toString();
        
		// 开始计算生成 head 数据
        StringBuffer msgHdr = generateHead(cmdStr);
		
		// 拼接数据 开始校验
		String inputMsg = msgHdr + cmdStr ;
        
        String result = validationPCV(inputMsg);

//        String CompareRes = result.substring(6, 8);
        String CompareRes = result.substring(18, 20);
        // 组装数据 送到加密机
        if(HsmConstant.PENDING_STRING00.equals(CompareRes)){
        	return HsmConstant.HSM_ENCRIPT_RESULT_WITHPIN;
        }else{
        	bizlog.error("加密机校验失败", "");
        	throw new LttsServiceException("EsmDefaultSecurityImpl.encryptPin", "错误信息");
        }
    }
    
    @Override
    public boolean checkSimplePassword(String accNo, String mobileNo, String idNo, String checkData1, String checkData2,
    		String pin_format, String pinBlock) {
        
        return true;
    }

    @Override
    public String generatePin(String  pvk_type, String cardNO, String pinVerificationKey, String zonePinKey, String pinBlock) {

        // 截取后的数据 
        String inputHeadMsg = hsmSecurity.createPinOffMsg(pinVerificationKey, cardNO, zonePinKey, pinBlock);
        
        // 开始计算生成 head 数据
        StringBuffer msgHdr = generateHead(inputHeadMsg);
		
		// 拼接数据 开始校验
		String inputMsg = msgHdr + inputHeadMsg ;
        
        String result = validationPCV(inputMsg);

//        String CompareRes = result.substring(6, 8);
        String CompareRes = result.substring(18, 20);
        
        // 组装数据 送到加密机
        if(HsmConstant.PENDING_STRING00.equals(CompareRes)){
        	return result.substring(20,32);
        }else{
        	bizlog.error("加密机校验失败", "");
        	throw new LttsServiceException("EsmDefaultSecurityImpl.generatePin", "错误信息");
        }
        
    }

    @Override
    public boolean checkPin(String  pvk_type, String cardNO, String pinBlock, String pinVerificationKey, String zonePinKey, String pinOffset) {
        
        // 截取后的数据 
        String inputHeadMsg = hsmSecurity.createPinMsg(pinVerificationKey, cardNO, zonePinKey, pinBlock, pinOffset);
        
        // 开始计算生成 head 数据
        StringBuffer msgHdr = generateHead(inputHeadMsg);
		
		// 拼接数据 开始校验
		String inputMsg = msgHdr + inputHeadMsg ;
        
        String result = validationPCV(inputMsg);

//        String CompareRes = result.substring(6, 8);
        String CompareRes = result.substring(18, 20);
        // PIN密文，PIN格式，账号，ZPK名称，模式 1:IBM PinOffset验证，PVK名称，PIN Offset， 检验标识
        // 组装数据 送到加密机
        if(HsmConstant.PENDING_STRING00.equals(CompareRes)){
        	return true;
        }else{
        	bizlog.error("加密机密码校验失败", "");
        	return false;
        }
    }

    @Override
    public boolean checkARQC(String mdk_type, String algorithmID, String cardNo, String atc, String arqcData, String arqc) {
        
    	return true;
    }

    /**
     * 校验ARQC并生成ARPC
     * @param E_KEYTYPE  mdk_type
     * @param algorithmID  01-3DES 04-SM4
     * @param cardNo  卡号
     * @param atc   离散过程因子(即55域中9F36)
     * @param arqcData  ARQC数据源
     * @param arqc      要校验的ARQC
     * @param arc       授权响应码(即8583中的39域，这里可默认为00)
     * @return arpc
     */
    @Override								
    public String checkARQCAndGenerateARPC(String mdk_type, String algorithmID,String cmdStr) {
        
		// 开始计算生成 head 数据
        StringBuffer msgHdr = generateHead(cmdStr);
        
        // 拼接数据 开始校验
		String inputMsg = msgHdr + cmdStr ;
        
        String result = validationPCV(inputMsg);
	        
        //Add logic to see what is the result
		String CompareRes = result.substring(18, 20);
		bizlog.info("result>>>>>>>>>>>>>>>[%s]",result);
		
		//CMD(3) + RC(1) + FILLER(1) + ARPC(8)
		//6+2+2+16
		if (HsmConstant.PENDING_STRING00.equals(CompareRes)) {
			if (result.length() < 38)
				return null;
			else
				return result.substring(22, 38);
		} else {
			return null;
		}
    }

    @Override
    public String translatePinOffsetWith2AccNo(String pvk_type, String accNo, String accNo1, String pinOffset) {
        
    	return "";
    }

    @Override
    public String translatePinBlockWith2AccNo(String accNo, String accNo1, String pinBlock) {
        
    	return "";
    }

    @Override
    public String encryptData(int encryptType, String plainData) {

    	return "";
    }

    @Override
    public String decryptData(int encryptType, String cipherData) {
        
    	return "";
    }
    
    @Override
    public String generateOfflinePin(String accNo) {
        
    	//数据组装
    	StringBuffer   msg =  new StringBuffer();//EE0E04 pin-generate  rand 
    	msg.append(HsmConstant.HSM_HEADEREE0E04);
    	msg.append(HsmConstant.PENDING_STRING00);
    	msg.append(HsmConstant.PENDING_STRING06);//PIN-length
    	msg.append(HsmConstant.PENDING_STRING01);//PIN-Block  Format option:01 10 13
    	msg.append(accNo);//ANB
    	msg.append(HsmConstant.PENDING_STRING11);//PPK-Spec
    	
    	comm = new HsmCommClient(Integer.valueOf(hsmConf.getHsmPort()), hsmConf.getHsmIp());
    	String resultstr = null;
    	try{
    		byte[] strMsgAfterSend = comm.send(HsmStringUtil.byteToBcd(msg.toString()));
            resultstr = HsmStringUtil.byteArrayToHexString(strMsgAfterSend); //Converts to HEX with with no spaces
        }catch(Exception ex){
        	bizlog.error("密码重置随机密码生成失败[%s]",ex);
		} finally {
			comm.closeSocket();
        }
    	
    	return resultstr;
        
    }
    
    /**
     *  process CVV verifiction
     *  
     * @param inputMsg
     * @return
     */
    public String validationPCV(String inputMsg){
    	
    	comm = new HsmCommClient(Integer.valueOf(hsmConf.getHsmPort()),hsmConf.getHsmIp());
    	
    	String resultstr = null;
    	try{
    		bizlog.info("Request Message:[%s]", inputMsg);
			byte[] out=HsmStringUtil.byteToBcd(inputMsg);
			bizlog.info("Message Length:[%s]", out.length);
			byte[] strMsgAfterSend = comm.send(out);
			bizlog.info("Receive Size:[%s]", strMsgAfterSend.length);
			String receiveMsg = HsmStringUtil.dump(strMsgAfterSend,0,strMsgAfterSend.length); //Converts to HEX with spaces every 4 char
			bizlog.info("resultSet:[%s]", receiveMsg);
            resultstr = HsmStringUtil.byteArrayToHexString(strMsgAfterSend); //Converts to HEX with with no spaces
            bizlog.info("resultStr:[%s]", resultstr);
            
        }catch(Exception except){
        	bizlog.error(except.getMessage(), except);
        	try {
	        	comm = new HsmCommClient(Integer.valueOf(hsmConf.getHsmPort()),hsmConf.getHsmIp());
	        	bizlog.info("Request Message:[%s]", inputMsg);
				byte[] out=HsmStringUtil.byteToBcd(inputMsg);
				bizlog.info("Message Length:[%s]", out.length);
				byte[] strMsgAfterSend = comm.send(out);
				bizlog.info("Receive Size:[%s]", strMsgAfterSend.length);
				String receiveMsg = HsmStringUtil.dump(strMsgAfterSend,0,strMsgAfterSend.length); //Converts to HEX with spaces every 4 char
				bizlog.info("resultSet:[%s]", receiveMsg);
	            resultstr = HsmStringUtil.byteArrayToHexString(strMsgAfterSend); //Converts to HEX with with no spaces
	            bizlog.info("resultStr:[%s]", resultstr);
        	} catch (Exception e) {
        		bizlog.info("RETRY FAILED");
        	}
		} finally {
			comm.closeSocket();
        }
		return resultstr;
    }
    
    /**
     * @Author wangtk
     *         <p>
     *         <li>2017年8月31日-下午1:29:19</li>
     *         <li>功能说明：加密机校验串头部生成</li>
     *         </p>
     * @param inputHeadMsg
     * @return
     */
    public StringBuffer generateHead(String inputHeadMsg){
    	//0101 00000019
    	StringBuffer msgHdr = new StringBuffer();
		int halfSize = inputHeadMsg.length() / 2;
		msgHdr.append(HsmConstant.VERIFY_STRING0101);
		msgHdr.append(HsmStringUtil.getPadLeft(Integer.toHexString(halfSize), 8, HsmConstant.PENDING_CHAR0));
		
		return msgHdr;
    }
   
	@Override
	public String translateRSABlock(String RSAIndex, String C, String P,
			String RN, String PPK, String CardNo) {
		String cmdStr = hsmSecurity.createMessage(RSAIndex, C, P, RN,
				PPK, CardNo);

		StringBuffer msgHdr = generateHead(cmdStr);
	        
	        // 拼接数据 开始校验
		String inputMsg = msgHdr + cmdStr;
		
		String result = validationPCV(inputMsg);
	        
		String CompareRes = result.substring(18, 20);
		//01010000000CEE301900DF500266422C8AB7
	        // 组装数据 送到加密机
		if (HsmConstant.PENDING_STRING00.equals(CompareRes)) {
			return result.substring(20);
		} else {
			bizlog.error("加密机校验失败", "");
			throw new LttsServiceException("EsmDefaultSecurityImpl.translateRSABlock", "错误信息");
		}
	}

	@Override
	public String createMsg(String cardNo, String expiry, String keyIndex,
			String serviceCode) {
		return this.hsmSecurity.createMsg(cardNo, expiry, keyIndex, serviceCode);
	}

	@Override
	public String createPinOffMsg(String pinVerificationKey, String cardNo,
			String zonePinKey, String pinBlock) {
		return this.hsmSecurity.createPinOffMsg(pinVerificationKey, cardNo, zonePinKey, pinBlock);
	}

	@Override
	public String GenPublicKey(String pksIndex) {
		return this.hsmSecurity.GenPublicKey(pksIndex);
	}

	@Override
	public String createMessage(String RSAIndex, String C, String P, String RN,
			String PPK, String CardNo) {
		return this.hsmSecurity.createMessage(RSAIndex, C, P, RN, PPK, CardNo);
	}

	@Override
	public String createMsg(String cardNo, String expiry, String keyIndex,
			String serviceCode, String cvv) {
		return this.hsmSecurity.createMsg(cardNo, expiry, keyIndex, serviceCode, cvv);
	}

	@Override
	public String createPinMsg(String pinVerificationKey, String cardNo,
			String zonePinKey, String pinBlock, String pinOffset) {
		return this.hsmSecurity.createPinMsg(pinVerificationKey, cardNo, zonePinKey, pinBlock, pinOffset);
	}

	@Override
	public String genHeader(String command) {
		return this.hsmSecurity.genHeader(command);
	}

	@Override
	public String genMsgHdr(StringBuffer headerString) {
		return this.hsmSecurity.genMsgHdr(headerString);
	}
}
