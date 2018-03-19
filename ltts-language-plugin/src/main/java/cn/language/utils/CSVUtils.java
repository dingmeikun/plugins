package cn.langauge.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import cn.sunline.ltts.config.template.Compiler;
import cn.sunline.ltts.core.api.model.ModelFile;
import cn.sunline.ltts.frw.model.resource.PatternModelFilesLoader;
import cn.sunline.ltts.frw.model.util.ModelFilesLoader;
import cn.sunline.ltts.frw.model.util.XmlConfigManager;

import com.csvreader.CsvReader;

/**
 * CSV处理API
 * 
 * @author dmk
 * 
 */
public class CSVUtils {
	
	private static final String FILE_PATTERN = "*.csv";
	private static final String SQL_PREFIX = ".SQL";
	public static final LinkedHashMap<String, String> header = new LinkedHashMap<String, String>();
	
	/**
	 * 生成CVS文件
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static File createCSVFile(List<LinkedHashMap<String ,String>> exportData, LinkedHashMap<String ,String> headMap, String outPutPath, String fileName) {
		File csvFile = null;
		BufferedWriter csvFileOutputStream = null;
		try {
			File file = new File(outPutPath);
			if (!file.exists()) {
				file.mkdir();
			}
			// 定义文件名格式并存储
			csvFile = new File(outPutPath + fileName);
			// UTF-8 正确读取分隔符","
			csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "UTF-8"), 1024);
			// 写入文件头部
			for(Iterator iterHead = headMap.entrySet().iterator(); iterHead.hasNext();){
				Entry nextData = (Entry) iterHead.next();
				csvFileOutputStream.write((String) nextData.getValue() != null ? (String) nextData.getValue() : "");
				if(iterHead.hasNext()){
					csvFileOutputStream.write(",");
				}
			}
			csvFileOutputStream.newLine();

			// 写入文件内容
			for(Iterator iterData = exportData.iterator(); iterData.hasNext();){
				int headCount = 0;
				while(iterData.hasNext()){
					Map map = (LinkedHashMap<String, String>) iterData.next();
					for (String key : headMap.keySet()) { //keySet : longName en cn ...
						if(map.get(key) != null){
							csvFileOutputStream.write(map.get(key).equals("") ? "" : map.get(key).toString());
							if(++ headCount < headMap.size()){ //是否还有head数据
								csvFileOutputStream.write(",");
							}
						}
					}
					if(headCount == headMap.size()){ //是否head装载完
						csvFileOutputStream.newLine();
						headCount = 0;
					}
				}
			}
//			for (Iterator iterList = exportData.iterator(); iterList.hasNext();) {
//				Object row = (Object) iterList.next();
//				for (Iterator iterData = headMap.entrySet().iterator(); iterData.hasNext();) {
//					Entry nextData = (Entry) iterData.next();
//					//BeanUtils.getProperty(obj, key) 从obj中取key对应的value
//					String dataValue = BeanUtils.getProperty(row, (String) nextData.getKey()) == null ? "" : 
//						BeanUtils.getProperty(row, (String) nextData.getKey());
//					csvFileOutputStream.write(dataValue);
//					if (iterData.hasNext()) {
//						csvFileOutputStream.write(",");
//					}
//				}
//				if (iterList.hasNext()) {
//					csvFileOutputStream.newLine();
//				}
//			}
			csvFileOutputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				csvFileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return csvFile;
	}
	
	/**
	 * 读取表文件内容生成SQL脚本
	 * @param type
	 * @param filePath
	 * @param outPath
	 * @return
	 */
	public static void GenTableData2SQL(String type, String filePath, String outPath){
		File dir = new File(filePath);
		List<File> fileList = new ArrayList<File>();
		collectFiles(fileList, dir);
		for(File file: fileList){
			String filename = file.getName().substring(0, file.getName().indexOf(".csv"));
			StringBuffer resultString = new StringBuffer("delete from " + filename + ";\n");
			File outFile = new File(outPath + File.separator + filename + SQL_PREFIX);
			outFile.delete();//删除原文件
			try {
				//读取的Windows环境下的文件，win7默认编码是GBK
				InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "gbk");
				CsvReader creader = new CsvReader(reader, ',');
				String headString = "";
				while(creader.readHeaders()){ 
					for(String head: creader.getHeaders()){
						headString = headString + "," + head;
					}
					//System.out.println("headString: " + headString.substring(1));
					while(creader.readRecord()){
						String dataString = "";
						if(creader.getCurrentRecord() != 0){
							//System.err.println(creader.getCurrentRecord());
							String rowData = creader.getRawRecord();
							String[] dataSet = rowData.split(",");
							for(String data: dataSet){
								dataString = dataString+ ",'" + data + "'";
							}
							//System.out.println("dataString: " + dataString.substring(1));
							resultString.append("insert into " + filename + "("+ headString.substring(1) +")" + " values(" + dataString.substring(1) + ");\n");
							//System.out.println(resultString.toString());
							//System.out.println("insert into " + file.getName() + "("+ head +")" + "values(" + rawRecord + ");");
						}
					}
				}
				creader.close();
			} catch (IOException e) {
				e.printStackTrace(); 
			}
			//写入文件
			try {
				writeStringToFile(outFile, resultString.toString(), "UTF-8", true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获取指定CSV文件数据
	 * 
	 * @param file 指定的具体schema文件路径
	 * @return
	 */
	public static LinkedHashMap<String, Object> readSchemaData(String file){
		LinkedHashMap<String, Object> content = new LinkedHashMap<String, Object>();
		LinkedHashMap<String, Object> header = new LinkedHashMap<String, Object>();
		List<LinkedHashMap<String, String>> data = new ArrayList<LinkedHashMap<String, String>>();
		try{
			InputStreamReader stream = new InputStreamReader(new FileInputStream(file), "utf-8");
			CsvReader reader = new CsvReader(stream, ',');
			while(reader.readHeaders()){
				String[] hades =  reader.getHeaders();
				for(String head : hades){
					header.put(head, head);
				}
				//保存CSV文件头
				content.put("head", header);
				while(reader.readRecord()){
					String rowRecords = reader.getRawRecord();
					String[] rowRecode = rowRecords.split(",");
					List<String> recorde = Arrays.asList(rowRecode);
					for(int idx = 0; idx < hades.length; idx ++){
						LinkedHashMap<String, String> rowData = new LinkedHashMap<String, String>();
						rowData.put(hades[idx], idx > recorde.size() - 1 ? "" : recorde.get(idx));
						data.add(rowData);
					}
				}
			}
		}catch (IOException e) {
			e.printStackTrace(); 
		}
		content.put("content", RmRepeat(data));
		return content;
	}
	
	/**
	 * 获取CSV文件对应翻译语种数据
	 * 
	 * @param file
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, Map<String, String>> readLanguageData(String[] file){
		Map<String, Map<String, String>> languageData = new LinkedHashMap<String, Map<String, String>>();
		List<String[]> csvString = new ArrayList<String[]>();
		List<String[]> errString = new ArrayList<String[]>();
		List<String> errhead = new ArrayList<String>();
		//String[] heads = null;
		try{
			//读取CSV文件
			InputStreamReader stream = new InputStreamReader(new FileInputStream(file[0]), "gbk");
			CsvReader reader = new CsvReader(stream, ',');
			while(reader.readHeaders()){
				if(reader.getHeaderCount() > 1){
					//heads =  reader.getHeaders();
					while(reader.readRecord()){
						String rowRecords = reader.getRawRecord();
						String[] rowRecode = rowRecords.split(",");
						csvString.add(rowRecode);
					}
				}
				else
					return null;
			}
			
			//读取XLS文件
			InputStream inStream = new FileInputStream(file[1]);
			HSSFWorkbook workbook = new HSSFWorkbook(inStream);
			//循环每一页，处理当前页
			for(int sheetCount = 0; sheetCount < workbook.getNumberOfSheets(); sheetCount ++){
				HSSFSheet sheet = workbook.getSheetAt(sheetCount);
				if(sheet == null){
					continue;
				}
				HSSFRow headrow = sheet.getRow(0);
				Iterator iterator = headrow.cellIterator();
				while(iterator.hasNext()){
					errhead.add(iterator.next().toString());
				}
				//处理当前页，循环每一行，遍历从第2行开始
				for(int rowCount = 1; rowCount <= sheet.getLastRowNum(); rowCount ++){
					HSSFRow row = sheet.getRow(rowCount);
					String[] rowRecode = new String[headrow.getLastCellNum()];
					//遍历该行，获取每个cell数据
					for(int idx = 0; idx < errhead.size(); idx ++){
						rowRecode[idx] = (row.getCell(idx) == null ? "": row.getCell(idx).toString());
					}
					errString.add(rowRecode);
				}
			}
			
		}catch (IOException e) {
			e.printStackTrace();
		}
		// 分语种创建map集合，data封装到map
		for(int idx = 2; idx < errhead.size(); idx ++){
			Map<String, String> rowData = new LinkedHashMap<String, String>();
			for(String[] data : csvString){
				rowData.put(data[0], idx > data.length ? "" : data[idx-1]);
			}
			for(String[] data : errString){
				rowData.put(data[0], idx > data.length - 1 ? "" : data[idx]);
			}
			languageData.put(errhead.get(idx), rowData);
		}
		return languageData;
	}
	
	/**
	 * file写入content
	 * 
	 * @param outFile
	 * @param content
	 * @param encoding
	 * @param append
	 */
	public static void writeStringToFile(File outFile, String content,
			String encoding, boolean append) throws IOException {
		OutputStream out = null;
        try {
            out = openOutputStream(outFile, append);
            IOUtils.write(content, out, encoding);
            out.close(); // don't swallow close Exception if copy completes normally
        } finally {
            IOUtils.closeQuietly(out);
        }
	}
	
	/**
	 * 创建文件已经创建父目录
	 * @param file
	 * @param append
	 * @return
	 * @throws IOException
	 */
	public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canWrite() == false) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file, append);
    }

