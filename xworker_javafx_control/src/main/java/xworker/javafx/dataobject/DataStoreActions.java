package xworker.javafx.dataobject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableView;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataStore;
import xworker.dataObject.PageInfo;
import xworker.javafx.dataobject.datastore.DataStoreChoiceBox;
import xworker.javafx.dataobject.datastore.DataStoreComboBox;
import xworker.javafx.dataobject.datastore.DataStorePagination;
import xworker.javafx.dataobject.datastore.DataStoreTableView;
import xworker.lang.executor.Executor;
import xworker.util.UtilData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 封装数据对象的查询。
 */
public class DataStoreActions {

    public static DataStore create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        DataStore store = new DataStore(self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), store);

        String bindTo = self.doAction("getBindTo", actionContext);
        if(bindTo != null){
            for(String bind : bindTo.split("[,]")){
                Object obj = actionContext.get(bind);
                if(obj instanceof ChoiceBox){
                    new DataStoreChoiceBox(store, (ChoiceBox<DataObject>) obj);
                }else if(obj instanceof ComboBox){
                    new DataStoreComboBox(store, (ComboBox<DataObject>) obj);
                }else if(obj instanceof Pagination){
                    new DataStorePagination(store, (Pagination) obj);
                }else if(obj instanceof TableView){
                    new DataStoreTableView(store, (TableView<DataObject>) obj);
                }
            }
        }
        actionContext.peek().put("parent", store);
        for(Thing child : self.getChilds()){
            child.doAction("ceate", actionContext);
        }
        return store;
    }
}
