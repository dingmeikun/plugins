package cn.rdp.custom.form.api;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cxsw.bean.CxNode;
import com.cxsw.form.CxFormConfig;
import com.cxsw.io.CxFileFilterAdapter;
import com.cxsw.io.zip.CxZipFile;
import com.cxsw.logger.CXLogger;
import com.cxsw.model.utils.XmlParserUtil;
import com.cxsw.odb.dao.CxOdbDaoProxy;
import com.cxsw.plugin.api.CxPluginFileApi;
import com.cxsw.plugin.api.CxPluginProjectBaseApi;
import com.cxsw.plugin.compile.CxPluginFlexCompile;
import com.cxsw.plugin.form.tools.CxPluginForm2Html;
import com.cxsw.plugin.form.tools.CxPluginForm2WebJs;
import com.cxsw.plugin.form.tools.CxPluginForm2WebJsBase;
import com.cxsw.plugin.form.tools.CxPluginForm2Widget;
import com.cxsw.plugin.form.tools.CxPluginGrid2WebJs;
import com.cxsw.tsf.CxTsfWorkI;
import com.cxsw.utils.FileUtil;
import com.cxsw.utils.FilenameUtil;
import com.cxsw.utils.LangUtil;
import com.cxsw.utils.ReflectBeanUtil;
import com.cxsw.utils.RegtagUtil;
import com.cxsw.utils.StringUtil;
import com.sunline.rdp.form.bean.RdpFormBean;
import com.sunline.rdp.form.utils.RdpFormBeanFlex;


/**
 * Flex代码转Java代码的原则
 * 1. Flex转过来之后，注释信息没有了
 * 2. Flex转程Java，然后再由Java转成Flex，然后再由Flex转成Java，得到的两个Java文件应该是一样的。
 * 
 * @author winen
 *
 */
public abstract class CxPluginFormBase
{
	public static final String FORMJAVACODE = "src/com/rdp/busi/trx/t%s/RdpPage_%s.java";
	public static final String FORMJAVASCRIPTCODE = "RdpPage_%s.js";
	public static final String GRIDJAVASCRIPTCODE = "RdpPage_%s_%s.js";
	public static final String FORMACTIONSCRIPTCODE = "RdpPage_%s.as";
	public static final String GRIDACTIONSCRIPTCODE = "RdpPage_%s_%s.as";
	
	public boolean swtOnly = false;
	public abstract String getFlexSDK();
	
