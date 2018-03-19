package cn.language.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import cn.langauge.utils.CSVUtils;
import cn.language.model.ErrorConf;
import cn.language.model.Errors;
import cn.sunline.ltts.config.template.DataUtil;
import cn.sunline.ltts.core.api.model.ModelFile;
import cn.sunline.ltts.core.api.model.dm.ComplexType;
import cn.sunline.ltts.core.api.model.dm.Element;
import cn.sunline.ltts.frw.model.db.Table;
import cn.sunline.ltts.frw.model.dm.Enumeration;
import cn.sunline.ltts.frw.model.dm.RestrictionType;
import cn.sunline.ltts.frw.model.dm.Schema;
import cn.sunline.ltts.frw.model.resource.PatternModelFilesLoader;
import cn.sunline.ltts.frw.model.util.ModelFilesLoader;

public class GenCSVFile {

	private static final String DOT = ".";
	private static final String ERROR_MESSAGE = ".message";
	private static final String DICT_PATH = "classpath:*/*.d_schema.xml";
	private static final String TABLE_PATH = "classpath:*/*.tables.xml";
	private static final String ERRORS_PATH = "classpath:*/*.error.xml";
	private static final String PARMS_PATH = "classpath:*/*.parms.xml";
	private static final String BASETYPE_PATH = "classpath:*/*.u_schema.xml";
	private static final String COMPLEXTYPE_PATH = "classpath:*/*.c_schema.xml";
	private static final String ENUMTYPE_PATH = "classpath:*/*.e_schema.xml";
	private static final String SETTING_CONFIGURATION = "setting.properties";
	private static final LinkedHashMap<String, String> header = new LinkedHashMap<String, String>();
	private static final List<LinkedHashMap<String, String>> inputData = new ArrayList<LinkedHashMap<String, String>>();
	private static final LinkedHashMap<String, Object> datas = new LinkedHashMap<String, Object>();
	
	/**
	 * 生成各元数据文件
	 * @param LocalPath
	 * @param filename
	 */
	public static LinkedHashMap<String, Object> GenSchemaCsvFile(){
		header.put("longName", "longName");
		//生成字典数据，会读取系统jar包
		GenDictCsvData();
		//生成表数据，会加载系统jar包
		GenTableCsvData();
		//生成参数类型数据
		GenParmCsvData();
		//生成基础类型数据
		GenBaseTypeData();
		//生成复合类型数据
		GenComplexTypeData();
		//获取枚举类型数据
		GenEnumTypeData();
		
		//longName value 去重 
		datas.put("header", header);
		datas.put("content", CSVUtils.RmRepeat(inputData));
		return datas;
	}
	
	/**
	 * 生成错误码的数据文件
	 * @param LocalPath
	 * @param filename
	 */
	public static LinkedHashMap<String, Object> GenErrorExcelFile(){
		header.put("id", "id");
		header.put("message", "message");
		//生成错误码文件数据
		GenErrCsvFile();
		
		//longname value 去重 
		datas.put("header", header);
		datas.put("content", inputData);
		return datas;
	}
	
