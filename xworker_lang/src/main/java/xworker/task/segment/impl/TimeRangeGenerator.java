package xworker.task.segment.impl;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.task.segment.Range;
import xworker.task.segment.RangeGenerator;
import xworker.task.segment.SegmentTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeRangeGenerator implements RangeGenerator {
    String timeUnit;
    Date start;
    Date end;
    Date current = null;
    long step = 1;
    boolean trim = true;
    boolean waitRealTime = false;
    SegmentTask task;

    public TimeRangeGenerator(){
    }

    @Override
    public Range getNextRange() {
        Date currentEnd = null;
        if("second".equals(timeUnit)){
            currentEnd = new Date(current.getTime() + step * 1000);
        }else if("minute".equals(timeUnit)){
            currentEnd = new Date(current.getTime() + step * 60 * 1000);
        }else if("hour".equals(timeUnit)){
            currentEnd = new Date(current.getTime() + step * 3600 * 1000);
        }else if("day".equals(timeUnit)){
            currentEnd = new Date(current.getTime() + step * 24 * 3600 * 1000);
        }else if("week".equals(timeUnit)){
            currentEnd = new Date(current.getTime() + step * 7 * 24 * 3600 * 1000);
        }else {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(current);

            if ("year".equals(timeUnit)) {
                gc.add(Calendar.YEAR, (int) step);
                currentEnd = gc.getTime();
            }else if("month".equals(timeUnit)){
                gc.add(Calendar.MONTH, (int) step);
                currentEnd = gc.getTime();
            }
        }

        if(currentEnd == null){
            return null;
        }

        if(waitRealTime && currentEnd.getTime() >= System.currentTimeMillis()){
            //是否等待系统时间
            return null;
        }

        Range range = new Range(task, current.getTime(), currentEnd.getTime());

        current = currentEnd;
        return range;
    }

    @Override
    public void init(SegmentTask task) {
        Long offset = task.getRangeStore().getRangeOffset();
        if(offset != null){
            current = new Date(offset);
        }else{
            current = start;
        }

        this.task = task;
    }

    @Override
    public boolean hasNext() {
        return end == null || current.getTime() < end.getTime();
    }

    @Override
    public Object toObject(long value) {
        return new Date(value);
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    private Date trim(Date date) throws ParseException {
        if(date == null){
            return null;
        }

        SimpleDateFormat sf = null;
        if("minute".equals(timeUnit)){
            sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }else if("hour".equals(timeUnit)){
            sf = new SimpleDateFormat("yyyy-MM-dd HH");
        }else if("day".equals(timeUnit)){
            sf = new SimpleDateFormat("yyyy-MM-dd");
        }else if("week".equals(timeUnit)){
            sf = new SimpleDateFormat("yyyy-MM-dd");
        }else if ("year".equals(timeUnit)) {
            sf = new SimpleDateFormat("yyyy");
        }else if("month".equals(timeUnit)){
            sf = new SimpleDateFormat("yyyy-MM");
        }

        if(sf != null){
            Date newDate =  sf.parse(sf.format(date));
            if("week".equals(timeUnit)){
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTime(newDate);
                int dateOfWeek = gc.get(Calendar.DAY_OF_WEEK);
                gc.add(Calendar.DATE, -dateOfWeek);
                newDate = gc.getTime();
            }
            return newDate;
        }else{
            return date;
        }
    }


    //动作实现：xworker.lang.task.impls.TimeRangeGenerator/@actions/@create
    public static Object create(ActionContext actionContext) throws ParseException {
        Thing self = actionContext.getObject("self");

        Date start = self.doAction("getStart", actionContext);
        Date end = self.doAction("getEnd", actionContext);
        Long step = self.doAction("getStep", actionContext);
        String timeUnit = self.doAction("getTimeUnit", actionContext);
        Boolean trim = self.doAction("isTrim", actionContext);
        Boolean waitRealTime = self.doAction("isWaitRealTime", actionContext);

        TimeRangeGenerator generator = new TimeRangeGenerator();
        if(start == null){
            start = new Date();
        }
        generator.start = start;
        generator.end = end;
        generator.step = step;
        if(generator.step <= 0){
            generator.step = 1;
        }
        generator.timeUnit = timeUnit;
        generator.trim = trim;
        generator.waitRealTime = waitRealTime;
        if(trim){
            generator.start = generator.trim(generator.start);
            if(generator.end != null){
                generator.end = generator.trim(generator.end);
            }
        }

        return generator;
    }
}
