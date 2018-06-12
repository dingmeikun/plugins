package cn.sunline.odc.hsm.hsm.impl;

import cn.sunline.odc.hsm.constant.HsmConstant;
import cn.sunline.odc.hsm.hsm.HsmSecuritywrapper;
import cn.sunline.odc.hsm.util.HsmStringUtil;

/**
 * <p>
 * 功能说明：
 *   加密信息生成实现
 * </p>
 * 
 * @author dingmk
 *         <p>
 *         <li>2018年6月10日-下午2:04:51</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>2018年6月8日 dingmk：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class HsmDefaultMessageImpl extends HsmSecuritywrapper{

	@Override
	public String createMsg(String cardNo, String expiry, String keyIndex, String serviceCode ) {
		StringBuffer sb = new StringBuffer();
		sb.append(genHeader(HsmConstant.HSM_HEADEREE0802)); //CVV-GENERATE (EE0802)   FunctionCode
		sb.append(HsmConstant.PENDING_STRING00); //FunctionModifier(FM=00)
		sb.append(HsmConstant.PENDING_STRING02); //FORMAT
		sb.append(HsmConstant.PENDING_STRING00); //CVK
		sb.append(keyIndex); //CVKIDX
		sb.append(cardNo); //PAN
		sb.append(expiry); // YYMM
		sb.append(serviceCode); //   ServerCode
		sb.append("000000000"); //NUL5
		
		String cmdStr = sb.toString();
		return cmdStr;
	}
	
	@Override
	public String createPinOffMsg(String pinVerificationKey, String cardNo, String zonePinKey, String pinBlock){
		
//		String wsLst12Pan = cardNo.substring(cardNo.length()-12,cardNo.length());
		String wsLst12Pan = cardNo.substring(3, 15);
		//String wsValidData = wsLst12Pan.substring(0, wsLst12Pan.length()-1) + "N";
		String wsValidData = cardNo.substring(0, 16);
		StringBuffer sb = new StringBuffer();
		String pvka = pinVerificationKey.substring(0, 2); //SAC20162381
		sb.append(genHeader(HsmConstant.HSM_HEADEREE0604)); //CMD 
		sb.append(HsmConstant.PENDING_STRING00); //FM
		sb.append(pinBlock); //PIN-BLOCK
		sb.append(HsmConstant.PENDING_STRING02); //PPKI-VAR
		sb.append(HsmConstant.PENDING_STRING00); //PPKI-FORMAT		
		sb.append(zonePinKey); //PPKI
		sb.append(HsmConstant.PENDING_STRING01); //PFI
		sb.append(wsLst12Pan); //ANB
		sb.append(HsmConstant.PENDING_STRING02); //PVKI-VAR
		sb.append(HsmConstant.PENDING_STRING00); //PVKI-FORMAT
		sb.append(pvka); //PVKI		
		sb.append(wsValidData); //VALIDATION-DATA
//		sb.append(EsmStringUtil.getPadRight(pinOffset, 12, '0')); //OFFSET
//		sb.append(twoDigit.format(pinLen)); //CHECK-LEN
//		sb.append("06"); //CHECK-LEN
		String cmdStr = sb.toString();
		return cmdStr;
	}
	
	@Override
	public String GenPublicKey(String pksIndex){
		StringBuffer sb = new StringBuffer();
		sb.append(genHeader(HsmConstant.HSM_HEADEREE3000)); //CVV-GENERATE (EE3000)   FunctionCode
		sb.append(HsmConstant.PENDING_STRING00); //FunctionModifier(FM=00)
		sb.append(HsmConstant.PENDING_STRING02); //FORMAT
		sb.append(HsmConstant.PENDING_STRING00); //CVK
		sb.append(pksIndex); //PKSIDX
				
		String pubKeyStr = sb.toString();
		return pubKeyStr;
	}
	
	@Override
	public String createMessage(String RSAIndex, String C, String P, String RN, String PPK,String CardNo) {
		
		//Construct EPB
		StringBuffer epb = new StringBuffer();
		epb.append(HsmConstant.VERIFY_STRING0200);
		epb.append(RSAIndex);
		if(256 == C.length()/2) {
			epb.append(HsmConstant.PENDING_STRING8 + HsmStringUtil.int2hex2(C.length()/2));
		} else {
			epb.append(HsmStringUtil.int2hex2(C.length()/2));
			epb.append(HsmStringUtil.int2hex2(C.length()/2));
		}
		epb.append(C);
		epb.append(HsmConstant.PENDING_STRING18);
		epb.append(HsmConstant.PENDING_STRING5A5A);
		epb.append(HsmConstant.PENDING_STRING01);
		epb.append(HsmConstant.PENDING_STRING05);//how change 
		epb.append(HsmConstant.PENDING_STRING14);
		epb.append(HsmStringUtil.int2hex2(P.length()/2));
		epb.append(P);
		epb.append(HsmConstant.PENDING_STRINGA5A5);
		epb.append(HsmStringUtil.int2hex2(RN.length()/2));
		epb.append(RN);
		
		StringBuffer sb = new StringBuffer();
//		String msgHdr = "010100000019";
//		
//		sb.append(msgHdr); // MSGHDR
		sb.append(HsmConstant.HSM_HEADEREE3019); // CMD
		sb.append(HsmConstant.PENDING_STRING00);
		sb.append(epb.toString());
		sb.append(HsmConstant.VERIFY_STRING0200);
		sb.append(PPK);
		sb.append(HsmConstant.PENDING_STRING01);
		sb.append(CardNo.substring(3, 15));
		
		return sb.toString();
	}
	
	@Override
	public String createMsg(String cardNo, String expiry, String keyIndex, String serviceCode, String cvv) {
		StringBuffer sb = new StringBuffer();
//		String msgHdr = "010100000019";
//		sb.append(msgHdr); //MSGHDR
		sb.append(genHeader(HsmConstant.HSM_HEADEREE0803)); //CMD
		sb.append(HsmConstant.PENDING_STRING00); //FM
		sb.append(HsmConstant.PENDING_STRING02); //FORMAT
		sb.append(HsmConstant.PENDING_STRING00); //CVK
		sb.append(keyIndex); //CVKIDX
		//CVV-DATA (length 16 hex) = PAN + YYMM + CRDTYPE + NUL5
		sb.append(cardNo); //PAN
		sb.append(expiry); // YYMM
		sb.append(serviceCode); // CRDTYPE ServerCode
		sb.append("000000000"); //NUL5
		sb.append(cvv); //CVV
		//COBOL got below values
		sb.append("F"); //PAD
		//sb.append("00"); //NUL2
				
		String cmdStr = sb.toString();
		return cmdStr;
	}
	
	@Override
	public String createPinMsg(String pinVerificationKey, String cardNo, String zonePinKey, String pinBlock, String pinOffset){
		String wsLst12Pan = cardNo.substring(3, 15);
		//String wsValidData = wsLst12Pan.substring(0, wsLst12Pan.length()-1) + "N";
		String wsValidData = cardNo.substring(0, 16);

		StringBuffer sb = new StringBuffer();
		String pvka = pinVerificationKey.substring(0, 2); //SAC20162381
		/*
		 * If cardBrand.CUP
   		sb.append("010100000037"); //MSGHDR FOR CUP
		else
        sb.append(msgHdr); //MSGHDR 
		 */
