package cn.sunline.ltts.data.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.core.config.ConfigurationFactory;

import cn.sunline.common.util.StringUtil;
import cn.sunline.edsp.microcore.plugin.impl.PluginManager;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.busi.ltts.data.tables.TabDataTable.Ksys_builder_concurrentDao;
import cn.sunline.ltts.busi.ltts.data.tables.TabDataTable.ksys_builder_concurrent;
import cn.sunline.ltts.core.api.config.ConfigManagerFactory;
import cn.sunline.ltts.core.api.model.dm.ElementType;
import cn.sunline.ltts.core.loader.store.ExcelEntityModelLoader;
import cn.sunline.ltts.data.srv.DataGeneratorProcessManager;
import cn.sunline.ltts.frw.model.db.Field;
import cn.sunline.ltts.frw.model.db.Table;
import cn.sunline.ltts.frw.model.dm.RestrictionType;
import cn.sunline.ltts.frw.model.dm.SimpleType;
import cn.sunline.ltts.frw.model.util.ModelClassManager;
import cn.sunline.ltts.frw.model.util.ModelConfig;
import cn.sunline.ltts.frw.model.util.ModelConfig.Group;
import cn.sunline.ltts.frw.model.util.ModelConfig.ModelLoader;
import cn.sunline.ltts.frw.model.util.ModelManager;
import cn.sunline.ltts.frw.model.util.XmlEntityLoader;

/**
 * 数据生成工具类
 * 
 * @author caiqq
 * 
 */
public class DataGenUtil {

	private static final Map<String, DataWriter> writers = new ConcurrentHashMap<String, DataWriter>();
	private static final char SEP_CHAR = '|';

	private static SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss SSS");
	
	private static final String PATH = StringUtil.nullable(System.getProperty("ltts.home"), System.getProperty("user.dir")) + File.separator + "tmp";
	private static final String PATHCTRL = StringUtil.nullable(System.getProperty("ltts.home"), System.getProperty("user.dir")) + File.separator + "temp";

	public static String getPath() {
		return PATH;
	}

	public static void closeWriter() {
		for (DataWriter dw : writers.values()) {
			dw.close();
		}
		writers.clear();
	}

	public static void write(Object data, String filename) {
		write(data, "", filename);
	}

	/**
	 * 写文件的方法
	 * 
	 * @param obj
	 *            数据对象
	 * @param relativePath
	 *            相对路径
	 * @param filename
	 *            文件名
	 */
	public static void write(Object data, String relativePath, String filename) {
		String fileContent = getDataStr(data);
		if (StringUtil.isEmpty(fileContent)) {
			return;
		}

		filename = filename + "_" + Thread.currentThread().getName() + ".txt";
		DataWriter writer = writers.get(filename);
		if (writer == null) {
			String path = PATH;
			if (relativePath.startsWith(File.separator)) {
				path = PATH + relativePath;
			} else {
				path = PATH + File.separator + relativePath;
			}
			writers.put(filename, writer = new DataWriter(path, filename));
			writer.open(true);
		}
		
		writer.write(fileContent);

		// 写控制文件
		/*
		 * Table table = (Table) OdbFactory.getComplexType(data.getClass());
		 * String pathCtrl = PATHCTRL + File.separator + "ctl"; if(pathCtrl !=
		 * null ) { File f = new File(pathCtrl); if (!f.exists()) f.mkdirs(); }
		 * File ctlFile = new File(pathCtrl, table.getId() + ".ctl");
		 * writers.put(ctlFile.getName(), writer = new
		 * DataWriter(ctlFile.getParent(), ctlFile.getName()));
		 * generateCtlFile(data, ctlFile.getAbsolutePath(), writer);
		 */
	}

	private static void generateCtlFile(Object data, String fileName, DataWriter writer) {
		if (new File(fileName).exists())
			return;
		Table table = (Table) OdbFactory.getComplexType(data.getClass());
		StringBuffer sb = new StringBuffer();
		List<Field> fields = table.getAllElements();
		String Tab_Space = "    ";
		sb.append("LOAD DATA" + "\n");
		sb.append("APPEND" + "\n");
		sb.append("INTO TABLE " + table.getId() + "\n");
		sb.append("FIELDS TERMINATED BY '" + SEP_CHAR + "'" + "\n");
		sb.append("trailing nullcols" + "\n");
		sb.append("(" + "\n");
		if (fields.size() > 0)
			for (int i = 0; i < fields.size(); i++) {
				if (i != fields.size() - 1) {
					sb.append(Tab_Space + fields.get(i).getId() + "," + "\n");
				} else {
					sb.append(Tab_Space + fields.get(i).getId() + "\n");
				}
			}
		sb.append(")" + "\n");
		writer.open(true);
		writer.write(sb.toString());
	}

