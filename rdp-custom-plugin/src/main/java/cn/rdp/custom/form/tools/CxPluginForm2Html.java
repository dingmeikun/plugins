package cn.rdp.custom.form.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cxsw.bean.CxNode;
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
import com.cxui.common.CxUiContext;
import com.cxui.model.CxUiConfig;
import com.cxui.model.CxUiPage;
import com.cxui.parser.CxUiParser;
import com.cxui.parser.CxUiParserFlowAbstract;
import com.cxui.parser.CxUiParserFlowBean;

@SuppressWarnings("rawtypes")
public class CxPluginForm2Html
{
	private CxUiContext uiContext;
	public CxUiContext createUiContext(String devhome)
	{
		if (uiContext != null) return uiContext;
//		String devhome = toolCfg.getDevhome();
		
		uiContext = new CxUiContext();
		CXLogger.detial("devhome:[%s]", devhome);
		String flowConfig = FilenameUtil.concat(devhome, "config/webform/flow.cfg.xml");
		CXLogger.detial("flowConfig:[%s]", flowConfig);
		
		this.beans = new ArrayList<CxUiParserFlowBean>();
		String floderConfig = FilenameUtil.concat(devhome, "config");
		CXLogger.detial("flowConfig:[%s]", flowConfig);
		uiContext.setLoger(new CxLogerConsole());
		uiContext.setParser(new CxParserBeanXml());
		uiContext.setStrategy(new CxParserStrategyXml());
		uiContext.setFolderConfig(floderConfig);
		uiContext.setParserNode(new CxParserNodeXmlPull());
		
		CXLogger.detial("toolCfg.getDevhome():[%s]", devhome);
		ListData listdata = ListData.getInstance();
		listdata.loadDir(FilenameUtil.concat(devhome, "define/resource/list"));
		
		DictData dictdata = DictData.getInstance();
		dictdata.loadDir(FilenameUtil.concat(devhome, "define/resource/dict"));
		uiContext.setDictdata(dictdata);
		uiContext.setListdata(listdata);
		
		this.beans.addAll(CxUiParser.load(flowConfig));
		this.flows = new ArrayList<CxUiParserFlowAbstract>();
		CxUiParser.initParserFlow(uiContext, beans, flows);
			
		return uiContext;
	}
	
	private List<CxUiParserFlowBean> beans;
	private List<CxUiParserFlowAbstract> flows;
	public String transferFormWeb(String devhome, String formfile, String formid, Map<String, CxNode> elements)
	{
		File file = new File(formfile);
		
		String cfgpath = FilenameUtil.concat(file.getParent(), formid+".form.cfg");
		CXLogger.detial("cfg path :[%s]", cfgpath);
		CxNode node = XmlParserUtil.unpack(FileUtil.readFileToByteArray(cfgpath));
		
		String formmxml = FileUtil.readFileToString(formfile);
		
		CxUiContext uiContext = createUiContext(devhome);
		
		CxUiConfig uiCfg = new CxUiConfig();
		uiCfg.setSelfGridNodes(new HashMap<String, CxNode>());
		uiCfg.setSelfListNodes(new HashMap<String, CxNode>());
		uiCfg.setTemplate(node.getAttributes().get("template"));
		uiCfg.setId(node.getAttributes().get("id"));
		uiCfg.setUnitType("frm");
		initSelfData(formfile,uiCfg.getId(), uiCfg);
		
		CxUiPage uiPage = (CxUiPage) uiContext.getParser().unpack(formmxml, CxUiPage.class, uiContext.getStrategy());
    	String context = (String) CxUiParser.parserContextUi(uiContext, beans, flows, uiCfg, uiPage);
    	if (elements != null && uiCfg.getElementNodes() != null) elements.putAll(uiCfg.getElementNodes());
		return context;
	}
	
	public static void initSelfData(String formfile, String widgetid, CxUiConfig uiCfg)
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
