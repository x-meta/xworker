package xworker.dataObject;

import org.xmeta.Thing;

public interface DataStoreListener {
    public void onReconfig(DataStore dataStore, Thing dataObject);

    public void onLoaded(DataStore dataStore);
}