	/**
	 * 获取枚举类型数据
	 */
	public static void GenEnumTypeData(){
		ModelFile modelFile = DataUtil.getSettingModelFile(SETTING_CONFIGURATION);
		Map<String, Object> settings;
		try {
			settings = DataUtil.getTreeMap(DataUtil.getFileSettings(modelFile));
		
			List<Schema> schemaList = CSVUtils.load(new Class[] { Schema.class }, ENUMTYPE_PATH, settings);
			for (Schema schema : schemaList) {
				List<RestrictionType> restriction = schema.getModels(RestrictionType.class);
				for(RestrictionType restric : restriction){
					LinkedHashMap<String, String> RestricData = new LinkedHashMap<String, String>();
					RestricData.put("longName", null == restric.getLongname() ? "" : restric.getLongname());
					inputData.add(RestricData);
					if(restric.getEnumerations() != null){
						for(Enumeration enume : restric.getEnumerations()){
							LinkedHashMap<String, String> Enumedata = new LinkedHashMap<String, String>();
							Enumedata.put("longNname", null == enume.getLongname() ? "" : enume.getLongname());
							inputData.add(Enumedata);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取复杂类型数据
	 */
	public static void GenComplexTypeData(){
		ModelFile modelFile = DataUtil.getSettingModelFile(SETTING_CONFIGURATION);
		Map<String, Object> settings;
		try {
			settings = DataUtil.getTreeMap(DataUtil.getFileSettings(modelFile));
		
			List<Schema> schemaList = CSVUtils.load(new Class[] { Schema.class }, COMPLEXTYPE_PATH, settings);
			for (Schema schema : schemaList) {
				List<ComplexType> complexs = schema.getModels(ComplexType.class);
				for(ComplexType complex : complexs){
					LinkedHashMap<String, String> complexdata = new LinkedHashMap<String, String>();
					complexdata.put("longName", null == complex.getLongname() ? "" : complex.getLongname());
					inputData.add(complexdata);
					if(complex.getElements() != null){
						for(Element element : complex.getElements()){
							LinkedHashMap<String, String> Elementdata = new LinkedHashMap<String, String>();
							Elementdata.put("longName", null == element.getLongname() ? "" : element.getLongname());
							inputData.add(Elementdata);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取基础类型数据
	 */
	public static void GenBaseTypeData(){
		ModelFile modelFile = DataUtil.getSettingModelFile(SETTING_CONFIGURATION);
		Map<String, Object> settings;
		try {
			settings = DataUtil.getTreeMap(DataUtil.getFileSettings(modelFile));
		
			List<Schema> schemaList = CSVUtils.load(new Class[] { Schema.class }, BASETYPE_PATH, settings);
			for (Schema schema : schemaList) {
				List<RestrictionType> complexs = schema.getModels(RestrictionType.class);
				for(RestrictionType restric : complexs){
					LinkedHashMap<String, String> RestricData = new LinkedHashMap<String, String>();
					RestricData.put("longName", null == restric.getLongname() ? "" : restric.getLongname());
					inputData.add(RestricData);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成参数类型数据
	 */
	public static void GenParmCsvData(){
		ModelFile modelFile = DataUtil.getSettingModelFile(SETTING_CONFIGURATION);
		Map<String, Object> settings;
		try {
			settings = DataUtil.getTreeMap(DataUtil.getFileSettings(modelFile));
		
			List<Schema> schemaList = CSVUtils.load(new Class[] { Schema.class }, PARMS_PATH, settings);
			for (Schema schema : schemaList) {
				List<ComplexType> complexs = schema.getModels(ComplexType.class);
				for(ComplexType complex : complexs){
					LinkedHashMap<String, String> complexdata = new LinkedHashMap<String, String>();
					complexdata.put("longName", null == complex.getLongname() ? "" : complex.getLongname());
					inputData.add(complexdata);
					if(complex.getElements() != null){
						for(Element element : complex.getElements()){
							LinkedHashMap<String, String> Elementdata = new LinkedHashMap<String, String>();
							Elementdata.put("longNname", null == element.getLongname() ? "" : element.getLongname());
							inputData.add(Elementdata);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成字典类型数据
	 * @throws IOException 
	 * 
	 */
	public static void GenDictCsvData(){
		ModelFile modelFile = DataUtil.getSettingModelFile(SETTING_CONFIGURATION);
		Map<String, Object> settings;
		try {
			settings = DataUtil.getTreeMap(DataUtil.getFileSettings(modelFile));
		
			List<Schema> schemaList = CSVUtils.load(new Class[] { Schema.class }, DICT_PATH, settings);
			for (Schema schema : schemaList) {
				List<ComplexType> complexs = schema.getModels(ComplexType.class);
				for(ComplexType complex : complexs){
					if(!complex.getFullId().startsWith("ks")){
						LinkedHashMap<String, String> complexdata = new LinkedHashMap<String, String>();
						complexdata.put("longName", null == complex.getLongname() ? "" : complex.getLongname());
						inputData.add(complexdata);
						if(complex.getElements() != null){
							for(Element element : complex.getElements()){
								LinkedHashMap<String, String> Elementdata = new LinkedHashMap<String, String>();
								Elementdata.put("longName", null == element.getLongname() ? "" : element.getLongname());
								inputData.add(Elementdata);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成当前项目表数据
	 * 
	 */
	public static void GenTableCsvData() {
		ModelFile modelFile = DataUtil.getSettingModelFile(SETTING_CONFIGURATION);
		Map<String, Object> settings;
		try {
			settings = DataUtil.getTreeMap(DataUtil.getFileSettings(modelFile));
		
			List<Schema> schemaList = CSVUtils.load(new Class[] { Schema.class }, TABLE_PATH, settings);
			for (Schema schema : schemaList) {
				List<Table> tables = schema.getModels(Table.class);
				for(Table table : tables){
					if(!table.getId().startsWith("ksys_") && !table.getName().startsWith("ksys_")){
						LinkedHashMap<String, String> fieldSet = new LinkedHashMap<String, String>();
						fieldSet.put("longName", null == table.getLongname() ? "" : table.getLongname());
						inputData.add(fieldSet);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取错误码数据
	 * 
	 */
	public static void GenErrCsvFile() {
		ModelFilesLoader loader = new PatternModelFilesLoader(new String[] { ERRORS_PATH });
		ModelFile[] fs = loader.load();
		
		for(ModelFile f :fs){
			String mdoelfile = f.getFullPath();
			String file = mdoelfile.substring(mdoelfile.indexOf("[") + 1, mdoelfile.indexOf("]"));
			String dataStr = CSVUtils.GetErrorString(file);
			
			//调用XStream生成对象
			XStream xstream = new XStream(new DomDriver());
			xstream.processAnnotations(ErrorConf.class);
			//调用XStream API .formXML映射成对应对象
			ErrorConf errorConf = (ErrorConf)xstream.fromXML(dataStr);
	        if(errorConf.getErrors() != null){
	        	List<Errors> errors = errorConf.getErrors();
		        for(Errors error : errors) {
		        	if(error.getError() != null){
		        		for(cn.language.model.Error err : error.getError()){
		        			LinkedHashMap<String, String> dataSet = new LinkedHashMap<String, String>();
		        			dataSet.put("id", error.getId() + DOT + err.getId() + ERROR_MESSAGE);
		        			dataSet.put("message", err.getMessage());
		        			inputData.add(dataSet);
			        	}
		        	}
		        }
	        }
		}
	}
	
	public static void main(String[] args) {
		//生成所有的元数据，并生成到csv文件当中
//		LinkedHashMap<String, Object> datas = GenSchemaCsvFile();
//		File file = CSVUtils.createCSVFile(
//				(List<LinkedHashMap<String, String>>) datas.get("content"),
//				(LinkedHashMap<String, String>) datas.get("header"), "C:/Users/Administrator/Desktop/sys/src/main/resources/translate/", "schema.csv");
		// 生成字典
//		GenDictCsvData();
//		System.out.println(inputData);
		
		//error
		//GenErrCsvFile();
		//System.out.println(GenErrorExcelFile());
		//error generation
		String filepath = "C:\\Users\\Administrator\\Desktop\\sys\\logs\\";
//		LinkedHashMap<String, Object> datass = GenCSVFile.GenErrorExcelFile();
//		GenExcelFile.GenErrorExcelFile(filepath, 
//				(List<LinkedHashMap<String, String>>) datass.get("content"),
//				(LinkedHashMap<String, String>) datass.get("header"), "error1.xls");
		
		LinkedHashMap<String, Object> err_data = GenExcelFile.readErrorExcelFile(filepath + "error1.xls");
//		
		List<LinkedHashMap<String, String>> memory_data = (List<LinkedHashMap<String, String>>) GenErrorExcelFile().get("content");
		
		List<LinkedHashMap<String, String>> merge_Data = CSVUtils.mergeXlsData(err_data, memory_data);

		System.out.println(merge_Data);
		GenExcelFile.GenErrorExcelFile(filepath, merge_Data,
				(LinkedHashMap<String, String>) err_data.get("header"), "error_all.xls");
	}
}
