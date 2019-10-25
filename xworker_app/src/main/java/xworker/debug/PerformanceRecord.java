/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.debug;

public class PerformanceRecord {
	/** 动作路径 */
	String actionPath;
	/** 纳秒时间 */
	long nanoTime = 0;
	/** 执行次数 */
	int runCount;
	/** 成功次数 */
	int successCount;
	/** 异常次数 */
	int exceptionCount;
	
	public PerformanceRecord(String actionPath){
		this.actionPath = actionPath;
	}
	
	/**
	 * 记录一次执行。
	 * 
	 * @param time
	 */
	public void record(long time, boolean successed){
		nanoTime += time;
		runCount ++;
		if(successed){
			successCount ++;
		}else{
			exceptionCount ++;
		}
		
		//System.out.println(Thread.currentThread().getId() + "\t:" + actionPath + " " + getTimeString(time));
	}
	
	public int getSuccessCount(){
		return successCount;
	}
	
	public int getExceptionCount(){
		return exceptionCount;
	}
	
	/**
	 * 返回所有时间的字符串。
	 * 
	 * @return
	 */
	public String getTotalTime(){
		return getTimeString(nanoTime);
	}
	
	/**
	 * 返回总执行次数。
	 * 
	 * @return
	 */
	public int getRunCount(){
		return runCount;
	}
	
	public String getActionPath(){
		return actionPath;
	}
	
	/**
	 * 重置。
	 *
	 */
	public void reset(){
		nanoTime = 0;
		runCount = 0;
		successCount = 0;
		exceptionCount = 0;
	}
	
	/**
	 * 返回每次执行的平均时间。
	 * 
	 * @return
	 */
	public String getPerTime(){
		if(runCount == 0) return getTimeString(0);
		
		return getTimeString(nanoTime / runCount);
	}
	
	public String getTimeString(long naTime){
		double d = naTime / 1000000d;
		if(d > 1000){
			return "" + ((long) d) / 1000d + "秒";
		}else{
			return "" + ((long) (d * 1000)) / 1000d + "毫秒";
		}
	}
}