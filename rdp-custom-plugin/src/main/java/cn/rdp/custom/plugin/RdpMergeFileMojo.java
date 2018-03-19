package cn.rdp.custom.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import cn.rdp.mergefile.tools.Mergefile;

/**
 * 
 * @goal RdpMergeFile
 * 
 * @phase package
 */
public class RdpMergeFileMojo extends AbstractMojo{

	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException 
	{
		//process merge
		Mergefile.Merge(project);
	}
}
