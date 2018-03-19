package cn.sunline.ltts.data.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.base.util.ConvertUtil;
import cn.sunline.ltts.base.util.PropertyUtil;
import cn.sunline.ltts.base.util.PropertyUtil.PropertyAccessor;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.model.dm.Element;
import cn.sunline.ltts.core.api.model.dm.ElementType;
import cn.sunline.ltts.core.api.util.LttsCoreBeanUtil;
import cn.sunline.ltts.core.plugin.PluginSupport;
import cn.sunline.ltts.frw.model.db.Table;
import cn.sunline.ltts.frw.model.dm.Schema;
import cn.sunline.ltts.frw.model.util.ExcelConfigManager;

public class DataGeneratorPlugin<T> extends PluginSupport{
	BizLog log = BizLogUtil.getBizLog(DataGeneratorPlugin.class);
	public static Map<String, List> cache = new HashMap<>();
	
	@Override
	public boolean initPlugin() {
		try {
			ExcelConfigManager.get().load(new String[]{"classpath*:excels/**/*.xls"});
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	@Override
	public void startupPlugin() {
		Schema tableFile = OdbFactory.get().getOdbManager(Schema.class).selectByKey("TabDataTable");
		List<Table> tables = new ArrayList<>();
		List<ElementType> eles = tableFile.getTypes();
		for (ElementType ele : eles) {
			if(ele instanceof Table) {
				tables.add((Table)ele);
			}
		}
		
		for (Table table : tables) {
			List<Map<String, String>> datas = ExcelConfigManager.get().getData(table.getId());
			if (datas == null || datas.size() == 0) 
				datas = ExcelConfigManager.get().getData(table.getLongname());
			
			List<T> tData = new ArrayList<>();
			if(CommUtil.isNotNull(datas)) for (Map<String, String> data : datas) {
				T t = mapToEntity(data, table);
				tData.add(t);
//				OdbFactory.get().getOdbManager((T)table.getJavaClass()).insert(t);
			}
			cache.put(table.getFullId(), tData);
		}
	}

	@SuppressWarnings("unchecked")
	private T mapToEntity(Map<String, String> data, Table table) {
		T ret = (T) LttsCoreBeanUtil.getModelObjectCreator().create(table.getJavaClass());
		
		PropertyAccessor pa = PropertyUtil.createAccessor(ret);
		
		for (Element e : table.getAllElements()) {
			String d = data.get(e.getLongname());
			if (d == null) 
				d = data.get(e.getId());
			
			Object value = ConvertUtil.convert(d, e.getElementJavaClass());
			pa.setNestedProperty(e.getId(), value);
		}
		
		return (T)pa.getWrappedObject();
	}

}
