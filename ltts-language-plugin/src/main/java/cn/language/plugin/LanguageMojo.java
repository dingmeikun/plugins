package cn.language.plugin;

import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import cn.language.engine.LanguageEngine;

/**
 * 
 * @goal Translate
 * 
 * @phase install
 */
public class LanguageMojo extends AbstractMojo{

	/**
	 * @parameter expression="${filepath}"
	 * 
	 */
	private String filepath;
	
	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;
	
	private static Map<String, Object> arguments = new HashMap<String, Object>();

	public void execute() throws MojoExecutionException, MojoFailureException {
		if(filepath != null && !filepath.equals("")){
			//存在CSV文件和Excel文件
			arguments.put("filePath", filepath);
		}
		arguments.put("project", project);
		
		//process
		LanguageEngine.Main(arguments);
	}
}