	/**
	 * 获取文件集
	 * @param fileList
	 * @param dir
	 */
	public static void collectFiles(List<File> fileList, File dir){
		if(dir.isFile()){
			if(wildcardMatch(FILE_PATTERN, dir.getName())){
				fileList.add(dir);
			}
		}else{
			for(File file: dir.listFiles()){
				collectFiles(fileList, file);
			}
		}
	}
	
	/**
	 * 通配符匹配文件
	 * @param pattern
	 * @param str
	 * @return
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
	
	/**
	 * 删除文件
	 * @param folderPath
	 */
	public static void delFile(String dir) {
		try {
			delAllFile(dir); // 删除完里面所有内容
			java.io.File myFilePath = new java.io.File(dir);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除文件夹路径下所有文件
	 * @param path
	 * @return
	 */
	private static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFile(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}
	
	/**
	 * 获取错误文件数据流
	 * @param file
	 * @return
	 */
	public static String GetErrorString(String file){
		StringBuilder sb = new StringBuilder();
		String str; 
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));
			while((str = br.readLine()) != null){
				sb.append(str);
				sb.append("\r\n");
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	/**
	 * 获取去重的List<Map>
	 * @param originData
	 * @return
	 */
	public static List<LinkedHashMap<String, String>> RmRepeat(List<LinkedHashMap<String, String>> originData){
		List<LinkedHashMap<String, String>> newData = new ArrayList<LinkedHashMap<String, String>>();
		List<LinkedHashMap<String, String>> mergeData = new ArrayList<LinkedHashMap<String, String>>();
		LinkedHashMap<String, String> RepeatMap = new LinkedHashMap<String, String>();
		List<Integer> listIdx = new ArrayList<>();
		List<String> TempkeyList = new ArrayList<String>();
		List<String> TempvalueList = new ArrayList<String>();
		for(int idx = 0; idx < originData.size(); idx ++){
			Map<String, String> map = originData.get(idx);
			for(String key : map.keySet()){
				if(key.equals("longName")){
					if(!RepeatMap.containsValue(map.get(key)) && !TempvalueList.contains(map.get(key))){ 
						//获取键·值都不同的数据
						if(!RepeatMap.containsKey(key)){
							RepeatMap.put(key, map.get(key));
							listIdx.add(originData.indexOf(map));
						}else{
							TempkeyList.add(key);
							TempvalueList.add(map.get(key));
							listIdx.add(originData.indexOf(map));
						}
					}
				}
			}
		}
		if(originData.get(0).keySet().size() > 1){
			for(Integer idx : listIdx){
				mergeData.add(originData.get(idx));
			}
			return mergeData;
		}else{
			for(int index = 0; index < TempkeyList.size(); index ++){
				LinkedHashMap<String, String> tempMap = new LinkedHashMap<String, String>();
				synchronized (tempMap) {
					tempMap.put(TempkeyList.get(index), TempvalueList.get(index));
					newData.add(tempMap);
				}
			}
		}
		newData.add(RepeatMap);
		return newData;
	}
	
	/**
	 * 增量的增加CSV数据
	 * 
	 * @param baseData 基础数据
	 * @param newData 增量数据(全量)
	 * @return
	 */
	public static List<LinkedHashMap<String, String>> mergeNewData(
			List<LinkedHashMap<String, String>> baseData, List<LinkedHashMap<String, String>> newData) {
		List<LinkedHashMap<String, String>> data = baseData;
		if(newData != null){
			for(LinkedHashMap<String, String> newMap : newData){
				boolean isContains = false;
				for(String newValue : newMap.values()){
					for(LinkedHashMap<String, String> baseMap : baseData){
						if(baseMap.containsValue(newValue)){
							isContains = true;
							continue;
						}
					}
				}
				if(!isContains){// 合并数据
					for(String head : header.keySet()){
						if(!newMap.containsKey(head)){
							newMap.put(head, "");
						}
					}
					data.add(newMap);
				}
			}
		}
		//去重
		return RmRepeat(data);
	}
	
	/**
	 * 合并Xls数据
	 * @param baseData
	 * @param newData
	 * @return
	 */
	public static List<LinkedHashMap<String, String>> mergeXlsData(
			LinkedHashMap<String, Object> baseData, List<LinkedHashMap<String, String>> newData) {
		LinkedHashMap<String, String> head = (LinkedHashMap<String, String>) baseData.get("header");
		List<LinkedHashMap<String, String>> data = (List<LinkedHashMap<String, String>>) baseData.get("content");
		if(newData != null){
			for(LinkedHashMap<String, String> newMap : newData){
				boolean isContains = false;
				String id = newMap.get("id");
				for(LinkedHashMap<String, String> baseMap : data){
					if(baseMap.get("id").equals(id)){
						isContains = true;
						continue;
					}
				}
				if(!isContains){
					for(String key : head.keySet()){
						if(!newMap.containsKey(key)){
							newMap.put(key, "");
						}
					}
					data.add(newMap);
				}
			}
		}
		return data;
	}
	
	/**
	 * 使用平台解析xml文件机制，加载各模型文件
	 * @param type
	 * @param filename
	 * @param vars
	 * @return
	 */
	public static <T> List<T> load(Class<?>[] type, String filename, Map<String, Object> vars)
	{
		List<T> ret = new ArrayList<>();
		XmlConfigManager xcm = new XmlConfigManager(type);
		ModelFilesLoader loader = new PatternModelFilesLoader(new String[] { filename });
		ModelFile[] fs = loader.load();
		Compiler compiler = new Compiler();
		for (ModelFile f : fs) {
			try {
				InputStream is = f.getInputStream();
				if (is == null)
				{
					System.err.println("文件读取失败！");
				}
				else {
					if (vars == null) { 
						vars = (Map<String, Object>) new HashMap();
					}
					StringBuilder out = new StringBuilder();
					compiler.compile(is, vars, out);
					is.close();
					if (out.toString().length() != 0)
					{
						if (out.toString().contains("/"))
						{
							InputStream reader = new ByteArrayInputStream(out.toString().getBytes());
							ret.add((T) xcm.load(reader));
						} }
					}
			} catch (Exception e) {
				if ((e instanceof RuntimeException))
					throw ((RuntimeException)e);
				throw new RuntimeException(e);
				}
			}
		return ret;
	}
	
	/**
	 * 测试数据
	 * 
	 * @param args
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		List exportData = new ArrayList<Map>();
		Map row1 = new LinkedHashMap<String, String>();
		row1.put("longName", "sys head");
//		row1.put("2", "12");
//		row1.put("3", "13");
//		row1.put("4", "14");
		exportData.add(row1);
		
//		Map row2 = new LinkedHashMap<String, String>();
//		row2.put("en", "sys haha");
//		row1.put("2", "12");
//		row1.put("3", "13");
//		row1.put("4", "14");
//		exportData.add(row2);
		
		Map row6 = new LinkedHashMap<String, String>();
		row6.put("longName", "transaction code");
		exportData.add(row6);
		
//		Map row3 = new LinkedHashMap<String, String>();
//		row3.put("en", "sys code");
//		row1.put("2", "12");
//		row1.put("3", "23");
//		row1.put("4", "24");
//		exportData.add(row3);
		
		Map row4 = new LinkedHashMap<String, String>();
		row4.put("longName", "error id");
		exportData.add(row4);
		
//		Map row5 = new LinkedHashMap<String, String>();
//		row5.put("en", "error");
//		exportData.add(row5);
		
		//List exportData1 = new ArrayList<Map>();
		Map row7 = new LinkedHashMap<String, String>();
		row7.put("longName", "common request");
		exportData.add(row7);
		
//		Map row8 = new LinkedHashMap<String, String>();
//		row8.put("en", "");
//		exportData.add(row8);
		
		Map row9 = new LinkedHashMap<String, String>();
		row9.put("longName", "organization id");
		exportData.add(row9);
		
//		Map row10 = new LinkedHashMap<String, String>();
//		row10.put("en", "");
//		exportData.add(row10);
		
		LinkedHashMap map = new LinkedHashMap();
		map.put("longName", "longName");
		map.put("en", "en");
//		map.put("3", "第三列");
//		map.put("4", "第四列");
		
		String[] file = new String[]{"C:/Users/Administrator/Desktop/sys/src/main/resources/translate/schema.csv",
		"C:/Users/Administrator/Desktop/sys/src/main/resources/translate/error.xls"};
		Map<String, Map<String, String>> languageData = readLanguageData(file);
		System.out.println(languageData);
		
//		Map<String, Map<String, String>> languageData = readLanguageData("C:/Users/Administrator/Desktop/sys/src/main/resources/translate/schema.csv");
//		GenPropertyFile.GenPropertyFiles(languageData, "J:/Edsp-Oversea/OverseaWS/translate-parent/src/main/resources/language");
		
		//System.out.println(readSchemaData("C:/Users/Administrator/Desktop/sys/src/main/resources/translate/schema1.csv"));
		LinkedHashMap<String, Object> csvData = readSchemaData("C:/Users/Administrator/Desktop/sys/src/main/resources/translate/schema.csv");
		LinkedHashMap<String, Object> newData = readSchemaData("C:/Users/Administrator/Desktop/sys/src/main/resources/translate/schema1.csv");
		//System.out.println(mergeNewData(csvData,newData));
		//creat muti-language csv file 【·update】
		//CSVUtils.createCSVFile(mergeNewData(csvData,newData), CSVUtils.header, "C:/Users/Administrator/Desktop/sys/src/main/resources/translate/", "schema3.csv");
		
		//create single-language csv file
		//File file1 = CSVUtils.createCSVFile(newData, head, "C:/Users/Administrator/Desktop/sys/src/main/resources/translate/", "schema3.csv");
		
		//去重
		//System.out.println(RmRepeat(exportData1));
		//System.out.println(RmRepeat(exportData1));
		
//		String path = "c:/export/";
//		String fileName = "文件导出";
//		File file = CSVUtils.createCSVFile(exportData, map, "C:/Users/Administrator/Desktop/comp/", "schema_new.csv");
//		String fileName2 = file.getName();
//		System.out.println("文件名称：" + fileName2);
		
		
	}
}
