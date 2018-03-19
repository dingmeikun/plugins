package cn.language.tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

public class GenExcelFile {

	/**
	 * 生成excel表到指定目录
	 * 
	 * @param filepath
	 * @param errData
	 */
	public static void GenErrorExcelFile(String filepath, List<LinkedHashMap<String, String>> errData, LinkedHashMap<String, String> errHead, String fileName) {
		// 创建HSSFWorkbook（文档）对象
		HSSFWorkbook workbook = new HSSFWorkbook();
		try{
			// 创建sheet对象，（excel表单）
			HSSFSheet sheet = workbook.createSheet(fileName.substring(0, fileName.lastIndexOf(".")));
			//填充表头
			if(errHead != null){
				HSSFRow row = sheet.createRow(0);
				int headCount = 0;
				for(String value : errHead.values()){
					row.createCell(headCount++).setCellValue(value);
				}
			}
			//填充数据
			int rowcount = 1;
			for(Iterator iterData = errData.iterator(); iterData.hasNext();){
				while(iterData.hasNext()){
					int columcount = 0;
					HSSFRow row = sheet.createRow(rowcount ++);
					Map map = (LinkedHashMap<String, String>) iterData.next();
					for (String key : errHead.keySet()) { //keySet : message en cn ...
						if(map.get(key) != null){
							row.createCell(columcount++).setCellValue(map.get(key).equals("") ? "" : map.get(key).toString());
						}
					}
				}
			}
			FileOutputStream out = new FileOutputStream(filepath + fileName);
			// 写入File
			workbook.write(out);
			out.flush();
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 获取错误码excel表数据
	 * 
	 * @param filepath
	 * @return
	 */
	public static LinkedHashMap<String, Object> readErrorExcelFile(String filepath){
		LinkedHashMap<String, Object> outData = new LinkedHashMap<String, Object>();
		LinkedHashMap<String, String> header = new LinkedHashMap<String, String>();
		List<LinkedHashMap<String, String>> datas = new ArrayList<LinkedHashMap<String, String>>();
		List<String> head = new ArrayList<String>();
		try {
			InputStream inStream = new FileInputStream(filepath);
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
					Object next = iterator.next();
					header.put(next.toString(), next.toString());
					head.add(next.toString());
				}
				//保存文件头
				outData.put("header", header);
				//处理当前页，循环每一行，遍历从第2行开始
				for(int rowCount = 1; rowCount <= sheet.getLastRowNum(); rowCount ++){
					LinkedHashMap<String, String> rowData = new LinkedHashMap<String, String>();
					HSSFRow row = sheet.getRow(rowCount);
					//遍历该行，获取每个cell数据
					for(int idx = 0; idx < head.size(); idx ++){
						rowData.put(head.get(idx), null == row.getCell(idx) ? "": row.getCell(idx).toString());
					}
					datas.add(rowData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//装载数据
		outData.put("content", datas);
		return outData;
	}
	
//	@Test
//	public void read(){
//		String path = "C:/Users/Administrator/Desktop/sys/logs/error.xls";
//		Map<String, Object> map = readErrorExcelFile(path);
//		
//		System.out.println(map);
//	}
	
	public void genExcelTest(){
		// 创建HSSFWorkbook（文档）对象
				HSSFWorkbook wb = new HSSFWorkbook();
				// 创建sheet对象，（excel表单）
				HSSFSheet sheet = wb.createSheet("成绩表");

				// 创建第一行
				HSSFRow row1 = sheet.createRow(0);
				// 创建单元格，参数客人做列索引
				HSSFCell cell = row1.createCell(0);
				// 设置单元格内容
				cell.setCellValue("内容展示如下");
				// 合并单元格CellRangeAddress构造参数依次表示起始行、截止行、起始列、截止列
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

				// 创建第二行
				HSSFRow row2 = sheet.createRow(1);
				// 创建单元格并设置内容
				row2.createCell(0).setCellValue("姓名");
				row2.createCell(1).setCellValue("班级");
				row2.createCell(2).setCellValue("成绩1");
				row2.createCell(3).setCellValue("成绩2");

				// 创建第三行
				HSSFRow row3 = sheet.createRow(2);
				row3.createCell(0).setCellValue("黎明");
				row3.createCell(1).setCellValue("As187");
				row3.createCell(2).setCellValue("89");
				row3.createCell(3).setCellValue("78");

				try {
					FileOutputStream out = new FileOutputStream(
							"C:/Users/Administrator/Desktop/sys/logs/hello.xls");
					// 写入File
					wb.write(out);
					out.flush();
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
}