	private static ElementType getSimpleType(RestrictionType type, Map<String, Integer> temp) {
		if (type.getBaseTypeObj() instanceof SimpleType) {
			temp.put("length", type.getMaxLength());
			return type.getBaseTypeObj();
		}
		return getSimpleType((RestrictionType) type.getBaseTypeObj(), temp);
	}

	private static String getDataStr(Object data) {
		if (data == null) {
			return null;
		}

		Table table = (Table) OdbFactory.getComplexType(data.getClass());
		if (table == null) {
			return null;
		}

		StringBuffer sb = new StringBuffer();
		Map<String, Object> dataMap = CommUtil.toMap(data);
		for (Field field : table.getTrueField()) {
			Object value = dataMap.get(field.getId());
			if("data_create_user".equals(field.getId())){
				value = "sys.user";
			}
			if("data_create_time".equals(field.getId())){
				value = df.format(new Date());
			}
			if (value == null) {
				value = "";
			} else {
				value = value.toString();
			}
			sb.append(value).append(SEP_CHAR);
		}

		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	public static void init() {
//		ProfileSwitcher.newCallModeUseExtensionPoint = false;
		String Tmp_file = System.getProperty("temp_path", "classpath*:excels/**/DataGeneratorTable.xls");
		System.setProperty("ltts.log.home", "logs");
		System.setProperty("ltts.log.level", "debug");
		if (StringUtil.isEmpty(System.getProperty("log4j.configurationFile")))
			System.setProperty(ConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "ltts_log_data.xml");

		System.setProperty("ltts.vmid", "dataGenApp");
        System.setProperty(ConfigManagerFactory.SETTING_FILE, "setting.dev.properties");

		ModelConfig config = new ModelConfig();
		Group g1 = new Group();
		g1.setLongname("基础模型");
		config.setGroups(new ArrayList<Group>(Arrays.asList(g1)));
		config.getGroups()
				.get(0)
				.setLoaders(
						new ArrayList<ModelLoader>(Arrays.asList(new XmlEntityLoader("sysconfig", "系统配置",
								new String[] { "classpath*:conf/**/*.xml" }, ModelClassManager.SCHEMA, config.getGroups().get(0)),
								new XmlEntityLoader("datatype", "数据类型", new String[] { "classpath*:datatype/**/*.xml" },
										ModelClassManager.SCHEMA, config.getGroups().get(0)), new XmlEntityLoader("dict", "数据字典",
										new String[] { "classpath*:dict/**/*.xml" }, ModelClassManager.SCHEMA, config.getGroups().get(0)),
								new XmlEntityLoader("type", "复合类型", new String[] { "classpath*:type/**/*.xml" }, ModelClassManager.SCHEMA,
										config.getGroups().get(0)),
								new XmlEntityLoader("dbtable", "表结构", new String[] { "classpath*:tables/**/*.xml" },
										ModelClassManager.SCHEMA, config.getGroups().get(0)), new XmlEntityLoader("namedsql", "命名SQL",
										new String[] { "classpath*:namedsql/**/*.xml" }, ModelClassManager.SQLGROUP, config.getGroups()
												.get(0)))));
		Group g2 = new Group();
		g2.setLongname("excel数据");
		g2.setIndexOnCreation(true);
		config.getGroups().add(g2);
		config.getGroups()
				.get(1)
				.setLoaders(
						new ArrayList<ModelLoader>(Arrays.asList(new ExcelEntityModelLoader("exceltable", "excel表数据",
								new String[] { Tmp_file }, new String[] { "excel" }, config
										.getGroups().get(1)))));
		PluginManager.initPluginServices();
		ModelManager.get().init(config);
		// 初始化所有数据生成器
		DataGeneratorProcessManager.get();

	}
	
	public static String getGroupId(int index) {
		ksys_builder_concurrent d = Ksys_builder_concurrentDao.selectOne_odb1("分组号数量", false);
		// 匹配kapp_zlparm表中的PLZDFZSH字段
		String bfCount = "400";
		if (CommUtil.isNotNull(d) && CommUtil.isNotNull(d.getConcurrent_count())) {
			bfCount = d.getConcurrent_count();
		}
		int groupId = index % Integer.parseInt(bfCount);
		return groupId + "";
	}
	
}
