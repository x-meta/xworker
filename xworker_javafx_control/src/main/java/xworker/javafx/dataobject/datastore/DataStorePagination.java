package xworker.javafx.dataobject.datastore;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.xmeta.Thing;
import xworker.dataObject.DataStore;
import xworker.dataObject.DataStoreListener;
import xworker.dataObject.PageInfo;

import java.util.Map;

public class DataStorePagination implements DataStoreListener {
    DataStore dataStore;
    Pagination pagination;
    VBox blankNode = new VBox();
    boolean loadable = false;

    public DataStorePagination(DataStore dataStore, Pagination pagination){
        this.dataStore = dataStore;
        this.pagination = pagination;
        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer param) {
                //加载新的页
                if(loadable) {
                    dataStore.load(param + 1);
                }
                //pagination.setCurrentPageIndex(param);
                //return new Label("page:"+ param);
                return blankNode;
            }
        });
        dataStore.addListener(this);
    }

    @Override
    public void onReconfig(DataStore dataStore, Thing dataObject) {
        Platform.runLater(() -> {
            //此时不需要加载数据仓库，因为不是分页自己触发的，是别人触发的
            loadable = false;
            pagination.setPageCount(1);
            pagination.setCurrentPageIndex(0);
        });

    }

    @Override
    public void onLoaded(DataStore dataStore) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    loadable = false;
                    PageInfo pageInfo = dataStore.getPageInfo();
                    pagination.setPageCount((int) pageInfo.getTotalPage());
                }finally{
                    loadable = true;
                    pagination.setDisable(false);
                }
            }
        });

    }

    @Override
    public void onChanged(DataStore dataStore) {

    }

    @Override
    public void beforeLoad(DataStore dataStore, Thing condition, Map<String, Object> params) {
        pagination.setDisable(true);
    }
}