	public void save(String devhome, String project, String path, RdpFormBean formbean)
	{
		FileUtil.write(path, formbean.getFormmxml(), false);
		FileUtil.write(path + ".grid", formbean.getGriddata(), false);
		FileUtil.write(path + ".list", formbean.getListdata(), false);
		
		CxNode node = new CxNode("config");
		
		Map<String, String> attributes = new LinkedHashMap<String, String>();
		ReflectBeanUtil.getSimpleValuesAsString(formbean.getFormConfig(), attributes);
		
		CXLogger.detial("attributes:{%s}", attributes);
		node.getAttributes().putAll(attributes);
		FileUtil.write(path + ".cfg",XmlParserUtil.pack(node),false);
		
		String formid = FilenameUtil.getBaseName(path);
		String scriptFolder = FilenameUtil.concat(project, "src/com/rdp/busi/trx/t" + formid);
		
		String context = FileUtil.readFileToString(devhome+"/config/jcode/RdpPageScript.txt");
		context = context.replaceAll("%formid%", formid);
		FileUtil.write(scriptFolder + "/RdpPage_" + formid + ".java", context, false);
		
		context = FileUtil.readFileToString(devhome+"/config/jcode/RdpPageScriptGrid.txt");
		context = context.replaceAll("%formid%", formid);
		
		CxNode griddata = XmlParserUtil.unpack(formbean.getGriddata());
		for (CxNode grid : griddata.getChilds())
		{
			String gridid = grid.getAttributes().get("id");
			String script = context.replaceAll("%gridid%", gridid);
			FileUtil.write(scriptFolder + "/RdpPage_" + formid + "_" + gridid+ ".java", script, false);
		}
	}
	
	
	public void clearFormTemp(String project, String folder, final String filenameid)
	{
		FileUtil.listFolder(project + "/temp", new CxFileFilterAdapter()
		{
			@Override
			public boolean validateFolder(String folderName)
			{
				if (folderName.endsWith(".svn")) return false;
				
				if (folderName.endsWith("t" + filenameid))
				{
					try {
						FileUtil.deleteDirectory(new File(folderName));
					} catch (IOException e) {
						e.printStackTrace();
					}
					return false;
				}
				return super.validateFolder(folderName);
			}
			
			

			@Override
			public void display(String parent, String name)
			{
				if (FilenameUtil.wildcardMatchs(name, "*" + filenameid + "*")) FileUtil.delete(FilenameUtil.concat(parent, name));
			}
		});
		
		
		FileUtil.listFolder(folder, new CxFileFilterAdapter()
		{
			
			@Override
			public boolean validateFolder(String folderName) {
				if (folderName.endsWith(".svn")) return false;
				return super.validateFolder(folderName);
			}

			@Override
			public void display(String parent, String name)
			{
				if (name.equals(filenameid + ".form")) return;
				if (name.equals(filenameid + ".form.cfg")) return;
				if (name.equals(filenameid + ".form.grid")) return;
				if (name.equals(filenameid + ".form.list")) return;
				if (name.equals(filenameid + ".self.*")) return;
				
				if (FilenameUtil.wildcardMatchs(name, "*" + filenameid + "*.html"))
				{
					CXLogger.detial("name:[%s]", name);
					FileUtil.delete(FilenameUtil.concat(parent, name));
				}
				
				if (FilenameUtil.wildcardMatchs(name, "*" + filenameid + "*.frm.xml"))
				{
					CXLogger.detial("name:[%s]", name);
					FileUtil.delete(FilenameUtil.concat(parent, name));
				}
				
				if (FilenameUtil.wildcardMatchs(name, "*" + filenameid + "*.js"))
				{
					CXLogger.detial("name:[%s]", name);
					FileUtil.delete(FilenameUtil.concat(parent, name));
				}
				
				if (FilenameUtil.wildcardMatchs(name, "*" + filenameid + "*.as"))
				{
					CXLogger.detial("name:[%s]", name);
					FileUtil.delete(FilenameUtil.concat(parent, name));
				}
				
				if (FilenameUtil.wildcardMatchs(name, "*" + filenameid + "*.swf"))
				{
					CXLogger.detial("name:[%s]", name);
					FileUtil.delete(FilenameUtil.concat(parent, name));
				}
				
				if (FilenameUtil.wildcardMatchs(name, "*" + filenameid + "*.jar"))
				{
					CXLogger.detial("name:[%s]", name);
					FileUtil.delete(FilenameUtil.concat(parent, name));
				}
			}
		});
		
		String flexSdk = getFlexSDK();
		FileUtil.listFolder(flexSdk, new CxFileFilterAdapter()
		{
			
			@Override
			public void display(String parent, String name)
			{
				if (FilenameUtil.wildcardMatchs(name, filenameid + "-*.xml")) FileUtil.delete(FilenameUtil.concat(parent, name));
			}
		});
	}
	
//	public void save(String devhome, String project, String formfile)
//	{
//		CXLogger.detial("devhome:[%s],formfile:[%s],project:[%s]", devhome, formfile, project);
//		if (project == null || project.trim().length() == 0)
//		{
//			project = FilenameUtil.getParentPath(devhome);
//			String path = FilenameUtil.subcat(formfile, project);
//			String[] tItem = path.split("/");
//			project = FilenameUtil.concat(project, tItem[0]);
//			CXLogger.detial("project:[%s]", project);
//		}
//		
//		genFormWidget(devhome, formfile);
//		
//		if (swtOnly) return;
//		String formid = FilenameUtil.getBaseName(formfile);
//		Map<String, CxNode> elements = new HashMap<String, CxNode>();
//		
//		genFormActionScriptCode(formfile,project, formid);
//		genFormFlex(devhome, project, formfile, formid);
//		
//		try
//		{
//			elements.clear();
//			genFormHtml(devhome, formfile, formid, elements);
//			genFormGridJs(devhome,project, formfile, formid,elements);
//			genFormJavaScriptCode(devhome, project, formfile, formid, elements.keySet());	
//		}
//		catch (Exception e)
//		{
////			e.printStackTrace();
//		}
//	}
	
