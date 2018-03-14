package cn.sunline.ltts.test;

import java.util.Map;

import cn.sunline.edsp.microcore.Bootstrap;
import cn.sunline.odc.file.model.BaseModel;
import cn.sunline.odc.file.util.FileServiceCache;

public class StartServerTest {

	public static void main(String[] args) {
		System.setProperty("setting.file", "setting.dev.properties");
		System.setProperty("log4j.configurationFile", "ltts_log_dev.xml");

		System.setProperty("ltts.home", "");
		System.setProperty("ltts.log.home", "C:\\ltts");
		System.setProperty("ltts.vmid", "UnitTestApp");
		
		Bootstrap.main(new String[]{"start"});
		
		Map<String, BaseModel> allFileService = FileServiceCache.get().getAllFileService();
		System.err.println(allFileService.toString());
	}
}
