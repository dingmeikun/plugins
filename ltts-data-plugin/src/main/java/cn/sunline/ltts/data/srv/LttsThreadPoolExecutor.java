package cn.sunline.ltts.data.srv;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LttsThreadPoolExecutor extends ThreadPoolExecutor {
	private boolean hasFinish = false;

	public LttsThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				handler);
		

	}

	public LttsThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
			RejectedExecutionHandler handler) {
		
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory, handler);

	}

	public LttsThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory);

	}

	public LttsThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);

	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		synchronized (this) {
			System.out.println("自动调用了....afterEx 此时getActiveCount()值:"
					+ this.getActiveCount());
			if (this.getActiveCount() == 1)// 已执行完任务之后的最后一个线程
			{
				this.hasFinish = true;
				this.notify();
			} 
		} 
	}

	public boolean isEndTask() {
		synchronized (this) {
			while (this.hasFinish == false) {
				System.out.println("等待线程池所有任务结束: wait...");
				try {
					this.wait();
				} catch (InterruptedException ignore) {
				}
			}
		}
		return true;
	}

}
