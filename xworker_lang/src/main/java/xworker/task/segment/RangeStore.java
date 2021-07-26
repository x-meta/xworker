package xworker.task.segment;

import java.util.List;

public interface RangeStore {
    /**
     * 保存一个范围。
     *
     * @param task
     * @param range
     */
    public void saveRange(SegmentTask task, Range range);

    /**
     * 更新一个范围。
     *
     * @param task
     * @param range
     */
    public void updateRange(SegmentTask task, Range range);

    /**
     * 移除一个范围。
     *
     * @param task
     * @param range
     */
    public void removeRange(SegmentTask task, Range range);

    /**
     * 返回所有的范围列表。
     *
     * @param task
     * @return
     */
    public List<Range> geAllRanges(SegmentTask task);

    /**
     * 返回所有没有结束的范围列表。
     *
     * @param task
     * @return
     */
    public List<Range> getUnfinishedRanges(SegmentTask task);

    /**
     * 返回分段的当前偏移量。
     *
     * @return 如果不存在返回null
     */
    public Long getRangeOffset();

    /**
     * 更新保存分段偏移量。
     */
    public void updateRangeOffset(long offset);

    public void init(SegmentTask task);
}
