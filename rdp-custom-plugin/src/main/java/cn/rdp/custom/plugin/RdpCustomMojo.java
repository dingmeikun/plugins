package cn.rdp.custom.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import cn.rdp.custom.form.tools.CxPluginHTMLWidgetBatch;

/**
 * 
 * @goal RdpCustom
 * 
 * @phase package
 */
public class RdpCustomMojo extends AbstractMojo{

	/**
	 * @parameter expression="${logfile}"
	 * 
	 */
	private String logfile;
	
	/**
	 * @parameter expression="${threadCount}"
	 * 
	 */
	private int threadCount = 1;
	
	/**
	 * @parameter expression="${definePath}"
	 * 
	 */
	private String definePath;
	
	/**
	 * @parameter expression="${skipError}"
	 * 
	 */
	private String skipError;
	
	/**
	 * @parameter expression="${project.build.outputDirectory}"
	 * @required
	 * @readonly
	 */
	private String target;
	
	/**
	 * @parameter expression="${project.basedir}"
	 * @required
	 * @readonly
	 */
	private File basedir;
	
	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;
	
	private static Map<String, Object> args = new HashMap<String, Object>();
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		if(definePath != null && !definePath.equals("") && (new File(definePath)).exists()){
			target = definePath;
		}else{
			getLog().info("未配置文件输出路径！默认生成到target/classes目录！");
		}
		args.put("project", project);
		args.put("workspace", basedir.getParentFile().getAbsolutePath());
		args.put("targetPath", target);
		args.put("logfile", logfile);
		args.put("skipError", skipError);
		args.put("threadcount", threadCount);
		CxPluginHTMLWidgetBatch.Main(args);
	}

}