	private RdpFormBeanFlex beanFlex;
	public void genFormFlex(String devhome, String project, String formfile, String formid)
	{
		String frameworks = getFlexSDK() + "/flex_sdk_3.6a/frameworks";
		String template = project;
		
		if (beanFlex == null)
		{
			beanFlex = new RdpFormBeanFlex();
			beanFlex.init(devhome,CxPluginFlexCompile.class.getName(), frameworks, template + "/temp/flexscript", template + "/temp/flexexport");
		}
		
		beanFlex.saveFormBean(formfile);
	}
	
	public void genFormActionScriptCode(final String ifile, String project, final String formid)
	{
		final String formFolder = FilenameUtil.getParentPath(ifile);
		String javaname = StringUtil.format(FORMJAVACODE, formid, formid);
		String name = StringUtil.format(FORMACTIONSCRIPTCODE, formid, formid);
		
		
		String source = FilenameUtil.concat(project, javaname);
		String target = FilenameUtil.concat(formFolder, name);
		CXLogger.detial("source:[%s] target:[%s]", source,target);
//		CxPluginFileApi.loadJava2As(source, target);
		
		String folder = FilenameUtil.getParentPath(source);
		FileUtil.listFolder(folder, new CxFileFilterAdapter()
		{
			@Override
			public void display(String parent, String name)
			{
				if (!FilenameUtil.wildcardMatchs(name, "RdpPage_" + formid + "_*.java")) return;
				String source = FilenameUtil.concat(parent, name);
				String target = FilenameUtil.concat(formFolder, name.replace(".java", ".as"));
				CxPluginFileApi.loadJava2As(source, target);
			}

			@Override
			public boolean validateFolder(String folderName) {
				return false;
			}
		});
	}
	
	private CxPluginForm2Widget formWidget;
	
