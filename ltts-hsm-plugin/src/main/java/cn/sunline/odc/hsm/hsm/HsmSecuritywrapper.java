package cn.sunline.odc.hsm.hsm;

import cn.sunline.ltts.core.api.exception.LttsServiceException;
import cn.sunline.odc.hsm.constant.HsmConstant;
import cn.sunline.odc.hsm.util.HsmStringUtil;

/**
 * <p>
 * 功能说明：
 *   适配Hsm加密接口，提供子类覆盖实现
 * </p>
 * 
 * @author dingmk
 *         <p>
 *         <li>2018年6月8日-下午2:04:51</li>
 *         <li>标记：修订内容</li>
 *         <li>2018年6月10日 dingmk：增加默认空实现抽象类</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public abstract class HsmSecuritywrapper implements HsmSecurity{

	@Override
	public String generateCvv(String cvk_type, String cardNo,
			String expiryDate, String serviceCode, String cvv_type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkCvv(String cvk_type, String cardNo, String expiryDate,
			String serviceCode, String cvv_type, String checkCvv) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String encryptPin(String accNO, String plainPin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkSimplePassword(String accNo, String mobileNo,
			String idNo, String checkData1, String checkData2,
			String pin_format, String pinBlock) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String generatePin(String pvk_type, String accNO,
			String pinVerificationKey, String zonePinKey, String pinBlock) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkPin(String pvk_type, String accNO, String pinBlock,
			String pinVerificationKey, String zonePinKey, String pinOffset) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkARQC(String mdk_type, String algorithmID,
			String cardNo, String atc, String arqcData, String arqc) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String checkARQCAndGenerateARPC(String mdk_type, String algorithmID,
			String cmdStr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String translatePinOffsetWith2AccNo(String pvk_type, String accNo,
			String accNo1, String pinOffset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String translatePinBlockWith2AccNo(String accNo, String accNo1,
			String pinBlock) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encryptData(int encryptType, String plainData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String decryptData(int encryptType, String cipherData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateOfflinePin(String accNo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPublicKey(String pksIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String translateRSABlock(String RSAIndex, String C, String P,
			String RN, String PPK, String CardNo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createMsg(String cardNo, String expiry, String keyIndex,
			String serviceCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createPinOffMsg(String pinVerificationKey, String cardNo,
			String zonePinKey, String pinBlock) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String GenPublicKey(String pksIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createMessage(String RSAIndex, String C, String P, String RN,
			String PPK, String CardNo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createMsg(String cardNo, String expiry, String keyIndex,
			String serviceCode, String cvv) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createPinMsg(String pinVerificationKey, String cardNo,
			String zonePinKey, String pinBlock, String pinOffset) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//BSE20162241
	@Override
	public String genHeader(String command){
		if( command.length() != 6 ) {
				throw new LttsServiceException("EsmDefaultSecurityImpl.genHeader", "Command length is not 6!");
		}
		return command;
	}
		
	//SAC20162511[S]
	@Override
	public String genMsgHdr(StringBuffer sb) {
		StringBuffer msgHdr = new StringBuffer();
		int halfSize = sb.length() / 2;
		msgHdr.append(HsmConstant.VERIFY_STRING0101);
		msgHdr.append(HsmStringUtil.getPadLeft(Integer.toHexString(halfSize), 8, HsmConstant.PENDING_CHAR0));
		return msgHdr.toString();
	}
	//SAC20162511[E]
}
