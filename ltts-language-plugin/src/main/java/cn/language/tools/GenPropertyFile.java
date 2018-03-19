package cn.language.tools;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import cn.langauge.utils.CSVUtils;
import cn.language.model.ErrorConf;
import cn.language.model.Errors;
import cn.sunline.ltts.config.template.DataUtil;
import cn.sunline.ltts.core.api.model.ModelFile;
import cn.sunline.ltts.core.api.model.dm.ComplexType;
import cn.sunline.ltts.core.api.model.dm.Element;
import cn.sunline.ltts.frw.model.dm.Enumeration;
import cn.sunline.ltts.frw.model.dm.RestrictionType;
import cn.sunline.ltts.frw.model.dm.Schema;
import cn.sunline.ltts.frw.model.resource.PatternModelFilesLoader;
import cn.sunline.ltts.frw.model.util.ModelFilesLoader;

public class GenPropertyFile {

	private static final String DICT_PATH = "classpath:**/*.d_schema.xml";
	private static final String ERRORS_PATH = "classpath:**/*.error.xml";
	private static final String PARMS_PATH = "classpath:**/*.parms.xml";
	private static final String BASETYPE_PATH = "classpath:**/*.u_schema.xml";
	private static final String COMPLEXTYPE_PATH = "classpath:**/*.c_schema.xml";
	private static final String ENUMTYPE_PATH = "classpath:**/*.e_schema.xml";
	private static final String SETTING_CONFIGURATION = "setting.properties";
	private static final String PROPERTIES_FREFIX = ".properties";
	private static final String DESCRIPTION_FREFIX = ".description";
	private static final String LONGNAME_FREFIX = ".longname";
	private static final String MESSAGE_FREFIX = ".message";
	private static final String ID_PREFIX = ".id";
	private static final String DOT = ".";
	
	/**
	 * 国际化文件生成入口
	 * @param map
	 * @param projectResourceDir
	 */
	public static void GenPropertyFiles(Map<String, Map<String, String>> map, String projectResourceDir){
		if(map != null){
			Iterator<Entry<String, Map<String, String>>> iterdata = map.entrySet().iterator();
			while(iterdata.hasNext()){
				Entry<String, Map<String, String>> data = iterdata.next();
				String language = data.getKey();
				Map<String, String> languagevalues = data.getValue();
				System.err.println(language + " >> " + languagevalues);
				//生成字典properties文件
				GenDictProperties(languagevalues, language, projectResourceDir + File.separator + language + File.separator + "dict");
				//生成参数properties文件
				GenParmProperties(languagevalues, language, projectResourceDir + File.separator + language + File.separator + "parm");
				//生成基础类型properties文件
				GenBaseProperties(languagevalues, language, projectResourceDir + File.separator + language + File.separator + "type");
				//生成复杂类型properties文件
				GenComplexProperties(languagevalues, language, projectResourceDir + File.separator + language + File.separator + "type");
				//生成枚举properties文件
				GenEnumProperties(languagevalues, language, projectResourceDir + File.separator + language + File.separator + "type");
				//生成错误码properteis文件
				GenErrorProperties(languagevalues, language, projectResourceDir + File.separator + language + File.separator + "error");
			}
		}
	}
	