	public String genFormWidgetContext(String devhome, String formfile)
	{
		String formid = FilenameUtil.getFilenameid(formfile);
		
		if (formWidget == null)
		{
			formWidget = new CxPluginForm2Widget();
		}
		
		formWidget.devhome = devhome;
		formWidget.target = FilenameUtil.getParentPath(formfile);
		
		String context = formWidget.transferFormWidget(devhome, formfile, formid);
		String path = FilenameUtil.concat(formWidget.target, formid + ".frm.xml");
		
		CXLogger.detial("path:[%s]", path);
		return context;
	}
	public String genFormWidget(String devhome, String formfile)
	{
		String context = genFormWidgetContext(devhome, formfile);
		String formid = FilenameUtil.getFilenameid(formfile);
		String path = FilenameUtil.concat(formWidget.target, formid + ".frm.xml");
		
		CXLogger.detial("path:[%s]", path);
		FileUtil.write(path, context, false);
		return formid;
	}
	
	
	public void genFormJavaScriptCode(String devhome, String project, String targetpath, String formfile, String formid, Set<String> grids)
	{
		String javaname = StringUtil.format(FORMJAVACODE, formid, formid);
		String name = StringUtil.format(FORMJAVASCRIPTCODE, formid, formid);
		String source = FilenameUtil.concat(project, javaname);
		String target = FilenameUtil.concat(targetpath + File.separator + "tran" + File.separator + formid, name);
		CXLogger.detial("source:[%s],\r\ntarget:[%s],\r\nformfile:[%s]", source, target, formfile);
//		CxPluginFileApi.loadJava2Js(source, target);
		
		new CxPluginForm2WebJs().execute(source, formfile, target, grids);
	}
	
	
	private CxPluginForm2Html form2html;
	public void genFormHtml(String devhome, String formpath, String formid, Map<String, CxNode> elements)
	{
		if (form2html == null) form2html = new CxPluginForm2Html();
		String htmlcontext = form2html.transferFormWeb(devhome,  formpath, formid, elements);
		try
		{
			formpath = formpath.replace(".form", ".html");
			CXLogger.detial("formpath:[%s]", formpath);
			FileUtil.writeStringToFile(new File(formpath), htmlcontext, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getGridScriptFile(String devhome, String formid, String gridid)
	{
		String name = "src/com/rdp/busi/trx/t%s/RdpPage_%s_%s.java";
		return FilenameUtil.concat(devhome, StringUtil.format(name, formid, formid, gridid));
	}
	
	public void genFormGridJs(String devhome, String project, String targetpath, String formfile, String formid, Map<String, CxNode> elements)
	{
		String filepath = targetpath + File.separator + "tran" + File.separator + formid + formfile.substring(formfile.lastIndexOf("/"));
		File file = new File(filepath);
		if(file.exists())
		{
			Map<String, String> mapGridjs = CxPluginGrid2WebJs.initGridData(devhome, formfile, formid, elements);
			for(Map.Entry<String , String> grids : mapGridjs.entrySet())
			{
				String path = FilenameUtil.concat(targetpath + File.separator + "tran" + File.separator + formid, "RdpPage_" + formid +"_" + grids.getKey()+".js");
				String context = grids.getValue();
				
				String source = getGridScriptFile(project, formid, grids.getKey());
				CXLogger.detial("source:{%s}", source);
				source = CxPluginGrid2WebJs.loadGridJavaCode(grids.getKey(), source);
				CXLogger.detial("source:{%s}", source);
				context = RegtagUtil.replateContext(context, "gridfunctions", source);
				context = CxPluginForm2WebJsBase.replateJavaScript(context);
				FileUtil.write(path, context, false);
			}
		}
	}
	
	
	public byte[] genFormJavaScriptCode(InputStream in)
	{
		try
		{
			CompilationUnit cu;
			cu = JavaParser.parse(in, "UTF-8");
			return cu.toJavaScriptCode().getBytes();
		}
		catch (ParseException e)
		{
			throw LangUtil.newRuntimeException(e);
		}
	}
	
	public byte[] genFormActionScriptCode(InputStream in)
	{
		try
		{
			CompilationUnit cu = JavaParser.parse(in, "UTF-8");
			return cu.toActionScriptCode().getBytes();
		}
		catch (Exception e) 
		{
			throw LangUtil.newRuntimeException(e);
		}
	}
	
	public byte[] genFormWebScriptCode(String formid)
	{
		return "".getBytes();
	}

	public byte[] genFormFrontScriptCode(String formid) {
		return "".getBytes();
	}
	
	public void submitFlexResource(CxTsfWorkI tsfWork, String project, String formfile, String formid)
	{
		String[] tFile = new String[]{"lib" + formid + ".swf", "t" + formid + ".swf"};
		
		for (String file : tFile)
		{
			String local = FilenameUtil.concat(project,"temp/flexscript/resource/formmxml", file);
			String remote = FilenameUtil.concat("rdp/task",file);
			CXLogger.detial("[%s]->[%s]", local, remote);
			tsfWork.upload(remote, local);
		}
	}
	
	public void submitHtmlResource(final CxTsfWorkI tsfWork, String formfile, final String formid)
	{
		String folder = FilenameUtil.getParentPath(formfile);
		
		final String filter = "*" + formid + "*";
		FileUtil.listFolder(folder, new CxFileFilterAdapter()
		{
			@Override
			public void display(String parent, String name)
			{
				if (FilenameUtil.wildcardMatchs(name, "*.self.*")) return;
				
				if (!FilenameUtil.wildcardMatchs(name, filter + ".js") &&
						!FilenameUtil.wildcardMatchs(name, filter + ".html") ) return;
				
				String remote = FilenameUtil.concat("tran",formid, name);
				String local = FilenameUtil.concat(parent, name);
				CXLogger.detial("[%s]->[%s]", local, remote);
				tsfWork.upload(remote, local);
			}
		});
	}
	
	public void compileFormFlex(String devhome, String project, String file, String formid)
	{
		String frameworks = getFlexSDK() + "/flex_sdk_3.6a/frameworks";
		
		if (formid == null) formid = FilenameUtil.getBaseName(file);
		if (project == null || project.trim().length() == 0) project = new CxPluginProjectBaseApi(devhome).getProjectPath(file);
		String template =project;
		RdpFormBeanFlex beanFlex = new RdpFormBeanFlex();
		beanFlex.init(devhome,CxPluginFlexCompile.class.getName(), frameworks, template + "/temp/flexscript", template + "/temp/flexexport");
		beanFlex.compile(file);
	}
	
	
	/*public static void main(String[] args)
	{
		String folder = FilenameUtil.concat(devhome, "bin/com/rdp/busi/trx/t" + formid);
		CXLogger.detial("folder:[%s]", folder);
		FileUtil.listFolder(folder, new CxFileFilterAdapter()
		{
			@Override
			public void display(String parent, String name)
			{
				if (FilenameUtil.wildcardMatchs(name, "*.self.*")) return;
				if (!FilenameUtil.wildcardMatchs(name, filter + ".class")) return;
				String path = FilenameUtil.concat(parent, name);
				CXLogger.detial("path:[%s]", path);
				CxZipFile.insertData(listdatas, name, FileUtil.readFileToByteArray(path));
			}
		});
	}*/
	
	public void submitFormWidget(final CxTsfWorkI tsfWork, String devhome, String project, String formfile, final String formid)
	{
		final String filter = "*" + formid + "*";
		
		String local = formfile.replace(".form", ".jar");
		final List<Object> listdatas = new ArrayList<Object>();
		
		String widgetfile = formfile.replace(".form", ".frm.xml");
		CXLogger.detial("widgetfile:[%s]", widgetfile);
		CxZipFile.insertData(listdatas, formid + ".frm.xml", FileUtil.readFileToByteArray(widgetfile));
		
		String folder = FilenameUtil.concat(project, "bin/com/rdp/busi/trx/t" + formid);
		CXLogger.detial("folder:[%s]", folder);
		FileUtil.listFolder(folder, new CxFileFilterAdapter()
		{
			@Override
			public void display(String parent, String name)
			{
				if (FilenameUtil.wildcardMatchs(name, "*.self.*")) return;
				if (!FilenameUtil.wildcardMatchs(name, filter + ".class")) return;
				String path = FilenameUtil.concat(parent, name);
				CXLogger.detial("path:[%s]", path);
				CxZipFile.insertData(listdatas, "com/rdp/busi/trx/t" + formid + "/" +name, FileUtil.readFileToByteArray(path));
			}
		});
		
		CxZipFile zipfile = new CxZipFile(local);
		zipfile.write(listdatas, false);
		CxZipFile.clear(listdatas);
		
		String remote = FilenameUtil.concat("rdp/widget", formid + ".jar");
		CXLogger.detial("[%s]->[%s]", local, remote);
		tsfWork.upload(remote, local);
		
		String subfolder = FilenameUtil.getParentPath(local);
		subfolder = FilenameUtil.getParentPath(subfolder);
		subfolder = FilenameUtil.getName(subfolder);
		
		remote = FilenameUtil.concat("rdp/widget",subfolder, formid + ".frm.xml");
		tsfWork.upload(remote, widgetfile);
		CXLogger.detial("[%s]->[%s]", remote, widgetfile);
	}
	public static void submitFormOdb(CxOdbDaoProxy daoProxy,String resourcepath,String formid)
	{
		try
		{
			String path = resourcepath+".cfg";
			CXLogger.detial("path:%s", path);
			Map<String, String> data = null;
			if(FileUtil.exist(path))
			{
				CxNode cxnode = XmlParserUtil.unpack(FileUtil.readFileToString(path));
				data = cxnode.getAttributes();
			}
			if(data == null) return;
			CxFormConfig formCfg = new CxFormConfig();
			
			CXLogger.detial("data:{%s}", data);
			formCfg.init(data);
			
			Map<String, Object> datas = formCfg.getDbValue();
			CXLogger.infor("数据库操作:[%s]", datas);
			int count = daoProxy.count("rdpform", "formid", formCfg.getId());
			if (count <= 0)
			{
				daoProxy.insert("rdpform", datas);
			}
			else
				daoProxy.update("rdpform", Arrays.asList("formid"),datas);
		}
		catch (Exception e)
		{
			throw LangUtil.newRuntimeException(e);
		}
	}
	public static void main(String[] args)
	{
		String subfolder = FilenameUtil.getParentPath("G:/source/rdpnjbank/Rdpcm/define/form/brch/br5380Out/br5380Out.jar");
		subfolder = FilenameUtil.getParentPath(subfolder);
		subfolder = FilenameUtil.getName(subfolder);
		System.out.println(subfolder);
	}
}
