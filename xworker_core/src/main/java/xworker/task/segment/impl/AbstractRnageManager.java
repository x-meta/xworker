package xworker.task.segment.impl;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.task.segment.Range;
import xworker.task.segment.RangeManager;

public abstract class AbstractRnageManager implements RangeManager{
	private List<Range> ranges = new ArrayList<Range>();
	Range endRange = null;
	Thing thing;
	ActionContext actionContext;
	int faileCount = 1;
	
	public AbstractRnageManager(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
		this.faileCount = (Integer) thing.doAction("getFaileCount", actionContext);
	}
	
	/**
	 * 初始化段，把最后一个无状态的段作为endRange。
	 */
	protected abstract void init() throws Exception;
	
	/**
	 * 返回新的结束的段值。
	 * 
	 * @return
	 */
	protected abstract long getNewEnd();
	
	protected abstract void updateRange(Range range);
	
	protected abstract void removeRange(Range range);
	
	protected abstract void createRange(Range range);
	
	protected abstract int getStepSize();
	
	public int getRangeSize(){
		return ranges.size();
	}
	
	public Range getRange(int index){
		synchronized(ranges){
			return ranges.get(index);
		}
	}
	
	public Range removeRange(int index){
		synchronized(ranges){
			return ranges.remove(index);
		}
	}
	
	public void addRange(int index, Range newRange){
		synchronized(ranges){
			ranges.add(index, newRange);
		}
	}
	
	public void addRange(Range newRange){
		synchronized(ranges){
			ranges.add(newRange);
		}
	}
		
	@Override
	public Range getNextRange() {
		synchronized(ranges){
			if(ranges.size() == 0){
				//如果没有分段了，试图获取新的段
				long newEnd = getNewEnd();
				if(endRange.end < newEnd){
					endRange.end = newEnd;
					updateRange(endRange);
				}else{
					return null;
				}			
			}
			
			if(ranges.size() > 0){			
				Range range = ranges.get(0);
				if(range == endRange && endRange.start > endRange.end){
					//已到达最后一个段，且这个段已经没有可以分配的记录了
					long newEnd = getNewEnd();
					if(endRange.start <= newEnd){
						endRange.end = newEnd;
						updateRange(endRange);
					}else{
						return null;
					}
				}
				
				Range r = range.getRange(getStepSize());
				if(range.isDropped()){
					if(range == endRange){
						//endRange不会被删除
						endRange.start = endRange.end + 1;
						updateRange(endRange);
					}else{
						removeRange(ranges.remove(0));
					}
				}else if(range.isChanged()){
					updateRange(range);
				}
				
				if(r != null){
					createRange(r);
				}
				return r;
			}
		
		}
		return null;	
	}

	@Override
	public void rangeFinished(Range range) {
		//成功的记录删除，只保存未完成的段
		removeRange(range);
	}

	@Override
	public void rangeFailed(Range range) {
		String faileStrategy = thing.getStringBlankAsNull("faileStrategy");
		if("split".equals(faileStrategy)){
			if(range.start == range.end){
				//不能再分割了，记录为失败
				range.status = Range.FAILED;
				updateRange(range);
				return;
			}else{
				//分割为两个段
				long count = range.start + (range.end - range.start) / 2;
				
				Range r1 = new Range(range.start,  count);
				Range r2 = new Range(count + 1, range.end);
				createRange(r1);
				createRange(r2);
				removeRange(range);
				addRange(0, r1);
				addRange(0, r2);
				
				return;
			}
		}else if("faile".equals(faileStrategy)){
			//记录为失败			
			range.setRunCount(range.getRunCount() + 1);
			if(range.getRunCount() >= this.faileCount){
				range.status = Range.FAILED;
				updateRange(range);
			}else{
				addRange(0, range);
			}			
			return;
		}else{		
			//返回到未处理的段中		
			addRange(0, range);
		}
	}

}
