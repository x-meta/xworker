package xworker.javafx.dataobject;

import org.xmeta.Thing;
import xworker.dataObject.DataObject;

public interface DataStoreListener {
    public void onReconfig(DataStore dataStore, Thing dataObject);

    public void onLoaded(DataStore dataStore);
}
