package cn.sunline.odc.hsm.hsm;

/**
 * 
 * <p>
 * 功能说明：
 *   加密平台常用方法接口定义    			
 * </p>
 * 
 * @author heyong
 * @author dingmk
 *         <p>
 *         <li>2017年5月4日-上午9:11:30</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>2017年5月4日 heyong：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public interface HsmSecurity {
    
    /**
     * 生成CVV
     * @param cvk_type
     * @param cardNo  卡号
     * @param expiryDate YYMM(4位)
     * @param serviceCode
     * @param cvv_type
     * @return
     */
    String generateCvv(String cvk_type, String cardNo, String expiryDate, String serviceCode, String cvv_type);
    
    /**
     * 校验CVV
     * @param cvk_type
     * @param cardNo  卡号
     * @param expiryDate YYMM(4位)
     * @param serviceCode
     * @param cvv_type
     * @param checkCvv 要校验的CVV
     * @return
     */
    boolean checkCvv(String cvk_type, String cardNo, String expiryDate, String serviceCode, String cvv_type, String checkCvv);
    
    /**
     * 生成加密密码
     * @param accNO 凭证号、卡号或账号
     * @param plainPin  明文
     * @param pin_format
     * @return
     */
    String encryptPin(String accNO, String plainPin);
    
    /**
     * 弱密码检查
     * @param accNo   凭证号、卡号或账号
     * @param mobileNo  手机号
     * @param idNo   证件号
     * @param pinBlock  
     * @return
     */
    boolean checkSimplePassword(String accNo, String mobileNo, String idNo, String checkData1, String checkData2, 
    		String pin_format, String pinBlock);
    
    /**
     * 生成密码，贷记卡使用PinOffset密码
     * @param pvk_type
     * @param accNO 凭证号、卡号或账号
     * @param pinBlock
     * @param pin_format
     * @return
     */
    String generatePin(String  pvk_type, String accNO, String pinVerificationKey, String zonePinKey, String pinBlock);
    
    /**
     * 校验密码
     * @param pvk_type
     * @param accNO 凭证号、卡号或账号
     * @param pinBlock 上送的密码,要校验的密码
     * @param pin_format
     * @param pinOffset 账户中落地的密码
     * @return
     */
    boolean checkPin(String  pvk_type, String accNO, String pinBlock, String pinVerificationKey, String zonePinKey, String pinOffset);
    
    /**
     * 校验ARQC
     * @param E_KEYTYPE  mdk_type
     * @param algorithmID  01-3DES 04-SM4
     * @param cardNo 卡号
     * @param atc  离散过程因子(即55域中9F36)
     * @param arqcData  ARQC数据源
     * @param arqc  要校验的ARQC
     * @return
     */
    boolean checkARQC(String mdk_type, String algorithmID, String cardNo, String atc, String arqcData, String arqc);
    
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
    String checkARQCAndGenerateARPC(String mdk_type, String algorithmID,String cmdStr);
    
    /**
     *  将accNo加密生成的PinOffset转化成accNo1加密生成的PinOffset
     * @param E_KEYTYPE  pvk_type
     * @param accNo  凭证号、卡号或账号
     * @param accNo1  凭证号、卡号或账号
     * @param pinOffset  由accNo加密生成的PinOffset
     * @return  accNo1加密生成的PinOffset
     */
    String translatePinOffsetWith2AccNo(String pvk_type, String accNo, String accNo1, String pinOffset);
    
    /**
     * 将accNo加密生成的PinBlock转化成accNo1加密生成的PinBlock
     * @param accNo   凭证号、卡号或账号
     * @param accNo1   凭证号、卡号或账号
     * @param pinBlock  由accNo加密生成的PinBlock
     * @return  accNo1加密生成的PinBlock
     */
    String translatePinBlockWith2AccNo(String accNo, String accNo1, String pinBlock);
    
    /**
     * 加密数据数据
     * @param encryptType  1-敏感数据 2-磁条数据
     * @param plainData
     * @return
     */
    String encryptData(int encryptType,  String plainData);
    
    /**
     * 解密数据数据
     * @param encryptType  1-敏感数据 2-磁条数据
     * @param plainData
     * @return
     */
    String decryptData(int encryptType, String cipherData);
    
    /**
     * 获取随机产生的脱机密码
     * @param accNO 凭证号、卡号或账号
     * @return  加密密文
     */
    String generateOfflinePin(String accNo) ;

    /**
     * 获取公共秘钥
     * @param pksIndex
     * @return
     */
    String getPublicKey(String pksIndex);
    
    /**
     * Translate RSA pin block to PPK pin block
     * @param RSAIndex - rsa index
     * @param C - RSA encrypted PIN block
     * @param P - PKCN value
     * @param RN - Random number
     * @param PPK - Zone Pin Key
     * @param CardNo - Cardholder Number
     * @return
     */
    String translateRSABlock(String RSAIndex, String C, String P, String RN, String PPK,String CardNo);
    
    /**
     * 生成组装报文信息
     * @param cardNo
     * @param expiry
     * @param keyIndex
     * @param serviceCode
     * @return
     */
    String createMsg(String cardNo, String expiry, String keyIndex, String serviceCode);
    
    /**
     * 生成pin offset
     * @param pinVerificationKey
     * @param cardNo
     * @param zonePinKey
     * @param pinBlock
     * @return
     */
    String createPinOffMsg(String pinVerificationKey, String cardNo, String zonePinKey, String pinBlock);
    
    /**
     * 生成公共秘钥
     * @param pksIndex
     * @return
     */
    String GenPublicKey(String pksIndex);
    
    /**
     * 
     * @param RSAIndex
     * @param C
     * @param P
     * @param RN
     * @param PPK
     * @param CardNo
     * @return
     */
    String createMessage(String RSAIndex, String C, String P, String RN, String PPK,String CardNo);
    
    /**
     * 生成报文数据
     * @param cardNo
     * @param expiry
     * @param keyIndex
     * @param serviceCode
     * @param cvv
     * @return
     */
    String createMsg(String cardNo, String expiry, String keyIndex, String serviceCode, String cvv);
    
    /**
     * 生成header数据
     * @param pinVerificationKey
     * @param cardNo
     * @param zonePinKey
     * @param pinBlock
     * @param pinOffset
     * @return
     */
    String createPinMsg(String pinVerificationKey, String cardNo, String zonePinKey, String pinBlock, String pinOffset);
    
    /**
     * 校验HSM报文的报文头
     * @param command Function code
     * @return
     */
    String genHeader(String command);
    
    /**
     * 组装发往HSM报文的报文头
     * @param sb
     * @return
     */
    String genMsgHdr(StringBuffer sb);
}
