package xworker.task.segment.impl;

import org.xmeta.ActionContext;
import xworker.task.segment.Range;
import xworker.task.segment.RangeStore;
import xworker.task.segment.SegmentTask;

import java.util.ArrayList;
import java.util.List;

public class ListRangeStore implements RangeStore {
    long seq = 0;
    Long offset = null;

    List<Range> ranges = new ArrayList<>();

    public ListRangeStore(){
    }

    @Override
    public void saveRange(SegmentTask task, Range range) {
        if(!ranges.contains(range)){
            ranges.add(range);

            range.setId(String.valueOf(seq));
            seq++;
        }
    }

    @Override
    public void updateRange(SegmentTask task, Range range) {
    }

    @Override
    public void removeRange(SegmentTask task, Range range) {
        ranges.remove(range);
    }

    @Override
    public List<Range> geAllRanges(SegmentTask task) {
        return ranges;
    }

    @Override
    public List<Range> getUnfinishedRanges(SegmentTask task) {
        List<Range> list = new ArrayList<>();
        for(Range range : ranges){
            if(range.getStatus() != Range.FINISHED){
                list.add(range);
            }
        }
        return list;
    }

    @Override
    public Long getRangeOffset() {
        return offset;
    }

    @Override
    public void updateRangeOffset(long offset) {
        this.offset = offset;
    }

    @Override
    public void init(SegmentTask task) {

    }

    //动作实现：xworker.lang.task.impls.ListRangeStore/@actions/@create
    public static Object create(ActionContext actionContext){
        return new ListRangeStore();
    }
}
