package cn.sunline.odc.ftp.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;

import cn.sunline.common.util.StringUtil;
import cn.sunline.ltts.BaseConst;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.exception.LttsServiceException;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.odc.file.client.FileClient;
import cn.sunline.odc.ftp.comm.FtpProtocol;

/**
 * <p>
 * Ftp默认实现：Ftp协议具体实现
 * </p>
 * 
 * @Author Dingmk
 *         <p>
 *         <li>2018年3月1日-上午10:00:00</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class FtpFileClientImpl implements FileClient{
	
	private static BizLog bizlog = BizLogUtil.getBizLog(FtpFileClientImpl.class);
	private Boolean binaryMode = Boolean.valueOf(true);
	protected String localEncoding = "GB18030";
	protected String remoteEncoding = "ISO8859-1";
	private static boolean connReachable = false; 
	private static final char COMM_SEPARATOR = '/';
	private static final String FILE_OK = ".ok";
	
	private  FtpProtocol protocolConfig = null;
	
	public FtpFileClientImpl(FtpProtocol protocolConfig){
		if(null != protocolConfig){
			this.protocolConfig = protocolConfig;
		}
	}

	/**
	 * FTP客户端登录
	 */
	private FTPClient login() {
		FTPClient ftp = new FTPClient();
		try {
			connect(ftp, protocolConfig.getRetryTime());

			ftp.setSoTimeout(protocolConfig.getDataTimeoutInMs());
			ftp.setControlEncoding(remoteEncoding);

			if (!ftp.login(protocolConfig.getUserName(), protocolConfig.getPassword())) {
				throw new LttsServiceException("FtpFileClientImpl.download01", "Logon FTP server failed：user=" + protocolConfig.getUserName() + ",password=" + protocolConfig.getPassword());
			}
			if ((StringUtil.isNotEmpty(protocolConfig.getFilePath())) && (!ftp.changeWorkingDirectory(protocolConfig.getFilePath()))) {
				throw new LttsServiceException("FtpFileClientImpl.download02", "FTP server working directory change failed：" + protocolConfig.getFilePath());
			}
			if ((binaryMode != null) && (!binaryMode.booleanValue())) {
				if (!ftp.setFileType(0)) {
					throw new LttsServiceException("FtpFileClientImpl.download03", "FTP file transfer type setting failed：0");
				}
			}
			else if (!ftp.setFileType(2)) {
				throw new LttsServiceException("FtpFileClientImpl.download04", "FTP file transfer type setting failed：2");
			}
			connReachable = true;
			ftp.setBufferSize(1048576);
		}
		catch (SocketException e) {
			connReachable = false;
			throw new LttsServiceException("FtpFileClientImpl.download05", "FTP connection error", e);
		}
		catch (IOException e) {
			connReachable = false;
			throw new LttsServiceException("FtpFileClientImpl.download06", "FTP connection error", e);
		}
		return ftp;
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
		bizlog.method("download begin >>>>>>>>>>>>>>>>>>>>");
		bizlog.parm("localFileName [%s],remoteFileName [%s]", localFileName, remoteFileName);
		remoteFileName = getremoteFullPathName(getRemoteHome(), remoteFileName);
		if(!new File(localFileName).getParentFile().exists())
			new File(localFileName).getParentFile().mkdirs();
		if(!new File(remoteFileName).getParentFile().exists())
			new File(remoteFileName).getParentFile().mkdirs();
		
		long start = System.currentTimeMillis();
		FTPClient ftp = login();

		localPrepare(localFileName);
		try {
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			try {
				fos = new FileOutputStream(localFileName);
				bos = new BufferedOutputStream(fos);
				remoteFileName = getFileName(remoteFileName);
				if (!ftp.retrieveFile(remoteFileName, bos)) {
					throw new LttsServiceException("FtpFileClientImpl.download01", "File[" + remoteFileName + "] download failed," + ftp.getReplyString());
				}

				bizlog.info(BaseConst.SimpleFTPClient02, new Object[] { protocolConfig.getServerIp(), protocolConfig.getUserName(), remoteFileName, localFileName, Long.valueOf(System.currentTimeMillis() - start) });

				try {
					if (bos != null) {
						bos.flush();
						bos.close();
					}

					if (fos != null) {
						fos.flush();
						fos.close();
					}
				}
				catch (Exception e) {
					bizlog.error(BaseConst.SimpleFTPClient03, e, new Object[0]);
					if(e.getMessage().indexOf("550") != -1){
						throw new LttsServiceException("FtpFileClientImpl.download02", "file load failed, [" + remoteFileName + "] not exists");
					}
					else if(e.getCause()!=null){
						throw new LttsServiceException("FtpFileClientImpl.download03", e.getCause().toString());
					}
					else{
						throw new LttsServiceException("FtpFileClientImpl.download04", e.toString());
					}
				}
			}
			catch (FileNotFoundException e) {
				throw new LttsServiceException("FtpFileClientImpl.download05", "File download failed,the local file[" + localFileName + "] does not exist or can not be operated." + e.getMessage(), e);
			}
			catch (Exception e) {
				throw new LttsServiceException("FtpFileClientImpl.download06", "File download failed", e);
			}
			finally {
				try {
					if (bos != null) {
						bos.flush();
						bos.close();
					}

					if (fos != null) {
						fos.flush();
						fos.close();
					}
				}
				catch (Exception e) {
					bizlog.error(BaseConst.SimpleFTPClient03, e, new Object[0]);
				}
			}
		}
		finally {
			logoff(ftp);
		}
		bizlog.method("download end >>>>>>>>>>>>>>>>>>>>");
		return localFileName;
	}

	@Override
	public void upload(String localFileName, String remoteFileName) {
		bizlog.method("upload begin >>>>>>>>>>>>>>>>>>>>");
		bizlog.parm("localFileName [%s],remoteFileName [%s]", localFileName, remoteFileName);
		remoteFileName = getremoteFullPathName(getRemoteHome(), remoteFileName);
		
		long start = System.currentTimeMillis();
		FTPClient ftp = login();

		remotePrepare(ftp, remoteFileName);
		try {
			BufferedInputStream in = null;
			try {
				in = new BufferedInputStream(new FileInputStream(localFileName));
				if (!ftp.storeFile(getFileName(remoteFileName), in)) {
					throw new LttsServiceException("FtpFileClientImpl.upload01", "Uploading files to FTP server failed: " + ftp.getReplyString());
				}

				bizlog.info(BaseConst.SimpleFTPClient04, new Object[] { protocolConfig.getServerIp(), protocolConfig.getUserName(), remoteFileName, localFileName, Long.valueOf(System.currentTimeMillis() - start) });

				if (in != null) {
					try {
						in.close();
					}
					catch (Exception e) {
						bizlog.error(BaseConst.SimpleFTPClient05);
					}
				}
			}
			catch (FileNotFoundException e) {
				throw new LttsServiceException("FtpFileClientImpl.upload02", "File upload failed, the local file[" + localFileName + "] does not exist or can not be operated." + e.getMessage(), e);
			}
			catch (Exception e) {
				if(e.getMessage().indexOf("550") != -1){
					throw new LttsServiceException("FtpFileClientImpl.upload03", "file load failed, [ " + localFileName + " ] not exists");
				}else{
					throw new LttsServiceException("FtpFileClientImpl.upload04", "UPloading files to FTP server failed," + e.getMessage(), e);
				}
			}
			finally {
				if (in != null) {
					try {
						in.close();
					}
					catch (Exception e) {
						bizlog.error(BaseConst.SimpleFTPClient05);
					}
				}
			}
		}
		finally {
			logoff(ftp);
		}
		
		bizlog.method("upload end >>>>>>>>>>>>>>>>>>>>");
	}

	@Override
	public void upload(String localFileName, String remoteFileName,
			boolean uploadOk) {
		bizlog.method("upload begin >>>>>>>>>>>>>>>>>>>>");
		bizlog.debug("localFileName [%s], remoteFileName [%s]", localFileName, remoteFileName);
		
		upload(localFileName, remoteFileName);
		
		if (uploadOk) {
			File okFile = new File(localFileName + FILE_OK);
			if (!okFile.exists()) {
				try {
					okFile.createNewFile();
				}
				catch (IOException e) {
					throw new LttsServiceException("FtpFileClientImpl.upload05", "while FTP, failed to create OK document");
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
	 * 客户端连接、重连
	 * @param ftp
	 * @param retryTime
	 */
	private void connect(FTPClient ftp, int retryTime) {
		int retryCount = 0;
		try {
			ftp.setDefaultTimeout(protocolConfig.getConnTimeoutInMs());
			ftp.setDataTimeout(protocolConfig.getDataTimeoutInMs());
			ftp.setConnectTimeout(protocolConfig.getConnTimeoutInMs());
			ftp.connect(protocolConfig.getServerIp(), protocolConfig.getServerPort());
			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				throw new LttsServiceException("FtpFileClientImpl.connect01", "Can't Connect to :" + protocolConfig.getServerIp());
			}

			return;
		}
		catch (Exception e) {
			while (retryCount < retryTime) {
				try {
					Thread.sleep(protocolConfig.getRetryInterval());
				}
				catch (InterruptedException e1) {
				}
				retryCount++;
			}
			logoff(ftp);
			throw new LttsServiceException("FtpFileClientImpl.connect02", e.getMessage());
		}
	}
	
	/**
	 * FTP连接登出
	 * @param ftp
	 */
	private void logoff(FTPClient ftp) {
		connReachable = false;
		if (ftp.isConnected()) {
			try {
				ftp.logout();
			}
			catch (Exception e) {
				bizlog.error("logout fail", e, new Object[0]);
			}
			try {
				ftp.disconnect();
			}
			catch (Exception e) {
				bizlog.error(BaseConst.SimpleFTPClient01);
			}
		}
	}
	
	private String getFileName(String name) {
		try {
			return new String(name.getBytes(localEncoding), remoteEncoding);
		}
		catch (UnsupportedEncodingException e) {
			throw new LttsServiceException("FtpFileClientImpl.getFileName01", "[" + name + "]" + "conversion failed", e);
		}
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
	
	public void remotePrepare(FTPClient client, String remoteFileName) {
		try {
			client.deleteFile(getFileName(remoteFileName));
		}
		catch (IOException e) {
			throw new LttsServiceException("FtpFileClientImpl.remotePrepare01", "Deleting remote file[" + remoteFileName + "] failed", e);
		}

		List<String> dirs = getDirectories(remoteFileName);
		StringBuilder sb;
		if ((dirs != null) && (dirs.size() > 0)) {
			sb = new StringBuilder();
			for (String dir : dirs) {
				sb.append(dir).append("/");
				try {
					client.makeDirectory(getFileName(sb.toString()));

				}
				catch (IOException e) {
					throw new LttsServiceException("FtpFileClientImpl.remotePrepare02", "Remote directory" + sb.toString() + "creation failed", e);
				}
			}
		}
	}
	
	/**
	 * @Author liuzf@sunline.cn
	 *         <p>
	 *         <li>2017年4月7日-下午3:30:17</li>
	 *         <li>功能说明：根据文件规则遍历远程目录</li>
	 *         </p>
	 * @param pathname
	 * @param regs
	 * @return
	 */
	public List<String> getRemoList(String pathname, final String regs) {
		FTPClient ftpClient = login();
		FTPFile[] ftpFiles = null;

		try {
			String tmppath = "";
			if (CommUtil.isNotNull(pathname)) {
				tmppath = pathname.replace('\\', '/');
			}

			if (!ftpClient.changeWorkingDirectory(protocolConfig.getFilePath() + tmppath)) {
				throw new LttsServiceException("FtpFileClientImpl.getRemoList01", protocolConfig.getFilePath() + tmppath);
			}

			ftpFiles = ftpClient.listFiles(protocolConfig.getFilePath() + tmppath, new FTPFileFilter() {

				@Override
				public boolean accept(FTPFile file) {
					// 获取OK文件
					if (file.getName().endsWith(regs)) {
						return true;
					}
					// 不是OK文件
					return false;
				}
			});
		}
		catch (IOException e) {
			throw new LttsServiceException("FtpFileClientImpl.getRemoList02","Remote directory[" + pathname + "] traversal failed", e);
		}
		finally {
			logoff(ftpClient);
		}

		if (ftpFiles != null) {
			List<String> fileList = new ArrayList<String>();
			for (FTPFile ftpFile : ftpFiles) {
				fileList.add(ftpFile.getName());
			}
			return fileList;
		}
		return null;
	}
}
