package cn.sunline.ltts.data.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

public class DataWriter {
	private static final BizLog log = BizLogUtil.getBizLog(DataWriter.class);
	private static final String ENCODING = "UTF8";
	
	/**
	 * 数据集文件名。
	 */
	private String path;
	private String dataFileName;
	
	/**
	 * 写入刷新控制参数，控制每隔 FLUSH_SIZE_IN_WRITE 笔刷新写缓冲区。
	 */
	private final int FLUSH_SIZE_IN_WRITE;
	/**
	 * 记录数（行数），在读取或写入过程中会动态变化。
	 */
	private int size;
	
	// WARN! 文件资源可以重复打开或关闭，不会传递给拆分子任务
	private File file;
	private BufferedWriter writer;
	private BufferedReader reader;
	
	
	public DataWriter(String path, String dataFileName) {
		if(path != null ) {
			File f = new File(path);
			if (!f.exists())
				f.mkdirs();
		}
		this.path = path;
		this.dataFileName = dataFileName;
		this.size = 0;
		FLUSH_SIZE_IN_WRITE = 500;
	}

	public void write(String content) {
		try {
			//TODO: 可能有性能问题，需要优化
			getWriter().write(content + "\n");
			size++;
			if (size % FLUSH_SIZE_IN_WRITE == 0) {
				getWriter().flush();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String read() {
		try {
			return getReader().readLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void open(boolean forWrite) {
		this.size = 0;
		if (this.file == null)
			this.file = new File(path, dataFileName);
		
		if (!this.file.exists())
			try {
				this.file.createNewFile();
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
		
		if (this.writer == null && forWrite) {	
			try {
				this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), ENCODING));
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
		
		if (this.reader == null && !forWrite) {	
			try {
				this.reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), ENCODING));
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * 关闭资源。
	 */
	public void close() {
		if (this.reader != null) {
			try {
				this.reader.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
			this.reader = null;
		}
		
		if (this.writer != null) {
			try {
				this.writer.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
			this.writer = null;
		}
		this.file = null;
	}
	
	/**
	 * 删除文件。
	 */
	public void remove() {
		if (this.file == null)
			this.file = new File(path, dataFileName);
		this.file.delete();
		this.file = null;
	}
	
	public String getDataFileName() {
		return dataFileName;
	}
	
	public String getDataFilePath() {
		return file.getAbsolutePath();
	}
	
	private BufferedWriter getWriter() {
		return this.writer;
	}

	private BufferedReader getReader() {
		return this.reader;
	}

	public int getSize() {
		return size;
	}
	
}
