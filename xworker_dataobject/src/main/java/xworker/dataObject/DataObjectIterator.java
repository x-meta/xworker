package xworker.dataObject;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.dataObject.query.QueryConfig;

import java.util.Iterator;

/**
 * 数据对象迭代器。存在资源问题，使用应该使用close()方法关闭。
 */
public interface DataObjectIterator extends Iterator<DataObject>, AutoCloseable {
    public Thing getDataObject();

    public QueryConfig getQueryConfig();

    public ActionContext getActionContext();
}
