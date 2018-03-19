package cn.language.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import cn.langauge.utils.CSVUtils;
import cn.language.tools.GenCSVFile;
import cn.language.tools.GenExcelFile;
import cn.language.tools.GenPropertyFile;

public class LanguageEngine {
	
	private static final String SCHEMA_CSVFILE_NAME = "schema.csv";
	private static final String ERROR_XLSFILE_NAME = "error.xls";
	private static MavenProject project = new MavenProject();

	/**
	 * 翻译主方法
	 * 
	 * @param args
	 * @throws MojoFailureException
	 */
	public static void Main(Map<String, Object> args) throws MojoFailureException{
		
		//获取MavenProject对象，获取当前执行项目对象
		project = (MavenProject) args.get("project");
		//获取资源文件存在的路径(目录)
		if(null == args.get("filePath")){
			throw new MojoFailureException("CSV文件路径不能为空!!");
		}
		String filePath = args.get("filePath").toString();
		String languagePath = project.getBuild().getResources().get(0).getDirectory() + File.separator + "language";
		
		if((new File(filePath).exists())){
			new File(filePath).mkdir();
		}
		//初始化CSV文件和Excel文件
		initCsvAndExcelData(filePath);
		//TODO 生成properties文件 [前提：存在language文件目录]
		//按照csv文件存在多少个语种，生成多少个语言文件(cn,en,zh) 读取每个字典的内容，包括文件名、longname...
		//这一步需要注意的是：1、language文件目录  2、有多少个语种 3、按照文件粒度生成对应properties...
		if((new File(languagePath)).exists()){
			Map<String, Map<String, String>> languageData = CSVUtils.readLanguageData(new String[]{filePath + SCHEMA_CSVFILE_NAME, filePath + ERROR_XLSFILE_NAME});
			GenPropertyFile.GenPropertyFiles(languageData, languagePath);
		}
	}
	
	/**
	 * 初始化CSV文件元数据
	 * 
	 * @param filepath
	 */
	private static void initCsvAndExcelData(String filepath){
		File csvfile = new File(filepath);
		List<File> listFiles = new ArrayList<File>();
		boolean existSchemaFile = false;
		boolean existErrorFile = false;
		//获取文件集
		CSVUtils.collectFiles(listFiles, csvfile);
		if(listFiles.size() != 0){
			for(File file: listFiles){
				if(file.getName().equals(SCHEMA_CSVFILE_NAME)){
					existSchemaFile = true;
				}else if(file.getName().equals(ERROR_XLSFILE_NAME)){
					existErrorFile = true;
				}
			}
		}
		//如果不存在schema文件,本次生成该文件
		if(!existSchemaFile){
			LinkedHashMap<String, Object> datas = GenCSVFile.GenSchemaCsvFile();
			File file = CSVUtils.createCSVFile(
					(List<LinkedHashMap<String, String>>) datas.get("content"),
					(LinkedHashMap<String, String>) datas.get("header"), filepath, SCHEMA_CSVFILE_NAME);
			System.out.println("生成>>>" + file.getName());
		}else{
			//否则更新该文件  获取当前内存文件，拿来和现有已存在的文件做对比
			LinkedHashMap<String, Object> ndatas = GenCSVFile.GenSchemaCsvFile();
			//TODO 获取已存在CSV 文件内容  路径：filepath + SCHEMA_CSVFILE_NAME
			LinkedHashMap<String, Object> cdatas = CSVUtils.readSchemaData(filepath + SCHEMA_CSVFILE_NAME);
			
			List<LinkedHashMap<String, String>> CsvData = (List<LinkedHashMap<String, String>>) cdatas.get("content");
			List<LinkedHashMap<String, String>> NewData = (List<LinkedHashMap<String, String>>) ndatas.get("content");
			//获取增量的CSV文件数据集
			List<LinkedHashMap<String, String>> mergeData = CSVUtils.mergeNewData(CsvData, NewData);
			
			//重新写入到CSV文件
			File file = CSVUtils.createCSVFile(mergeData,
					(LinkedHashMap<String, String>) cdatas.get("head"), filepath, SCHEMA_CSVFILE_NAME);
			System.out.println("mergeData:" + mergeData + "\nheader:" + (LinkedHashMap<String, String>) cdatas.get("head"));
			System.out.println("更新 >>>" + file.getName());
		}
		//如果不存在error文件，生成该文件
		if(!existErrorFile){
			LinkedHashMap<String, Object> datas = GenCSVFile.GenErrorExcelFile();
			GenExcelFile.GenErrorExcelFile(filepath, 
					(List<LinkedHashMap<String, String>>) datas.get("content"),
					(LinkedHashMap<String, String>) datas.get("header"), ERROR_XLSFILE_NAME);
			System.out.println("生成>>>" + ERROR_XLSFILE_NAME);
		}else{
			//更新文件,从内存获取数据
			List<LinkedHashMap<String, String>> memory_data = (List<LinkedHashMap<String, String>>) GenCSVFile.GenErrorExcelFile().get("content");
			//拿指定文件数据，文件为已存在多语种翻译文件
			LinkedHashMap<String, Object> err_data = GenExcelFile.readErrorExcelFile(filepath + ERROR_XLSFILE_NAME);
			
			//TODO 合并数据、生成新文件
			List<LinkedHashMap<String, String>> merge_Data = CSVUtils.mergeXlsData(err_data, memory_data);

			//生成！
			GenExcelFile.GenErrorExcelFile(filepath, merge_Data,
					(LinkedHashMap<String, String>) err_data.get("header"), ERROR_XLSFILE_NAME);
			System.out.println("更新>>>" + ERROR_XLSFILE_NAME);
		}
	}
}
