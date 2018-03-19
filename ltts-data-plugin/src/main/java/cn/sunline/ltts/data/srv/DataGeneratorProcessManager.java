package cn.sunline.ltts.data.srv;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.sunline.common.util.StringUtil;
import cn.sunline.ltts.base.util.ClassUtils;
import cn.sunline.ltts.busi.ltts.data.tables.TabDataTable.ksys_builder_concurrent;
import cn.sunline.ltts.busi.ltts.data.tables.TabDataTable.Ksys_builder_concurrentDao;
import cn.sunline.ltts.busi.ltts.data.tables.TabDataTable.ksys_data_generator;
import cn.sunline.ltts.busi.sdk.util.DaoUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.data.process.DataGeneratorProcess;

/**
 * 数据生成管理器
 * @author caiqq
 *
 */
public class DataGeneratorProcessManager {
	private final static BizLog log = BizLogUtil.getBizLog(DataGeneratorProcessManager.class);
	private final Map<String, DataGeneratorProcess> processes = new ConcurrentHashMap<String, DataGeneratorProcess>();
	
	private int threadCount = 5;
	private int brchnoThreadCount = 1;
	
	private static DataGeneratorProcessManager instance = null;
	
	private DataGeneratorProcessManager(){
		init();
	}
	
	
	
	public static synchronized DataGeneratorProcessManager get() {
		if(instance==null){
			instance=new DataGeneratorProcessManager();
		}
		
		return instance;
	}
	
	private void init() {
		ksys_builder_concurrent ksys_bingfs = Ksys_builder_concurrentDao.selectOne_odb1("生成并发数", false);
		if (ksys_bingfs != null) {
			if (StringUtil.isNotEmpty(ksys_bingfs.getConcurrent_count()))
				threadCount = Integer.parseInt(ksys_bingfs.getConcurrent_count());
		}
		log.info("生成器并发数为["+threadCount+"]");
		
		ksys_bingfs = Ksys_builder_concurrentDao.selectOne_odb1("机构并发数", false);
		if (ksys_bingfs != null) {	
			if (StringUtil.isNotEmpty(ksys_bingfs.getConcurrent_count()))
				brchnoThreadCount = Integer.parseInt(ksys_bingfs.getConcurrent_count());
		}
		log.info("机构并发数为["+brchnoThreadCount+"]");

		for (ksys_data_generator ks : DaoUtil.selectAll(ksys_data_generator.class)) {
			try {
				if(StringUtil.isNotEmpty(ks.getBuilder_classpath())) {
					log.info("生成器类路径为： " + ks.getBuilder_classpath() + ";生成器中文名为 " + ks.getBuilder_chname());
					
					DataGeneratorProcess process = ClassUtils.newInstance(ks.getBuilder_classpath(), DataGeneratorProcess.class);
					processes.put(ks.getBuilder_name(), process);
					processes.put(ks.getBuilder_chname(), process);
				} else
					log.info(ks.getBuilder_classpath() + "(" + ks.getBuilder_chname() + ")" + "生成器类路径未配置，不生成数据。");
			} catch (Exception e) {
				log.error("初始化数据生成器[" + ks.getBuilder_name()+ "][" + ks.getBuilder_chname() + "]失败", e);
			}
		}
	}
	
	public DataGeneratorProcess getProcess(String processName) {
		return processes.get(processName);
	}
	
	public int getThreadCount() {
		return threadCount;
	}

	public int getBrchnoThreadCount() {
		return brchnoThreadCount;
	}
	
}
