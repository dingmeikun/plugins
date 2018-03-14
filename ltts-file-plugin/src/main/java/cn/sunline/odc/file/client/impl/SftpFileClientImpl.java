package cn.sunline.odc.file.client.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import cn.sunline.common.util.StringUtil;
import cn.sunline.ltts.BaseConst;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.exception.LttsServiceException;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.odc.file.client.FileClient;
import cn.sunline.odc.file.model.SftpProtocol;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry;

/**
 * <p>
 * Sftp默认客户端实现：Sftp协议具体实现
 * </p>
 * 
 * @Author Dingmk
 *         <p>
 *         <li>2018年3月1日-上午10:00:00</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class SftpFileClientImpl implements FileClient{
	
	private static BizLog bizlog = BizLogUtil.getBizLog(SftpFileClientImpl.class);
	protected String localEncoding = "GB18030";
	protected String remoteEncoding = "UTF8";
	private static boolean connReachable = false; 
	private static final char COMM_SEPARATOR = '/'; //远程目录分隔符
	private static final String FILE_OK = ".ok";
	
	private SftpProtocol protocolConfig = null;
	
	public SftpFileClientImpl(SftpProtocol protocolConfig){
		if(null != protocolConfig){
			this.protocolConfig = protocolConfig;
		}
	}

	/**
	 * 登录SFTP服务器
	 * @return
	 */
	private ChannelSftp login(){
		ChannelSftp sftp = null;
		String keypath = protocolConfig.getKeyPath();
		try {
			JSch jsch = new JSch();
			if (null != keypath && !"".equals(keypath)) {
	             jsch.addIdentity(protocolConfig.getKeyPath());// 设置私钥
	             bizlog.info("sftp connect,path of private key file：[%s]" , protocolConfig.getKeyPath());
	        }
			Session sshSession = jsch.getSession(protocolConfig.getUserName(), protocolConfig.getServerIp(), protocolConfig.getServerPort());
			bizlog.info("sftp connect by host:[%s] username:[%s]", protocolConfig.getServerIp(), protocolConfig.getUserName());
	        if (protocolConfig.getPassword() != null) {
	    		sshSession.setPassword(protocolConfig.getPassword());
	        }
			sshSession.setTimeout(protocolConfig.getConnTimeoutInMs());
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(config); // 为Session对象设置properties
			sshSession.connect();
			sftp = (ChannelSftp) sshSession.openChannel("sftp");

	        bizlog.info(String.format("sftp server host:[%s] port:[%s] is connect successfull", protocolConfig.getServerIp(), protocolConfig.getServerPort()));
			sftp.connect();
			connReachable = true;
			
			if ((StringUtil.isNotEmpty(protocolConfig.getFilePath()))) {
				sftp.cd(protocolConfig.getFilePath());
			}
		}
		catch (Exception e) {
			disconnect(sftp);
			throw new LttsServiceException("SftpFileClientImpl.login01", "Sftp initialize failed:user=" + protocolConfig.getUserName() 
					+ ",ip=" + protocolConfig.getServerIp() + ",port=" + protocolConfig.getServerPort() 
					+ " Error Message:" + e.getMessage());
		}
		return sftp;
	}
	
	/**
	 * 断开SFTP服务连接
	 * @param sftp
	 */
	private void disconnect(ChannelSftp sftp) {
		connReachable = false;
		try {
			if (sftp != null) {
				sftp.disconnect();
				bizlog.debug("channel disconnect...");
			}
			if (sftp.getSession() != null) {
				sftp.getSession().disconnect();
				bizlog.debug("session disconnect...");
			}
			bizlog.debug("Success to close sftp connect!");
		}
		catch (Exception e) {
			bizlog.method("disconnect end >>>>>>>>>>>>>>>>>>>>");
			throw new LttsServiceException("SftpFileClientImpl.disconnect01", "Sftp disconnect failed:user=" 
					+ protocolConfig.getUserName() + ",ip=" + protocolConfig.getServerIp() + ",port=" 
					+ protocolConfig.getServerPort() + " Error Message:" + e.getMessage());
		}
	}
	
	@Override
	public String getRemoteHome() {
		bizlog.method("getRemoteDirectory begin >>>>>>>>>>>>>>>>>>>>");
		
		String file = protocolConfig.getFilePath();
		
		if (CommUtil.isNotNull(file)) {
			file = file.replace('/', COMM_SEPARATOR);
			file = file.replace('\\', COMM_SEPARATOR);
			if (!isFileSeparator(file.charAt(file.length() - 1)))
				file = file + COMM_SEPARATOR;
		}
		
		bizlog.parm("file [%s]", file);
		bizlog.method("getRemoteDirectory end >>>>>>>>>>>>>>>>>>>>");
		return file;
	}

	@Override
	public String download(String localFileName, String remoteFileName) {
		bizlog.method(" SftpFileClientImpl.download begin >>>>>>>>>>>>>>>>");
		bizlog.parm("localFileName [%s],remoteFileName [%s]", localFileName, remoteFileName);
		long start = System.currentTimeMillis();
		remoteFileName = getremoteFullPathName(getRemoteHome(), remoteFileName);
		
		ChannelSftp sftp = login();
		FileOutputStream fos = null;
		try {
			localPrepare(localFileName);
			remoteFileName = getFileName(remoteFileName);
			fos = new FileOutputStream(localFileName);
			try {
				sftp.get(remoteFileName, fos);
			}
			catch (SftpException e) {
				throw new LttsServiceException("SftpFileClientImpl.download01", "Sftp download file failed!", e);
			}
			if (fos != null) {
				try {
					fos.close();
					fos.flush();
				}
				catch (IOException e) {
					// 是否使用BaseConst常量
					bizlog.error(BaseConst.SimpleFTPClient03, e, new Object[0]);
				}
			}
			bizlog.info(BaseConst.SimpleFTPClient04, new Object[] { protocolConfig.getServerIp(), protocolConfig.getServerPort(), remoteFileName, localFileName, Long.valueOf(System.currentTimeMillis() - start) });

		}
		catch (FileNotFoundException e1) {
			throw new LttsServiceException("SftpFileClientImpl.download02", "File download failed,the local file[" + localFileName + "] does not exist or can not be operated." + e1.getMessage(), e1);
		}
		finally {
			disconnect(sftp);
			if (fos != null) {
				try {
					fos.close();
					fos.flush();
				}
				catch (IOException e) {
					bizlog.error(BaseConst.SimpleFTPClient03, e, new Object[0]);
				}
			}
		}
		bizlog.method(" SftpFileClientImpl.download end >>>>>>>>>>>>>>>>");
		return localFileName;
	}

	@Override
	public void upload(String localFileName, String remoteFileName) {
		bizlog.method(" SftpFileClientImpl.upload begin >>>>>>>>>>>>>>>>");
		bizlog.parm("localFileName [%s],remoteFileName [%s]", localFileName, remoteFileName);
		long start = System.currentTimeMillis();

		ChannelSftp sftp = login();
		FileInputStream in = null;
		try {
			remotePrepare(sftp, remoteFileName);
			in = new FileInputStream(new File(localFileName));
			try {
				sftp.put(in, remoteFileName);
			}
			catch (SftpException e) {
				bizlog.method("upload end >>>>>>>>>>>>>>>>>>>>");
				if (e.getMessage().indexOf("550") != -1) {
					throw new LttsServiceException("SftpFileClientImpl.upload01","file upload failed, [ " + localFileName + " ] not exists");
				}
				else {
					throw new LttsServiceException("SftpFileClientImpl.upload02", "Sftp upload file failed!", e);
				}
			}
			if (in != null) {
				try {
					in.close();
				}
				catch (IOException e) {
					bizlog.error(BaseConst.SimpleFTPClient05, e);
				}

			}
			bizlog.info(BaseConst.SimpleFTPClient04, new Object[] { protocolConfig.getServerIp(), protocolConfig.getUserName(), remoteFileName, localFileName, Long.valueOf(System.currentTimeMillis() - start) });
		}
		catch (FileNotFoundException e) {
			throw new LttsServiceException("SftpFileClientImpl.upload03", "File upload failed, the local file[" + localFileName + "] does not exist or can not be operated." + e.getMessage(), e);
		}
		finally {
			disconnect(sftp);
			if (in != null) {
				try {
					in.close();
				}
				catch (IOException e) {
					bizlog.error(BaseConst.SimpleFTPClient03, e, new Object[0]);
				}
			}
		}
		bizlog.method(" SftpFileClientImpl.upload end >>>>>>>>>>>>>>>>");
	}

	@Override
	public void upload(String localFileName, String remoteFileName,
			boolean uploadOk) {
		bizlog.method("upload begin >>>>>>>>>>>>>>>>>>>>");
		bizlog.debug("localFileName [%s], remoteFileName [%s]", localFileName, remoteFileName);
		
		upload(localFileName, remoteFileName);
		
		if(uploadOk){
			File okFile = new File(localFileName + FILE_OK);
			if (!okFile.exists()) {
				try {
					okFile.createNewFile();
				}
				catch (IOException e) {
					throw new LttsServiceException("SftpFileClientImpl.upload04", "while use FTP, failed to create OK document");
				}
			}
			upload(localFileName + FILE_OK, remoteFileName + FILE_OK);
		}
		
		bizlog.method("upload end >>>>>>>>>>>>>>>>>>>>");
	}
	
	@Override
	public List<String> getRemoteFileList(String remoteDir) {
		return getRemoList(remoteDir, FILE_OK);
	}

	@Override
	public boolean toReachable() {
		return connReachable;
	}

	/**
	 * 
	 * @param sftp
	 * @param remoteFileName
	 */
	public void remotePrepare(ChannelSftp sftp, String remoteFileName) {
		if (new File(remoteFileName).exists()) {
			try {
				sftp.rmdir(getFileName(remoteFileName));
			}
			catch (SftpException e) {
				throw new LttsServiceException("SftpFileClientImpl.remotePrepare01", "Deleting remote file [" + remoteFileName + "] failed", e);
			}
		}
		List<String> dirs = getDirectories(remoteFileName);
		StringBuilder sb;
		if ((dirs != null) && (dirs.size() > 0)) {
			sb = new StringBuilder();
			for (String dir : dirs) {
				dir = dir.replace('\\', '/');
				dir = dir.replace('/', '/');
				sb.append(dir).append("/");
				try {
					  sftp.cd(sb.toString());
				}catch (SftpException e) {
						try {
							sftp.mkdir(getFileName(dir));
							sftp.cd(sb.toString());
						} catch (SftpException e1) {
							throw new LttsServiceException("SftpFileClientImpl.remotePrepare02", "Remote directory [" + sb.toString() + "] creation failed", e1);
						}
				}
			}
		}
	}
	
	private String getFileName(String name) {
		try {
			return new String(name.getBytes(localEncoding), remoteEncoding);
		}
		catch (UnsupportedEncodingException e) {
			throw new LttsServiceException("SftpFileClientImpl.getFileName01", "[" + name + "]" + "conversion failed", e);
		}
	}
	
	public static List<String> getDirectories(String fileName) {
		fileName = fileName.replace('\\', File.separatorChar);
		fileName = fileName.replace('/', File.separatorChar);
		List<String> ret = null;
		boolean abs = false;
		int idx = fileName.indexOf(File.separatorChar);
		if (idx != -1) {
			if (idx == 0) {
				fileName = fileName.substring(idx + 1);
				abs = true;
			}

			idx = fileName.lastIndexOf(File.separatorChar);
			if (idx != -1) {
				fileName = fileName.substring(0, idx);

				ret = StringUtil.split(fileName, File.separatorChar);
				ret.set(0, abs ? File.separatorChar + (String) ret.get(0) : (String) ret.get(0));
			}
		}

		return ret;
	}
	
	public static void localPrepare(String localFileName) {
		List<String> dirs = getDirectories(localFileName);
		if ((dirs != null) && (dirs.size() > 0)) {
			StringBuilder sb = new StringBuilder();
			for (String dir : dirs) {
				sb.append(dir).append(File.separator);
			}
			new File(sb.toString()).mkdirs();
		}
	}
	
	private boolean isFileSeparator(char cha) {
		if (cha == '/' || cha == '\\')
			return true;
		return false;
	}
	
	/**
	 * 根据形参workDir和fileName,获取远程文件全路径
	 * @param workDir
	 * @param fileName
	 * @return
	 */
	private String getremoteFullPathName(String workDir, String fileName) {
		bizlog.method("getFullPathName begin >>>>>>>>>>>>>>>>>>>>");
		bizlog.parm("workDir [%s],fileName [%s]", workDir, fileName);
		if (CommUtil.isNull(workDir)) {
			return fileName;
		}
		else {
			// 2013-11-10 BUG-FIX: 目录未添加文件分隔符，自动添加
			String file = workDir;
			if (!isFileSeparator(file.charAt(file.length() - 1)))
				file = file + File.separatorChar;
			if (CommUtil.isNotNull(fileName) && isFileSeparator(fileName.charAt(0)))
				file = file + fileName.substring(1);
			else
				file = file + fileName;
			bizlog.parm("file [%s]", file);
			bizlog.method("getFullPathName end <<<<<<<<<<<<<<<<<<<<");
			return file;
		}
	}
	
	/**
	 * 
	 * @Author liucong
	 *         <p>
	 *         <li>2017.10.12-20:16:09</li>
	 *         <li>Function Description:</li>
	 *         </p>
	 * @param pathname
	 * @param regs
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<String> getRemoList(String pathname, final String regs) {
		ChannelSftp sftp = login();
		List<String> fileList;
		Vector vste;
		try {
			fileList = new ArrayList<String>();
			sftp.cd(protocolConfig.getFilePath());
			String tmppath = "";
			if (CommUtil.isNotNull(pathname)) {
				tmppath = pathname.replace('\\', '/');
				if(tmppath.startsWith("/"))
					tmppath.replaceFirst("", "");
				sftp.cd(tmppath);
			}
			vste = sftp.ls(sftp.pwd());
			for( Object file: vste){
				if(CommUtil.isNotNull(file) && ((LsEntry) file).getFilename().endsWith(regs)){
					fileList.add(((LsEntry) file).getFilename());
				}
			}
			return fileList;
		}
		catch (SftpException e) {
			throw new LttsServiceException("9013","Remote directory[" + pathname + "] traversal failed", e);
		}
		finally {
			disconnect(sftp);
		}
	}
}
