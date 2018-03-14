package cn.odc.shell.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import cn.odc.shell.replacer.Replacement;
import cn.odc.shell.replacer.ReplacementProcessor;
import cn.odc.shell.util.FileUtils;

/**
 * 
 * @goal genShell
 * 
 * @phase generate-sources
 */
public class GenShellMojo extends AbstractMojo
{
  public static final String ENCODING = "UTF-8";
  private static final String TOOLS_VERSION = "toolsVersion";
  private static final String FRW_VERSION = "frwVersion";
  private static final String APP_VERSION = "appVersion";
  private static final String EQ = "=";
  
  private static final String[] LTTS_SUB_CMDS = { "dump", "help", "restart", "start", "stop", "status", "console", "consoled" };
  private static final String[] REPLACED_FILE = { "/tpl/bin/ltts", "/tpl/bin/ltts.conf" };
  
  /**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;
	/**
	 * @parameter expression="${settingName}" default-value="settings.properties"
	 * @required
	 */
	private String settingName;
	/**
	 * @parameter expression="${needSettingFile}" default-value="true"
	 */
	private String needSettingFile;
	/**
	 * @parameter expression="${logConfigName}" default-value="ltts_log_server.xml"
	 */
	private String logConfigName;
	/**
	 * @parameter expression="${pluginConfigName}" default-value="plugin-global-conf.properties"
	 */
	private String pluginConfigName;
	/**
	 * @parameter expression="${mainClass}" default-value="cn.sunline.edsp.microcore.Bootstrap"
	 */
	private String mainClass;
	/**
	 * @parameter expression="${vmOpts}" default-value=""
	 * @required
	 */
	private String vmOpts;
	/**
	 * @parameter expression="${stopTimeout}" default-value="30"
	 */
	private String stopTimeout;
	/**
	 * @parameter expression="${startOkFlag}" default-value="SystemStatus:Started"
	 */
	private String startOkFlag;
	/**
	 * @parameter expression="${startErrFlag}" default-value="SystemStatus:Error"
	 */
	private String startErrFlag;
	/**
	 * @parameter expression="${comOpts}" default-value="-server -Xms5120m -Xmx5120m -XX:PermSize=512m -XX:MaxPermSize=1024m -XX:+UseFastAccessorMethods -XX:+UseCompressedOops"
	 */
	private String comOpts;
	/**
	 * @parameter expression="${gcOpts}" default-value="-XX:+DisableExplicitGC -XX:+ExplicitGCInvokesConcurrent -XX:ParallelGCThreads=10 -XX:-UseAdaptiveSizePolicy -Xmn1024m -XX:SurvivorRatio=6 -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCApplicationConcurrentTime -verbose:gc -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -XX:-TraceClassUnloading -XX:+PrintGCDetails"
	 */
	private String gcOpts;/**
	 * @parameter expression="${jmxOpts}" default-value="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=7091 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"
	 */
	private String jmxOpts;
	/**
	 * @parameter expression="${logOpts}" default-value="-Dlog4j2.is.webapp=false -Dlog4j2.enable.threadlocals=true -DenableLog=true -Dlog4j.initialReusableMsgSize=300 -DAsyncLoggerConfig.RingBufferSize=524288"
	 */
	private String logOpts;
	/**
	 * @parameter expression="${otherOpts}" default-value=""
	 */
	private String otherOpts;
	/**
	 * @parameter expression="${enableComOpts}" default-value="true"
	 */
	private String enableComOpts;
	/**
	 * @parameter expression="${enableGcOpts}" default-value="true"
	 */
	private String enableGcOpts;
	/**
	 * @parameter expression="${enableJmxOpts}" default-value="true"
	 */
	private String enableJmxOpts;
	/**
	 * @parameter expression="${enableLogOpts}" default-value="true"
	 */
	private String enableLogOpts;
	/**
	 * @parameter expression="${enableOtherOpts}" default-value="true"
	 */
	private String enableOtherOpts;
	/**
	 * @parameter expression="${enableInternational}" default-value="true"
	 */
	private String enableInternational;
	/**
	 * @parameter expression="${languageConf}" default-value="en_US"
	 */
	private String languageConf;
  
