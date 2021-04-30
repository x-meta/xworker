package xworker.dataObject;

import org.xmeta.Thing;

public interface DataStoreListener {
    /**
     * 数据仓库重新设置了数据对象的事件。
     *
     * @param dataStore
     * @param dataObject
     */
    public void onReconfig(DataStore dataStore, Thing dataObject);

    /**
     * 数据仓库加载了数据对象列表的事件。
     *
     * @param dataStore
     */
    public void onLoaded(DataStore dataStore);

    /**
     * 数据仓库
     *
     * @param dataStore
     */
    public void onChanged(DataStore dataStore);
}
