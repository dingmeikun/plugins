package cn.rdp.mergefile.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.MavenProject;

import com.cxsw.logger.CXLogger;
import com.cxsw.utils.FileUtil;
import com.cxsw.utils.FilenameUtil;

public class Mergefile {
	
	public static String default_path = "";

	public static void Merge(MavenProject project)
	{
		String basedir = project.getBasedir().getAbsolutePath();
		String sourcedir = project.getBuild().getSourceDirectory();
		default_path = basedir + File.separator + "WebRoot";
		String path = sourcedir + File.separator + "form" + File.separator + "conf";
		
		//TODO 判断是否符合项目生成结构
		if((new File(path)).exists() && (new File(default_path)).exists())
		{
			System.out.println("path:" + path + "\ndefault_path:" + default_path);
			genMergeFiles(path);
		}
	}
	
	private static void genMergeFiles(String path)
	{
		//clear 清楚文件目录
		String clearpath = default_path + File.separator + "composite";
		recurDelete(new File(clearpath));
		
		File file = new File(path);
		for (String child : file.list())
		{
			if(child.contains(".js")) continue;
			if(child.contains(".svn")) continue;
			String filepath = FilenameUtil.concat(path, child);
			//读取txt内容
			String context = doMergeFiles(filepath);
			
			String filename = child.replace(".txt", ".js").replaceAll("\\s+", "");
			
			FileUtil.write(clearpath + File.separator + filename, context, false);
		}
		CXLogger.detial("statu:[%s]", "success");
	}
	
	private static String doMergeFiles(String path)
	{
		StringBuffer sb = new StringBuffer();
		String source = default_path;
		List<String> lines = new ArrayList<String>();
		FileUtil.readLines(lines, path);	
		for(int i=0; i<lines.size(); i++)
		{
			String filepath = source + lines.get(i);
			String context = FileUtil.readFileToString(filepath);
			sb.append(context + "\n");
		}
		return sb.toString();
	}
	
	/**
	 * 清空文件夹文件
	 * @param dir
	 */
	public static void recurDelete(File dir)
	{
	    for(File file: dir.listFiles())
	    {
	        if(file.isDirectory())
	        {
	            recurDelete(file);
	        }
	        else{
	        	file.delete();
	        }
	    }
	    //存在风险，父文件夹下，需要不存在子文件夹
	    //f.delete();
	}
	
	/**
	 * test
	 * @param args
	 */
	public static void main(String[] args) {
		String path = "H:/交易服务平台/项目备份/basefrontserver/RdpOverseas/src/main/java/form/conf";
		default_path = "H:/交易服务平台/项目备份/basefrontserver/RdpOverseas/WebRoot";
		genMergeFiles(path);
	}
}
