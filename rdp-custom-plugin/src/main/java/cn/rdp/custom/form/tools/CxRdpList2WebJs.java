package cn.rdp.custom.form.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cxsw.bean.CxNode;
import com.cxsw.json.JsonParseUtil;
import com.cxsw.model.utils.XmlParserUtil;
import com.cxsw.utils.FileUtil;


public class CxRdpList2WebJs 
{
	private static String filePattern = "*.list.xml";
	
	private static StringBuffer fileContent = new StringBuffer("var LIST_RESOURCE_DATA={};\r\n");
	
	public static void main(String[] args) {
		File dir = new File("H:\\交易服务平台\\P2Pws\\Rdp_Sys\\define\\resource\\list");
		String temp = "H:\\交易服务平台\\P2Pws\\Rdp_Sys\\temp";
		try {
			paserBaseFile(dir, temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * API入口
	 * @param dir
	 * @param targetPath
	 * @throws IOException
	 */
	public static void paserBaseFile(File dir, String targetPath) throws IOException {
		if(!dir.exists()){
			return ;
		}
		String target = targetPath + File.separator + "business" + File.separator + "resources" + File.separator + "commonlist.cn.js";
		FileUtil.delete(target); //新建前把它删除掉，以防内容重复冗余
		File file = new File(target); 
		List<File> fileList = new ArrayList<File>();
		collectFiles(fileList, dir);
		
		//Iterator collected
		for(File files: fileList){
			//执行生成文件路径
			String path = files.getAbsolutePath();
			convertFileStream(path.replace("\\\\", "/"));
		}
		//生成文件
		try {
			FileUtil.writeStringToFile(file, fileContent.toString(), "UTF-8", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 转换文件流
	 * @param listpath
	 */
	private static void convertFileStream(String listpath)
	{
		String keystr = "LIST_RESOURCE_DATA[\"%s\"]";
		String context = FileUtil.readFileToString(listpath);
		CxNode cxnode = XmlParserUtil.unpack(context);
		StringBuffer sb = new StringBuffer();
		for(CxNode node : cxnode.getChilds())
		{
			Map<String, String> attr = node.getAttributes();
			Map<Object,Object> obj = new LinkedHashMap<Object, Object>();
			obj.putAll(attr);
			List<Map<Object,Object>> lst = new ArrayList<Map<Object,Object>>();
			for(CxNode item : node.getChilds())
			{
				Map<Object,Object> itemobj = new LinkedHashMap<Object, Object>();
				itemobj.putAll(item.getAttributes());
				lst.add(itemobj);
			}
			obj.put("items", lst);
			String value = JsonParseUtil.format(obj);
			String key = String.format(keystr, node.getAttributes().get("id"));
			
			sb.append(key).append("=").append(value).append(";\r\n");
		}
		fileContent.append(sb);
	}
	
	/**
	 * 递归遍历指定文件目录
	 * @param fileList 文件集
	 * @param file 文件路径
	 * @throws IOException
	 */
	private static void collectFiles(List<File> fileList, File file)
			throws IOException {
		if (file.isFile()) {
			if (wildcardMatch(filePattern, file.getName())) {
				fileList.add(file);
			}
		} else {
			for (File files : file.listFiles()) {
				collectFiles(fileList, files);
			}
		}
	}
	
	/**   
     * 通配符匹配   
     * @param pattern 通配符模式   
     * @param str     待匹配的字符串   
     * @return    匹配成功则返回true，否则返回false   
     */    
    private static boolean wildcardMatch(String pattern, String str) {     
        int patternLength = pattern.length();     
        int strLength = str.length();     
        int strIndex = 0;     
        char ch;     
        for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {     
            ch = pattern.charAt(patternIndex);     
            if (ch == '*') {     
                //通配符星号*表示可以匹配任意多个字符     
                while (strIndex < strLength) {     
                    if (wildcardMatch(pattern.substring(patternIndex + 1),     
                            str.substring(strIndex))) {     
                        return true;     
                    }     
                    strIndex++;     
                }     
            } else if (ch == '?') {     
                //通配符问号?表示匹配任意一个字符     
                strIndex++;     
                if (strIndex > strLength) {     
                    //表示str中已经没有字符匹配?了。     
                    return false;     
                }     
            } else {     
                if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {     
                    return false;     
                }     
                strIndex++;     
            }     
        }     
        return (strIndex == strLength);     
    }

}