//		if(brand.compareTo(CardBrand.CUP) == 0) {
//			sb.append("010100000037");
//		} else {
//			sb.append(msgHdr); //MSGHDR
//		}
		sb.append(genHeader(HsmConstant.HSM_HEADEREE0603)); //CMD 
		sb.append(HsmConstant.PENDING_STRING00); //FM
		sb.append(pinBlock); //PIN-BLOCK
		//Conversion no need
		//sb.append(StringUtil.byteArrayToHexString(pinBlock.getBytes())); //PIN-BLOCK //Byte2bcd
		
//		if(brand.compareTo(CardBrand.CUP) == 0) {
//			sb.append("11"); //PPKI-VAR
//			sb.append("11"); //PPKI-FORMAT		
//		} else if (route == ProcMsg.BaseInterface.Nac) {
//			sb.append("09"); //PPKI-VAR
//			sb.append("11"); //PPKI-FORMAT	 //Double length if POS
//		}else {
			sb.append(HsmConstant.PENDING_STRING02); //PPKI-VAR
			sb.append(HsmConstant.PENDING_STRING00); //PPKI-FORMAT		
//		}
		sb.append(zonePinKey); //PPKI
		sb.append(HsmConstant.PENDING_STRING01); //PFI
		sb.append(wsLst12Pan); //ANB
		sb.append(HsmConstant.PENDING_STRING02); //PVKI-VAR
		sb.append(HsmConstant.PENDING_STRING00); //PVKI-FORMAT
		sb.append(pvka); //PVKI		
		sb.append(wsValidData); //VALIDATION-DATA
		sb.append(HsmStringUtil.getPadRight(pinOffset, 12, HsmConstant.PENDING_CHAR0)); //OFFSET
		sb.append(HsmConstant.PENDING_STRING06); //CHECK-LEN
		String cmdStr = sb.toString();
		return cmdStr;
	}
	
}
