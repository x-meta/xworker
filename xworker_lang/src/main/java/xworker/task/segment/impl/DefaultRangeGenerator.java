package xworker.task.segment.impl;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.task.segment.Range;
import xworker.task.segment.RangeGenerator;
import xworker.task.segment.SegmentTask;

public class DefaultRangeGenerator implements RangeGenerator {
    long start = 0;
    long end = Long.MAX_VALUE;
    long step = 1;
    long current = start;
    SegmentTask task;

    public DefaultRangeGenerator(){
    }

    @Override
    public Range getNextRange() {
        long theEnd = current + step;
        if(theEnd > this.end){
            theEnd = this.end;
        }
        Range range = new Range(task, current, theEnd);

        current = theEnd;
        return range;
    }

    @Override
    public Object toObject(long value) {
        return null;
    }

    @Override
    public void init(SegmentTask task) {
        this.task = task;

        Long offset = task.getRangeStore().getRangeOffset();
        if(offset != null) {
            //offset不为null，表示该分段任务之前应该已经执行过
            current = offset;
        }else {
            current = start;
        }
    }

    @Override
    public boolean hasNext() {
        return current < end;
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

    public long getStep() {
        return step;
    }

    public void setStep(long step) {
        this.step = step;
    }

    //动作的实现：xworker.lang.task.impls.DefaultRangeGenerator/@actions/@create
    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Long start = self.doAction("getStart", actionContext);
        Long end = self.doAction("getEnd", actionContext);
        Long step = self.doAction("getStep", actionContext);

        DefaultRangeGenerator generator = new DefaultRangeGenerator();
        if(start != null){
            generator.setStart(start);
        }
        if(end != null && end != -1){
            generator.setEnd(end);
        }
        if(step != null){
            generator.setStep(step);
        }

        return generator;
    }
}
