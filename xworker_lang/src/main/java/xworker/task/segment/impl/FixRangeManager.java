package xworker.task.segment.impl;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.task.segment.Range;
import xworker.task.segment.RangeManager;

/**
 * 固定大小的段管理器，比如总共1000个任务等。
 * 
 * @author Administrator
 *
 */
public class FixRangeManager implements RangeManager{
	long end;
	long start;
	int stepSize;
	
	List<Range> ranges = new ArrayList<Range>();
	
	public FixRangeManager(long start, long end, int stepSize){
		this.start = start;
		this.end = end;
		this.stepSize = stepSize;
		
		ranges.add(new Range(start, end));
	}

	@Override
	public Range getNextRange() {
		if(ranges.size() > 0){
			Range range = ranges.get(0);
			Range r = range.getRange(stepSize);
			if(range.isDropped()){
				ranges.remove(0);
			}
			
			return r;
		}else{
			return null;
		}
	}

	@Override
	public void rangeFinished(Range range) {
	}

	@Override
	public void rangeFailed(Range range) {
		//返回到未处理的段中
		for(int i=0; i<ranges.size(); i++){
			Range r = ranges.get(i);
			r.combine(range);
			if(range.isDropped()){
				return;
			}else if(range.isChanged()){
				ranges.add(range);
				return;
			}
		}
		
		ranges.add(range);
	}
	
	public static Object run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		long start = (Long) self.doAction("getStart", actionContext);
		long end = (Long) self.doAction("getEnd", actionContext);
		long size = (Long) self.doAction("getSize", actionContext);
		
		return new FixRangeManager(start, end, (int) size);
	}
}