  public void execute()
    throws MojoExecutionException, MojoFailureException
  {
    String outputBaseDir = this.project.getBasedir() + "/target/dist";
    getLog().info("output dir : " + outputBaseDir);
    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    System.out.println("default classloader=" + contextClassLoader);
    try
    {
      copySubcmdWithMkdir(outputBaseDir);
      
      copyMainLttsWithReplace(outputBaseDir);
    }
    catch (IOException e1)
    {
      throw new RuntimeException(e1.getMessage(), e1);
    }
  }
  
  /**
   * 将各个配置文件中的参数，设置到ltts ltts.conf文件中
   * 
   * @param outputBaseDir baseDir/target/dist
   */
  private void copyMainLttsWithReplace(String outputBaseDir)
  {
    getLog().info("settingName : " + this.settingName);
    getLog().info("needSettingFile : " + this.needSettingFile);
    getLog().info("logConfigName : " + this.logConfigName);
    getLog().info("mainClass : " + this.mainClass);
    getLog().info("vmOpts : " + this.vmOpts);
    getLog().info("stopTimeout : " + this.stopTimeout);
    getLog().info("pluginConfigName : " + this.pluginConfigName);
    getLog().info("startOkFlag : " + this.startOkFlag);
    getLog().info("startErrFlag : " + this.startErrFlag);
    getLog().info("comOpts : " + this.comOpts);
    getLog().info("enableComOpts : " + this.enableComOpts);
    getLog().info("gcOpts : " + this.gcOpts);
    getLog().info("enableGcOpts : " + this.enableGcOpts);
    getLog().info("jmxOpts : " + this.jmxOpts);
    getLog().info("enableJmxOpts : " + this.enableJmxOpts);
    getLog().info("logOpts : " + this.logOpts);
    getLog().info("enableLogOpts : " + this.enableLogOpts);
    getLog().info("otherOpts : " + this.otherOpts);
    getLog().info("enableOtherOpts : " + this.enableOtherOpts);
    getLog().info("enableInternational : " + this.enableInternational);
    getLog().info("languageConf : " + this.languageConf);
    
    List<Replacement> replaces = new ArrayList<>();
    Replacement settingRep = new Replacement("_settingName", this.settingName);
    Replacement needSettingFileRep = new Replacement("_needSettingFile", this.needSettingFile);
    Replacement loggerRep = new Replacement("_logConfigName", this.logConfigName);
    Replacement pluginRep = new Replacement("_pluginConfigName", this.pluginConfigName);
    Replacement mainClassRep = new Replacement("_mainClass", this.mainClass);
    Replacement vmOptsRep = new Replacement("_vmOpts", this.vmOpts);
    Replacement stopTimeoutRep = new Replacement("_stopTimeout", this.stopTimeout);
    Replacement startOkFlagRep = new Replacement("_startOkFlag", this.startOkFlag);
    Replacement startErrFlagRep = new Replacement("_startErrFlag", this.startErrFlag);
    Replacement enableInternational = new Replacement("_enableInternational", this.enableInternational);
    Replacement languageConf = new Replacement("_languageConf", this.languageConf);
    
    Replacement comOptsRep = new Replacement("_comOpts", this.comOpts);
    Replacement gcOptsRep = new Replacement("_gcOpts", this.gcOpts);
    Replacement jmxOptsRep = new Replacement("_jmxOpts", this.jmxOpts);
    Replacement logOptsRep = new Replacement("_logOpts", this.logOpts);
    Replacement otherOptsRep = new Replacement("_otherOpts", this.otherOpts);
    
    Replacement enableComOptsRep = new Replacement("_enableComOpts", this.enableComOpts.equals("true") ? "" : "#");
    Replacement enableGcOptsRep = new Replacement("_enableGcOpts", this.enableGcOpts.equals("true") ? "" : "#");
    Replacement enableJmxOptsRep = new Replacement("_enableJmxOpts", this.enableJmxOpts.equals("true") ? "" : "#");
    Replacement enableLogOptsRep = new Replacement("_enableLogOpts", this.enableLogOpts.equals("true") ? "" : "#");
    Replacement enableOtherOptsRep = new Replacement("_enableOtherOpts", this.enableOtherOpts.equals("true") ? "" : "#");
    
    replaces.add(settingRep);
    replaces.add(needSettingFileRep);
    replaces.add(loggerRep);
    replaces.add(pluginRep);
    replaces.add(mainClassRep);
    replaces.add(vmOptsRep);
    replaces.add(stopTimeoutRep);
    replaces.add(startOkFlagRep);
    replaces.add(startErrFlagRep);
    replaces.add(enableInternational);
    replaces.add(languageConf);
    replaces.add(comOptsRep);
    replaces.add(gcOptsRep);
    replaces.add(jmxOptsRep);
    replaces.add(logOptsRep);
    replaces.add(otherOptsRep);
    replaces.add(enableComOptsRep);
    replaces.add(enableGcOptsRep);
    replaces.add(enableJmxOptsRep);
    replaces.add(enableLogOptsRep);
    replaces.add(enableOtherOptsRep);
    
    ReplacementProcessor replacementProcessor = new ReplacementProcessor();
    try
    {
      for (int i = 0; i < REPLACED_FILE.length; i++)
      {
        String sourceFile = REPLACED_FILE[i];
        String destFileName = sourceFile.substring(sourceFile.lastIndexOf("/") + 1);
        replacementProcessor.replace(replaces, REPLACED_FILE[i], outputBaseDir + "/bin" + File.separator + destFileName);
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
      throw new RuntimeException("[ERROR] replace token error : " + e.getMessage(), e);
    }
  }
  
  /**
   * 生成/etc /lib /bin 文件夹，及其内部txt文件
   * 
   * @param outputBaseDir baseDir/target/dist
   * @throws IOException
   */
  private void copySubcmdWithMkdir(String outputBaseDir)
    throws IOException
  {
	  File target_etc = new File(outputBaseDir + "/etc");
	    target_etc.mkdirs();
	    getLog().info("create dir " + outputBaseDir + "/etc");
	    
	    File target_lib = new File(outputBaseDir + "/lib");
	    target_lib.mkdirs();
	    getLog().info("create dir " + outputBaseDir + "/lib");
	    for (int i = 0; i < LTTS_SUB_CMDS.length; i++)
	    {
	      String target_path = outputBaseDir + "/bin/subcmd" + File.separator + LTTS_SUB_CMDS[i];
	      File target_bin_subcmd = new File(target_path);
	      target_bin_subcmd.mkdirs();
	      getLog().info("create dir " + target_path);
	    }
	    for (int i = 0; i < LTTS_SUB_CMDS.length; i++)
	    {
	      String cmdSource = "/tpl/bin/subcmd" + File.separator + LTTS_SUB_CMDS[i] + File.separator + LTTS_SUB_CMDS[i];
	      cmdSource = cmdSource.replace("\\", "/");
	      String cmdTarget = outputBaseDir + "/bin/subcmd" + File.separator + LTTS_SUB_CMDS[i] + File.separator + LTTS_SUB_CMDS[i];
	      getLog().info("copy subCmd from " + cmdSource + " to " + cmdTarget);
	      FileUtils.copyFile(cmdSource, cmdTarget, ENCODING);
	      
	      String manSource = "/tpl/bin/subcmd" + File.separator + LTTS_SUB_CMDS[i] + File.separator + "man.txt";
	      manSource = manSource.replace("\\", "/");
	      String manTarget = outputBaseDir + "/bin/subcmd" + File.separator + LTTS_SUB_CMDS[i] + File.separator + "man.txt";
	      getLog().info("copy cmd main.txt from " + manSource + " to " + manTarget);
	      FileUtils.copyFile(manSource, manTarget, ENCODING);
	    }
	    
	    Properties properties = project.getProperties();
	  	StringBuffer buffer = new StringBuffer();
	  	buffer.append(TOOLS_VERSION + EQ + properties.getProperty("ltts-shell.version") + "\r\n")
	  		.append(FRW_VERSION + EQ + properties.getProperty("edsp-solution.version") + "\r\n")
	  		.append(APP_VERSION + EQ + properties.getProperty("app-project.version"));
	    String versionPath = outputBaseDir + "/etc" + File.separator +  ".version";
	    FileUtils.writeToFile(versionPath, buffer.toString(), ENCODING);
	    getLog().info("create .version file");
  }
}
