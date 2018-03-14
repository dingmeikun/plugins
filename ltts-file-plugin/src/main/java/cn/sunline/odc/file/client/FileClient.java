package cn.sunline.odc.file.client;

import java.util.List;

public interface FileClient {

	/**
	 * @Author dingmk@sunline.cn
	 *         <p>
	 *         <li>2018年3月1日-下午21:35:00</li>
	 *         <li>功能说明：测试连接是否可达</li>
	 *         </p>
	 * @return
	 */
	public boolean toReachable();
	
	/**
	 * @Author dingmk@sunline.cn
	 *         <p>
	 *         <li>2018年3月6日-上午11:01:00</li>
	 *         <li>功能说明：获取远程指定目录文件集合</li>
	 *         </p>
	 * @param remoteDir 远程目录
	 * @return
	 */
	public List<String> getRemoteFileList(String remoteDir);
	
	/**
	 * @Author liuzf@sunline.cn
	 *         <p>
	 *         <li>2017年10月17日-下午5:35:53</li>
	 *         <li>功能说明：获取远程根目录</li>
	 *         </p>
	 * @return
	 */
	public String getRemoteHome();

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年2月9日-下午3:56:35</li>
	 *         <li>功能说明：文件下载</li>
	 *         </p>
	 * @param localFileName 本地文件全路径
	 * @param remoteFileName 远程文件名
	 * @return
	 */
	public String download(String localFileName, String remoteFileName);

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年2月9日-下午3:56:51</li>
	 *         <li>功能说明：文件上传，无OK文件</li>
	 *         </p>
	 * @param localFileName
	 *            相对路径
	 * @param remoteFileName
	 *            相对路径
	 * @return
	 */
	public void upload(String localFileName, String remoteFileName);

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年2月9日-下午3:56:51</li>
	 *         <li>功能说明：文件上传</li>
	 *         </p>
	 * @param localFileName
	 *            相对路径
	 * @param remoteFileName
	 *            相对路径
	 * @return
	 */
	public void upload(String localFileName, String remoteFileName, boolean uploadOk);
}
