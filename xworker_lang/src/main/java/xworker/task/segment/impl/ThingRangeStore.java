package xworker.task.segment.impl;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.task.DelayTask;
import xworker.task.segment.Range;
import xworker.task.segment.RangeStore;
import xworker.task.segment.SegmentTask;

import java.util.ArrayList;
import java.util.List;

public class ThingRangeStore implements RangeStore {
    Thing thing;
    Long offset = null;
    List<ThingRange> thingRanges = new ArrayList<>();
    SegmentTask task;
    DelayTask delayTask = new DelayTask(500) {
        @Override
        public void run() {
            save();
        }
    };

    public ThingRangeStore(Thing thing){
        if(thing.valueExists("offset")){
            offset = thing.getLong("offset");
        }
    }

    /**
     * 避免可能的保存过于频繁，使用延迟任务
     */
    private void doSave(){
        delayTask.doTask();
    }

    public void save(){
        for(ThingRange thingRange : thingRanges){
            //把Range的状态同步到模型里
            thingRange.updateThing();
        }

        thing.save();
    }

    @Override
    public void saveRange(SegmentTask task, Range range) {
        ThingRange thingRange = new ThingRange(range);
        thing.addChild(thingRange.thing);
        thingRanges.add(thingRange);

        doSave();
    }

    @Override
    public void updateRange(SegmentTask task, Range range) {
        doSave();
    }

    @Override
    public void removeRange(SegmentTask task, Range range) {
        synchronized (thingRanges) {
            for (int i = 0; i < thingRanges.size(); i++) {
                if(thingRanges.get(i).range == range){
                    thingRanges.remove(i);
                    i--;
                }
            }

            doSave();
        }
    }

    @Override
    public List<Range> geAllRanges(SegmentTask task) {
        List<Range> ranges = new ArrayList<>();
        for(ThingRange thingRange : thingRanges){
            ranges.add(thingRange.range);
        }

        return ranges;
    }

    @Override
    public List<Range> getUnfinishedRanges(SegmentTask task) {
        List<Range> ranges = new ArrayList<>();
        for(ThingRange thingRange : thingRanges){
            if(thingRange.range.getStatus() != Range.FINISHED) {
                ranges.add(thingRange.range);
            }
        }

        return ranges;
    }

    @Override
    public Long getRangeOffset() {
        return offset;
    }

    @Override
    public void updateRangeOffset(long offset) {
        this.offset = offset;
        thing.put("offset", offset);
        doSave();
    }

    @Override
    public void init(SegmentTask task) {
        this.task = task;

        for(Thing rangeThing : thing.getChilds("Range")){
            thingRanges.add(new ThingRange(task, rangeThing));
        }
    }

    static class ThingRange{
        Thing thing;
        Range range;

        public ThingRange(SegmentTask task, Thing thing){
            this.thing = thing;
            String id = thing.getString("id");
            long start = thing.getLong("start");
            long end = thing.getLong("end");
            range = new Range(task, start, end);
            range.setStatus(thing.getByte("status"));
            range.setRunCount(thing.getInt("runCount"));
        }

        public ThingRange(Range range){
            this.range = range;

            thing = new Thing("xworker.lang.task.impls.ThingRangeStore/@Range");
            updateThing();
        }

        public void updateThing(){
            thing.put("id", range.getId());
            thing.put("start", range.getStart());
            thing.put("end", range.getEnd());
            thing.put("status", range.getStatus());
            thing.put("runCount", range.getRunCount());
        }
    }

    //动作的实现：xworker.lang.task.impls.ThingRangeStore/@actions/@create
    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Thing storeThing = self.doAction("getStoreThing", actionContext);
        if(storeThing == null){
            storeThing = self;
        }

        return new ThingRangeStore(storeThing);
    }
}
