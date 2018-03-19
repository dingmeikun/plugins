package cn.sunline.ltts.data.srv;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import cn.sunline.ltts.base.srv.ServerContants;
import cn.sunline.ltts.base.srv.socket.common.SimpleThreadFactory;

/**
 * 数据生成线程管理器
 * @author caiqq
 *
 */
public class DataGeneratorThreadManager {
	private static final DataGeneratorThreadManager instance = new DataGeneratorThreadManager();
	
	private LttsThreadPoolExecutor brchnoGenerateService = null;
	
	private final Semaphore semp;
	
	private int max = 1;
	
	private DataGeneratorThreadManager() {
		max = DataGeneratorProcessManager.get().getBrchnoThreadCount();
		if (max == 0 )
			max = 1;
		brchnoGenerateService = new LttsThreadPoolExecutor(
				max, 
				max,
                0L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>(), 
                new SimpleThreadFactory(ServerContants.THREAD_PREFIX + "dataGen"));
		
		//控制同时只能有n个作业被提交
		semp = new Semaphore(max);
	}
	
	public static DataGeneratorThreadManager get() {
		return instance;
	}
	
	
	public void submit(final Runnable runnable) {
		// 获取提交作业的许可
		try {
			semp.acquire();
		} catch (Exception e) {
			throw new RuntimeException("获取执行许可失败...", e);
		}
		while (true) {
			try {
				brchnoGenerateService.submit( new Runnable() {
					@Override
					public void run() {
						try {
							runnable.run();
						} finally {
							// 处理完成后，释放许可
							semp.release();
						}
					}
				} );
				
				break;
			} catch(RejectedExecutionException ignore1) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException ignore2) {
				}
			}
		}
	}
	
	public boolean isFinish() {
		return brchnoGenerateService.isEndTask();
	}
	
}
