package cn.rdp.custom.form.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import cn.rdp.custom.form.api.CxPluginFormBase;

import com.cxsw.bean.CxNode;
import com.cxsw.io.CxFileFilterAdapter;
import com.cxsw.logger.CXLogger;
import com.cxsw.utils.FileUtil;
import com.cxsw.utils.FilenameUtil;
import com.cxsw.utils.InvokeUtil;
import com.cxsw.utils.LangUtil;
import com.cxsw.utils.ValueUtil;

public class CxPluginHTMLWidgetBatch extends CxPluginForm2Widget
{
	private static int threadcount;
	private static String logfile;
	private static String workspace;
	private static boolean isSkip = false;
	private static CountDownLatch countDownLatch = null;
	private static Properties conf = new Properties();
	
	private static final String CONFIG_PROJECT = "configproject";
	
	public static void Main(Map<String,Object> args) throws MojoFailureException
	{
		//获取MavenProject对象，获取当前install项目模块
		MavenProject project = (MavenProject) args.get("project");
		workspace = project.getBasedir().getParentFile().getAbsolutePath();
		String targetPath = args.get("targetPath").toString();
		
		//获取错误日志文件默认存放路径
		logfile = project.getBuild().getDirectory() + File.separator
				+ "log" + File.separator + project.getArtifactId() + "_error.log";
		FileUtil.delete(logfile);
		Object log = args.get("logfile");
		if(log != null)
		{
			logfile = log.toString();
			CXLogger.detial("logfile:[%s]", logfile);
		}
		//获取处理线程数
		threadcount = ValueUtil.intValue(args.get("threadcount"));
		CXLogger.detial("threadcount:[%s]",threadcount);
		//FileUtil.delete(logfile);
		
		//是否跳过异常
		if(args.get("skipError") != null && !args.get("skipError").toString().equals("")){
			isSkip = Boolean.valueOf(args.get("skipError").toString());
			CXLogger.detial("isSkip:[%s]",isSkip);
		}
		
		/**
		 * 执行所有匹配的文件
		 */
		String source = project.getBasedir().getAbsolutePath() + File.separator + "define";
		String config = project.getBasedir().getAbsolutePath() + File.separator + "config";
		if((new File(source)).exists())
		{
			if((new File(source + File.separator + "resource")).exists())
			{
				String filePath = source + File.separator + "resource" + File.separator + "list";
				try 
				{
					CxRdpList2WebJs.paserBaseFile(new File(filePath), targetPath);
				} catch (IOException exp) 
				{
					exp.printStackTrace();
					String error = LangUtil.getStackTraceString(exp);
					FileUtil.write(logfile, error + "\r\n\r\n", true);
				}
			}
			if((new File(source + File.separator + "form")).exists())
			{
				if((new File(config + File.separator + "cxsw.cfg")).exists())
				{
					//读取配置信息
					try 
					{
						FileInputStream in = new FileInputStream(config + File.separator + "cxsw.cfg");
						conf.load(in);
					} catch (IOException exp) 
					{
						exp.printStackTrace();
						String error = LangUtil.getStackTraceString(exp);
						FileUtil.write(logfile, error + "\r\n\r\n", true);
					}
					String devhome = workspace + File.separator + conf.getProperty(CONFIG_PROJECT);
					//process
					transfer(devhome, source, targetPath);
				}
			}
		}
		else return;
	}
	
	public static void transfer(String devhome, String path, String targetPath)throws MojoFailureException
	{
		final LinkedList<String> links = new LinkedList<String>();
		final CxPluginHTMLWidgetBatch formWidget = new CxPluginHTMLWidgetBatch();
		formWidget.devhome = devhome;
		formWidget.target = targetPath;
		CXLogger.detial("path:[%s]", path);
		FileUtil.listFolder(path, new CxFileFilterAdapter()
		{
			@Override
			public boolean validateFolder(String folderName)
			{
				if (folderName.endsWith(".svn")) return false;
				return super.validateFolder(folderName);
			}

			@Override
			public void display(String parent, String name)
			{
				if (!FilenameUtil.wildcardMatchs(name, "*.form")) return;
				links.add(FilenameUtil.concat(parent, name)); 
				CXLogger.detial("formfile:[%s]",FilenameUtil.concat(parent, name));
			}
		});
		//executeClear(targetPath + File.separator +"tran", links);
		formWidget.executeHtml(threadcount, links);
			
	}
	
