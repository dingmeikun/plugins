package cn.rdp.custom.form.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.cxsw.bean.CxNode;
import com.cxsw.compiler.CxCompileJava;
import com.cxsw.io.CxFileFilterAdapter;
import com.cxsw.loger.CxLogerConsole;
import com.cxsw.logger.CXLogger;
import com.cxsw.model.parser.CxParserBeanXml;
import com.cxsw.model.parser.CxParserNodeXmlPull;
import com.cxsw.model.parser.CxParserStrategyXml;
import com.cxsw.model.utils.XmlParserUtil;
import com.cxsw.unit.data.DictData;
import com.cxsw.unit.data.ListData;
import com.cxsw.utils.FileUtil;
import com.cxsw.utils.FilenameUtil;
import com.cxsw.utils.InvokeUtil;
import com.cxsw.utils.LangUtil;
import com.cxui.common.CxUiContext;
import com.cxui.model.CxUiConfig;
import com.cxui.model.CxUiPage;
import com.cxui.parser.CxUiParser;
import com.cxui.parser.CxUiParserFlowAbstract;
import com.cxui.parser.CxUiParserFlowBean;

@SuppressWarnings("rawtypes")
public class CxPluginForm2Widget
{
	public String devhome, target;
	private Map<String, CxUiContext> mapContexts = new HashMap<String, CxUiContext>();
	private Map<String, List<CxUiParserFlowAbstract>> mapFlows = new HashMap<String, List<CxUiParserFlowAbstract>>();
	private Map<String, List<CxUiParserFlowBean>> mapBeans = new HashMap<String, List<CxUiParserFlowBean>>();
	
	public synchronized CxUiContext getUiContext(String devhome)
	{
		CxUiContext uiContext = mapContexts.get(devhome);
		CXLogger.detial("devhome:[%s]:{%s} %s", devhome, uiContext, mapContexts);
		if (uiContext == null)
		{
			uiContext = new CxUiContext();
			
			List<CxUiParserFlowBean> beans = new ArrayList<CxUiParserFlowBean>();
			String flowConfig = FilenameUtil.concat(devhome, "config/tran/flow.cfg.xml");
			beans.addAll(CxUiParser.load(flowConfig));
			String floderConfig = FilenameUtil.concat(devhome, "config");
			
			uiContext.setLoger(new CxLogerConsole());
			uiContext.setParser(new CxParserBeanXml());
			uiContext.setStrategy(new CxParserStrategyXml());
			uiContext.setFolderConfig(floderConfig);
			uiContext.setParserNode(new CxParserNodeXmlPull());
			
			ListData listdata = ListData.getInstance(devhome);
			listdata.loadDir(FilenameUtil.concat(devhome, "define/resource/list"));
			
			DictData dictdata = DictData.getInstance(devhome);
			dictdata.loadDir(FilenameUtil.concat(devhome, "define/resource/dict"));
			uiContext.setDictdata(dictdata);
			uiContext.setListdata(listdata);
			
			List<CxUiParserFlowAbstract> flows = new ArrayList<CxUiParserFlowAbstract>();
			CxUiParser.initParserFlow(uiContext, beans, flows);
			
			mapBeans.put(devhome, beans);
			mapFlows.put(devhome, flows);
			mapContexts.put(devhome, uiContext);
		}
		
		return uiContext;
	}
	
	
	public String transferFormWidget(String devhome, Map<String, String> attributes, String formmxml, Map<String, CxNode> grids)
	{
		CxUiContext uiContext = getUiContext(devhome);
		List<CxUiParserFlowAbstract> flows = mapFlows.get(devhome);
		List<CxUiParserFlowBean> beans = mapBeans.get(devhome);
		
		CxUiConfig uiCfg = new CxUiConfig();
		
		uiCfg.init(attributes);
		uiCfg.setSelfGridNodes(new HashMap<String, CxNode>());
		uiCfg.setSelfListNodes(new HashMap<String, CxNode>());
		uiCfg.setUnitType("frm");
//		CXLogger.detial("uiCfg.getId():[%s]-template:[%s]", uiCfg.getId(),uiCfg.getTemplate());
		
		uiCfg.getSelfGridNodes().putAll(grids);
		CxUiPage uiPage = (CxUiPage) uiContext.getParser().unpack(formmxml, CxUiPage.class, uiContext.getStrategy());
		uiPage = (CxUiPage) CxUiParser.parserContextUi(uiContext, beans, flows, uiCfg, uiPage);
		return uiContext.getParser().packToString(uiPage, "tran", uiContext.getStrategy());
//		return XmlParserUtil.pack(uiPage);
	}
	
	
	public String transferFormWidget(String devhome, String formfile, String formid)
	{
		File file = new File(formfile);
		
		String cfgpath = FilenameUtil.concat(file.getParent(), formid + ".form.cfg");
		CXLogger.detial("cfgpath :[%s]", cfgpath);
		CxNode node = XmlParserUtil.unpack(FileUtil.readFileToByteArray(cfgpath));
		
		String formmxml = FileUtil.readFileToString(formfile);
		CxUiContext uiContext = getUiContext(devhome);
		List<CxUiParserFlowAbstract> flows = mapFlows.get(devhome);
		List<CxUiParserFlowBean> beans = mapBeans.get(devhome);
		
		CxUiConfig uiCfg = new CxUiConfig();
		
		uiCfg.init(node.getAttributes());
		uiCfg.setSelfGridNodes(new HashMap<String, CxNode>());
		uiCfg.setSelfListNodes(new HashMap<String, CxNode>());
		uiCfg.setUnitType("frm");
//		CXLogger.detial("uiCfg.getId():[%s]-template:[%s]", uiCfg.getId(),uiCfg.getTemplate());
		
		initSelfData(formfile, formid, uiCfg);
		
		CxUiPage uiPage = (CxUiPage) uiContext.getParser().unpack(formmxml, CxUiPage.class, uiContext.getStrategy());
		uiPage = (CxUiPage) CxUiParser.parserContextUi(uiContext, beans, flows, uiCfg, uiPage);
		return uiContext.getParser().packToString(uiPage, "tran", uiContext.getStrategy());
//		return XmlParserUtil.pack(uiPage);
	}
	
	
	public void initSelfData(String formfile, String widgetid, CxUiConfig uiCfg)
    {
		String resource;
		
		resource = formfile + ".list";
    	if (FileUtil.exist(resource))
    	{
    		CxNode listnode = XmlParserUtil.unpack(FileUtil.readFileToByteArray(resource));
    		for (CxNode childnode : listnode.getChilds()) uiCfg.getSelfListNodes().put(childnode.getAttributes().get("id"), childnode);
    	}

    	resource = formfile + ".grid";
    	if (FileUtil.exist(resource))
    	{
    		CxNode gridnode = XmlParserUtil.unpack(FileUtil.readFileToByteArray(resource));
    		for (CxNode childnode : gridnode.getChilds()) uiCfg.getSelfGridNodes().put(childnode.getAttributes().get("id"), childnode);
    	}
    }
}
