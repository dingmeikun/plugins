package cn.sunline.ltts.data.process;

import cn.sunline.ltts.busi.ltts.data.plugin.type.ComDataGen.DataGenInput;


/**
 * 数据生成处理器
 * @author caiqq
 *
 */
public interface DataGeneratorProcess {
	
	/**
	 * 单笔生成逻辑
	 * @param index 序号
	 * @param DataGenInput 数据生成输入条件
	 */
	public void generate(int index, DataGenInput input);
	
	public final static String JIAOYIRQ = "20151202";
	
}