	public static void executeClear(String folder, final LinkedList<String> sets)
	{
		Iterator<String> files = sets.iterator();
		while(files.hasNext())
		{
			String formfile = files.next();
			try
			{
				String name = FilenameUtil.getName(formfile);
				final String formid = FilenameUtil.getFilenameid(name);
				final String clearForder = folder + File.separator + formid;
				FileUtil.listFolder(clearForder, new CxFileFilterAdapter()
				{
					@Override
					public boolean validateFolder(String folderName) {
						if (folderName.endsWith(".svn")) return false;
						return super.validateFolder(folderName);
					}

					@Override
					public void display(String parent, String name)
					{
						String filenameid = FilenameUtil.getFilenameid(name);
						// filenameid dm000In
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
						FileUtil.delete(parent);
					}
				});
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
				String aaa = LangUtil.getStackTraceString(e);
				FileUtil.write(logfile, aaa + "\r\n\r\n", true);
			}
		}
		//FileUtil.write(logfile, "********** 执行前清理完成,开始统计错误日志  ************" + "\r\n\r\n", true);
	}

	public void executeHtml(int thread, final LinkedList<String> sets) throws MojoFailureException
	{
		final CxPluginFormBase formApi = new CxPluginFormBase() {
			
			@Override
			public String getFlexSDK() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		//同步辅助类，在完成子线程操作之前，允许其余线程等待，实现线程间的同步
		countDownLatch = new CountDownLatch(sets.size());//size：任务数
		for (int i = 0; i < thread; i++)
		{
			final CxPluginForm2Html form2html = new CxPluginForm2Html();
			
			InvokeUtil.asyncInvoke(new Runnable()
			{
				@Override
				public void run()
				{
					while (true)
					{
						String formfile = null;
						synchronized (sets)
						{
							if(sets.size() > 0)
								formfile = sets.removeFirst();
							else
								break ;
						}
						try
						{
							String name = FilenameUtil.getName(formfile);
							final String formid = FilenameUtil.getFilenameid(name);
							
							//Set<String> grids = new HashSet<String>();
							Map<String, CxNode> nodes = new HashMap<String, CxNode>();
//							formApi.genFormHtml(devhome, formfile,formid, nodes); 
							String htmlcontext = form2html.transferFormWeb(devhome, formfile, formid, nodes);
							String project = getProject(formfile);
							
							try
							{
								formfile = formfile.replace(".form", ".html");
								CXLogger.detial("formpath:[%s]", formfile);
								
								String targetpath = target + File.separator + "tran" + File.separator + formid + File.separator + formfile.substring(formfile.indexOf(formid));
								FileUtil.writeStringToFile(new File(targetpath), htmlcontext, "UTF-8");
							} catch (IOException e) {
								e.printStackTrace();
							}
								
							project = FilenameUtil.concat(workspace, project);
							formApi.genFormGridJs(devhome, project, target, formfile, formid, nodes);
							formApi.genFormJavaScriptCode(devhome, project, target, formfile, formid, nodes.keySet());
						}
						catch (Exception e)
						{
							e.printStackTrace();
							String aaa = LangUtil.getStackTraceString(e);
							FileUtil.write(logfile, formfile + "\r\n", true);
							FileUtil.write(logfile, aaa + "\r\n\r\n", true);
						}
						//当前线程调用此方法，计数减一
						countDownLatch.countDown();
					}
				}
			});
		}
		try {
			//调用此方法会一直阻塞当前[主]线程，直到计时器的值为0，释放等待线程
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		/**
		 * 是否跳过异常
		 */
		if(!isSkip)
		{
			if((new File(logfile)).exists())
			{
				throw new MojoFailureException(getFileStream(logfile));
			}
		}
	}
	
	public String getProject(String formfile)
	{
		String project = formfile.substring(workspace.length()+1);
		project = project.substring(0, project.indexOf("/"));
		return project;
	}
	/**
	 * 读取错误日志文件
	 * @param logfile 错误日志
	 * @return
	 */
	public static String getFileStream(String logfile)
	{
		File file = new File(logfile);
		BufferedReader reader = null;
		StringBuffer error = new StringBuffer();
		try 
		{
			reader = new BufferedReader(new FileReader(file));
			error.append("\n");
			String filetemp = null;
			while((filetemp=reader.readLine()) != null)
			{
				error.append(filetemp).append("\n");
			}
			reader.close();
		} catch (IOException e) 
		{
            e.printStackTrace();
        } 
		finally 
        {
            if (reader != null) 
            {
                try {
                    reader.close();
                } catch (IOException e1) {}
            }
        }
		return error.toString();
	}
}