	/**
	 * 生成字典类型properties文件
	 * @param map 源数据
	 * @param language 语种
	 * @param outpath 输出路径
	 */
	private static void GenDictProperties(Map<String, String> map, String language, String outpath){
		ModelFile modelFile = DataUtil.getSettingModelFile(SETTING_CONFIGURATION);
		Map<String, Object> settings;
		try {
			StringBuffer props = new StringBuffer();
			settings = DataUtil.getTreeMap(DataUtil.getFileSettings(modelFile));
			
			List<Schema> schemaList = CSVUtils.load(new Class[] { Schema.class }, DICT_PATH, settings);
			for (Schema schema : schemaList) {
				String dictname = schema.getId();
				File dictfile = new File(outpath + File.separator + dictname + DOT + language + PROPERTIES_FREFIX);
				List<ComplexType> complexs = schema.getModels(ComplexType.class);
				for(ComplexType complex : complexs){
					if(!complex.getFullId().startsWith("ks")){
						if(complex.getElements() != null){
							for(Element element : complex.getElements()){
								props.append(complex.getId() + DOT + element.getId() + DESCRIPTION_FREFIX + "=" + element.getDescription() + "\n");
								props.append(complex.getId() + DOT + element.getId() + ID_PREFIX + "=" + element.getId() + "\n");
								props.append(complex.getId() + DOT + element.getId() + LONGNAME_FREFIX + "=" + (null == map.get(element.getLongname()) ? "" : map.get(element.getLongname())) + "\n");
							}
						}
						//生成properties文件
						try {
							CSVUtils.writeStringToFile(dictfile, props.toString(), "UTF-8", true);
							System.out.println("生成dict file:" + dictname);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成参数的properties文件
	 * @param map
	 * @param language
	 * @param outpath
	 */
	private static void GenParmProperties(Map<String, String> map, String language, String outpath){
		ModelFile modelFile = DataUtil.getSettingModelFile(SETTING_CONFIGURATION);
		Map<String, Object> settings;
		try {
			StringBuffer props = new StringBuffer();
			settings = DataUtil.getTreeMap(DataUtil.getFileSettings(modelFile));
		
			List<Schema> schemaList = CSVUtils.load(new Class[] { Schema.class }, PARMS_PATH, settings);
			for (Schema schema : schemaList) {
				String parmname = schema.getId();
				File parmfile = new File(outpath + File.separator + parmname + DOT + language + PROPERTIES_FREFIX);
				List<ComplexType> complexs = schema.getModels(ComplexType.class);
				for(ComplexType complex : complexs){
					if(!complex.getFullId().startsWith("TrxEnvs.")){
						props.append(complex.getFullId() + "=" + map.get(complex.getLongname()) + "\n");
						if(complex.getElements() != null){
							for(Element element : complex.getElements()){
								props.append(element.getFullId() + "=" + map.get(element.getLongname()) + "\n");
							}
						}
						//生成properties文件
						try {
							CSVUtils.writeStringToFile(parmfile, props.toString(), "UTF-8", true);
							System.out.println("生成parm file:" + parmname);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成基础类型的properties文件
	 * @param map
	 * @param language
	 * @param outpath
	 */
	private static void GenBaseProperties(Map<String, String> map, String language, String outpath){
		ModelFile modelFile = DataUtil.getSettingModelFile(SETTING_CONFIGURATION);
		Map<String, Object> settings;
		try {
			StringBuffer props = new StringBuffer();
			settings = DataUtil.getTreeMap(DataUtil.getFileSettings(modelFile));
		
			List<Schema> schemaList = CSVUtils.load(new Class[] { Schema.class }, BASETYPE_PATH, settings);
			for (Schema schema : schemaList) {
				String basename = schema.getId();
				File parmfile = new File(outpath + File.separator + basename + DOT + language + PROPERTIES_FREFIX);
				List<RestrictionType> complexs = schema.getModels(RestrictionType.class);
				for(RestrictionType restric : complexs){
					props.append(restric.getFullId() + DESCRIPTION_FREFIX + "=" + (null == restric.getDesc() ? "" : restric.getDesc()) + "\n");
					props.append(restric.getFullId() + ID_PREFIX + "=" + restric.getId() + "\n");
					props.append(restric.getFullId() + LONGNAME_FREFIX + "=" + (null == map.get(restric.getLongname()) ? "" : map.get(restric.getLongname())) + "\n");
				}
				//生成properties文件
				try {
					CSVUtils.writeStringToFile(parmfile, props.toString(), "UTF-8", true);
					System.out.println("生成base file:" + basename);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成复杂类型的properties文件
	 * @param map
	 * @param language
	 * @param outpath
	 */
	private static void GenComplexProperties(Map<String, String> map, String language, String outpath){
		ModelFile modelFile = DataUtil.getSettingModelFile(SETTING_CONFIGURATION);
		Map<String, Object> settings;
		try {
			StringBuffer props = new StringBuffer();
			settings = DataUtil.getTreeMap(DataUtil.getFileSettings(modelFile));
			
			List<Schema> schemaList = CSVUtils.load(new Class[] { Schema.class }, COMPLEXTYPE_PATH, settings);
			for (Schema schema : schemaList) {
				String complexname = schema.getId();
				File complexfile = new File(outpath + File.separator + complexname + DOT + language + PROPERTIES_FREFIX);
				List<ComplexType> complexs = schema.getModels(ComplexType.class);
				for(ComplexType complex : complexs){
					if(complex.getElements() != null){
						for(Element element : complex.getElements()){
							props.append(element.getFullId() + ID_PREFIX + "=" + element.getId() + "\n");
							props.append(element.getFullId() + LONGNAME_FREFIX + "=" + map.get(element.getLongname()) + "\n");
						}
					}
					props.append(complex.getFullId() + DESCRIPTION_FREFIX + "=" + (null == complex.getDescription() ? "" : complex.getDescription()) + "\n");
				}
				//生成properties文件
				try {
					CSVUtils.writeStringToFile(complexfile, props.toString(), "UTF-8", true);
					System.out.println("生成complex file:" + complexname);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成枚举类型的properties文件
	 * @param map
	 * @param language
	 * @param outpath
	 */
	private static void GenEnumProperties(Map<String, String> map, String language, String outpath){
		ModelFile modelFile = DataUtil.getSettingModelFile(SETTING_CONFIGURATION);
		Map<String, Object> settings;
		try {
			StringBuffer props = new StringBuffer();
			settings = DataUtil.getTreeMap(DataUtil.getFileSettings(modelFile));
		
			List<Schema> schemaList = CSVUtils.load(new Class[] { Schema.class }, ENUMTYPE_PATH, settings);
			for (Schema schema : schemaList) {
				String enumname = schema.getId();
				List<RestrictionType> restriction = schema.getModels(RestrictionType.class);
				File enumfile = new File(outpath + File.separator + enumname + DOT + language + PROPERTIES_FREFIX);
				for(RestrictionType restric : restriction){
					if(restric.getEnumerations() != null){
						for(Enumeration enume : restric.getEnumerations()){
							props.append(restric.getFullId()  + DOT + enume.getId() + ID_PREFIX + "=" + enume.getId() + "\n");
							props.append(restric.getFullId()  + DOT + enume.getId() + LONGNAME_FREFIX + "=" + (null == map.get(enume.getLongname()) ? "" : map.get(enume.getLongname())) + "\n");
						}
						props.append(restric.getFullId() + DOT + DESCRIPTION_FREFIX + "=" + (null == map.get(restric.getLongname()) ? "" : map.get(restric.getLongname())) + "\n");
					}
				}
				//生成properties文件
				try {
					CSVUtils.writeStringToFile(enumfile, props.toString(), "UTF-8", true);
					System.out.println("生成enum file:" + enumfile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成错误码的properties文件
	 * @param map
	 * @param language
	 * @param outpath
	 */
	private static void GenErrorProperties(Map<String, String> map, String language, String outpath){
		ModelFilesLoader loader = new PatternModelFilesLoader(new String[] { ERRORS_PATH });
		ModelFile[] fs = loader.load();
		
		for(ModelFile f :fs){
			String mdoelfile = f.getFullPath();
			String file = mdoelfile.substring(mdoelfile.indexOf("[") + 1, mdoelfile.indexOf("]"));
			File errfile = new File(outpath + File.separator + f.getFileName() + DOT + language + PROPERTIES_FREFIX);
			String dataStr = CSVUtils.GetErrorString(file);
			StringBuffer props = new StringBuffer();
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
		        			String dataNode = error.getId() + DOT + err.getId() + MESSAGE_FREFIX;
		        			props.append(dataNode + "=" + (null == map.get(dataNode) ? "" : map.get(dataNode)) + "\n");
			        	}
		        	}
		        }
	        }
	        //生成properties文件
			try {
				CSVUtils.writeStringToFile(errfile, props.toString(), "UTF-8", true);
				System.out.println("生成Error file:" + errfile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//test 
	public static void main(String[] args) {
		String languagePath = "C:\\Users\\Administrator\\Desktop\\sys\\src\\main\\resources\\translate\\data\\";
		String filePath = "C:\\Users\\Administrator\\Desktop\\sys\\src\\main\\resources\\translate\\";
		Map<String, Map<String, String>> languageData = CSVUtils.readLanguageData(new String[]{filePath + "schema.csv", filePath + "error.xls"});
		GenPropertyFile.GenPropertyFiles(languageData, languagePath);
	}
}
