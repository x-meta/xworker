package xworker.task.segment;

import org.xmeta.ActionException;

public class Range {	
	/** 等待处理 */
	public static final byte WAITTING = 0;
	/** 正在被处理 */
	public static final byte WORKING = 1;
	/** 已经结束 */
	public static final byte FINISHED = 2;
	/** 已经失败 */
	public static final byte FAILED = 3;
	/** 执行超时 */
	public static final byte TIMEOUT = 4;
	/** 执行时出现异常 */
	public static final byte EXCEPTION = 5;
	/** 失败之后处理又失败的, 在FailedRangeManager中最后段会分为1，如果尝试几次还出错，那么会记录为此状态 */
	public static final byte FAILFAILE = 6;
	
	/** 没有混合 */
	public static final byte COMBINE_NONE = 0;
	/** 有变动 */
	public static final byte COMBINE_CHANGED = 1;
	/** 混合后可以丢弃了 */
	public static final byte COMBINE_DROP = 2;
	
	/** 标识 */
	public String id;
	/** 起始*/
	public long start;
	/** 结束 */
	public long end;	
	/** 状态 */
	public byte status;
	
	private byte combineStatus;
	
	public int runCount;
	
	public Range(long start, long end){
		this.start = start;
		this.end = end;
		if(this.end < 0){
			this.end = Long.MAX_VALUE;
		}
	}
	
	/**
	 * 从当前Range中分出一个新的 Range。
	 * 
	 * @param size
	 * @return
	 */
	public Range getRange(int size){
		if(size == 0){
			throw new ActionException("Range size must > 0");
		}
		combineStatus = Range.COMBINE_NONE;
		
		if(end + 1 - start <= size){
			Range r = new Range(start, end);
			combineStatus = Range.COMBINE_DROP;
			
			return r;
		}else{
			Range r = new Range(start, start + size - 1);
			start = r.end + 1;
			combineStatus = Range.COMBINE_CHANGED;
			
			return r;
		}
	}
	
	public boolean isDropped(){
		return combineStatus == Range.COMBINE_DROP;
	}
	
	public boolean isChanged(){
		return combineStatus == Range.COMBINE_CHANGED;
	}
	
	/**
	 * 把目标段融合到本段中。
	 * 
	 * @param range
	 */
	public void combine(Range range){
		combineStatus = 0;
		range.combineStatus = 0;
		
		//在左边，不可混合
		if(range.end < start){
			return;
		}
		
		//在右边，不可混合
		if(end < range.start){
			return;
		}
		
		if(end == range.start){
			//正好在右边
			end = range.end;
			
			combineStatus = Range.COMBINE_CHANGED;
			range.combineStatus = Range.COMBINE_DROP;
		}else if(start <= range.start){
			//在右边嵌入
			if(end < range.end){
				end = range.end;
			}
			
			combineStatus = Range.COMBINE_CHANGED;
			range.combineStatus = Range.COMBINE_DROP;
		}else if(start <= range.end){
			//左边嵌入
			if(start < range.start){
				start = range.start;
			}
			
			combineStatus = Range.COMBINE_CHANGED;
			range.combineStatus = Range.COMBINE_DROP;
		}		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public int getRunCount() {
		return runCount;
	}

	public void setRunCount(int runCount) {
		this.runCount = runCount;
	}

	@Override
	public String toString() {
		return "Range [id=" + id + ", start=" + start + ", end=" + end
				+ ", status=" + status + ", combineStatus=" + combineStatus
				+ "]";
	}
}
