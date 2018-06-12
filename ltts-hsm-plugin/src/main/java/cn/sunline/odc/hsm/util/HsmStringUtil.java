package cn.sunline.odc.hsm.util;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import cn.sunline.ltts.biz.global.CommUtil;

/**
 * <p>
 * 功能说明：
 *   Hsm数据格式转换api
 * </p>
 * 
 * @author dingmk
 *         <p>
 *         <li>2018年6月8日-下午2:04:51</li>
 *         <li>标记：修订内容</li>
 *         <li>2018年6月10日 dingmk：添加注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class HsmStringUtil {
	public static String byteArrayToHexString(byte in[]) {
		byte ch = 0x00;
		int i = 0;
		if (in == null || in.length <= 0)
			return null;

		String pseudo[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
		StringBuffer out = new StringBuffer(in.length * 2);

		while (i < in.length) {
			ch = (byte) (in[i] & 0xF0); // Strip off high nibble
			ch = (byte) (ch >>> 4);
			// shift the bits down
			ch = (byte) (ch & 0x0F);
			// must do this is high order bit is on!
			out.append(pseudo[(int) ch]); // convert the nibble to a String
											// Character
			ch = (byte) (in[i] & 0x0F); // Strip off low nibble
			out.append(pseudo[(int) ch]); // convert the nibble to a String
											// Character
			i++;
		}
		String rslt = new String(out);
		return rslt;
	}

	public static String dump(byte[] data, int pos, int len) {
		StringBuffer sb = new StringBuffer();
		for (int i = pos; i < pos + len; i++) {
			int value = (data[i] < 0 ? data[i] + 256 : data[i]);
			String hex = Integer.toHexString(value);
			if (hex.length() == 0) {
				hex = "00";
			} else if (hex.length() == 1) {
				hex = "0" + hex;
			}
			sb.append(hex.toUpperCase() + " ");
		}
		return sb.toString();
	}

	// 转换成BCD码格式
	public static byte[] byteToBcd(String inputMsg) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// StringTokenizer st = new StringTokenizer(inputMsg.trim());
		//如果 inputMsg 为奇数 末位补0
		int inputMsgLen = inputMsg.length();
		int mode = inputMsgLen%2;
		if (mode != 0) inputMsgLen += 1;
		inputMsg = CommUtil.rpad(inputMsg, inputMsgLen, "0");
		
		for (int i = 0; i < inputMsg.length(); i++) {
			String val = inputMsg.substring(i, i + 2);
			int val1 = Integer.parseInt(val, 16);
			baos.write(val1);
			i++;
		}

		byte[] out = baos.toByteArray();
		return out;
	}
	
	//BCD编码按每8位截取送加密机后补位0满8位
	public static byte[] fullBcd(byte[] inputMsg) {
        // 创建一个空的8位字节数组（默认值为0）  
        byte[] arrB = new byte[8];
        // 将原始字节数组转换为8位  
        for (int i = 0; i < inputMsg.length && i < arrB.length; i++) {  
            arrB[i] = inputMsg[i];  
        }     
		return arrB;
	}
	
    public static String getPadLeft(String msg, int size, char padChar) {
    	return getPadLeft(msg, size, padChar, 'L');
    }
    public static String getPadLeft(String msg, int size, char padChar, char leftOrRight) {
//    	StringBuilder buf = getInitializedString(padChar, size);
    	int strLength = 0;
    	if( msg != null ) {
	    	msg = msg.trim();
	    	strLength = msg.length();
	    	if( strLength == size )
	    		return msg;
	        if( strLength > size ) {
	        	if(leftOrRight == 'L')
	        		return msg.substring(0,size);
	        	else
	        		return msg.substring(strLength-size);
	        }
        }
    	else
    		msg = "";
    	char[] arr = new char[size-strLength];
    	Arrays.fill(arr, padChar);
        StringBuilder buf = new StringBuilder(size);
    	buf.append(arr);
    	buf.append(msg);
        return buf.toString();
    }
    
    public static String getPadRight(String msg, int size, char padChar) {
//    	StringBuilder buf = getInitializedString(padChar, size);
    	int strLength = 0;
    	if( msg != null ) {
	    	msg = msg.trim();
	        strLength = msg.length();
	        if( strLength == size )
	        	return msg;
	        if( strLength > size )
	        	return msg.substring(0,size);
    	}
    	else
    		msg = "";
        StringBuilder buf = new StringBuilder(msg);
    	char[] arr = new char[size-strLength];
    	Arrays.fill(arr, padChar);
    	buf.append(arr);
        return buf.toString();
    }
    
	public static String int2hex2(Integer i) {
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toHexString(i));
		if (sb.length() < 2) {
			sb.insert(0, '0'); // pad with leading zero if needed
		}
		String hex = sb.toString();
		return hex;
	}
	
	public  static String genMsgHdr(StringBuffer sb) {
		StringBuffer msgHdr = new StringBuffer();
		int halfSize = sb.length() / 2;
		msgHdr.append("0101");
		msgHdr.append(HsmStringUtil.getPadLeft(Integer.toHexString(halfSize), 8, '0'));
		return msgHdr.toString();
	}
	
}
