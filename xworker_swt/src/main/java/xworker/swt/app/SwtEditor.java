package xworker.swt.app;

public interface SwtEditor {
    /**
     * 设置内容，参数是Map&lt;String, Object&gt; params;
     */
    void setContent();

    /**
     * 返回是否是和已有的内容是相同的，参数是Map&lt;String, Object&gt; params;
     */
    boolean isSameContent();

    void doSave();

    boolean isDirty();

    String getSimpleTitle();

    String getTitle();

    void doDispose();

    void onActive();

    void onUnActive();

    String getIcon();

    /**
     * 返回概要栏的控件。
     */
    Object getOutline();

    void  onOutlineCreated();
}
