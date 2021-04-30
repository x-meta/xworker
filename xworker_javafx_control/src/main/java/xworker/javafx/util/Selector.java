package xworker.javafx.util;

import java.util.List;

/**
 * 从控件上获取选中的数据的方法。
 */
public interface Selector<T> {
    public List<T> getSelectItems();

    public T getSelectItem();
}
