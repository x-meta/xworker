package xworker.task.segment;

public interface RangeGenerator {
    /**
     * 返回下一个分段。
     *
     * @return
     */
    public Range getNextRange();

    /**
     * 把分段的long值转化为对象。如果按时间的分段可以把分段的Long值转化为日期。
     * @param value
     * @return
     */
    public Object toObject(long value);

    public void init(SegmentTask task);

    public boolean hasNext();
}
