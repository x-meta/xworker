package xworker.javafx.dataobject;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableView;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataStore;
import xworker.javafx.dataobject.datastore.DataStoreChoiceBox;
import xworker.javafx.dataobject.datastore.DataStoreComboBox;
import xworker.javafx.dataobject.datastore.DataStorePagination;
import xworker.javafx.dataobject.datastore.DataStoreTableView;
import xworker.javafx.thing.form.ThingForm;

import java.util.Map;

/**
 * 封装数据对象的查询。
 */
public class DataStoreActions {

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    public static void load(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        DataStore dataStore = self.doAction("getDataStore", actionContext);
        Object params = self.doAction("getParams", actionContext);
        Integer page = self.doAction("getPage", actionContext);

        if(dataStore == null){
            throw new ActionException("DataStore is null, action=" + self.getMetadata().getPath());
        }
        if(params instanceof Map){
            dataStore.load((Map<String, Object>) params, page);
        }else if(params instanceof ThingForm){
            ThingForm form = (ThingForm) params;
            dataStore.load(form.getValues(), page);
        }else if(params instanceof DataObjectForm){
            DataObjectForm form = (DataObjectForm) params;
            dataStore.load(form.getValues(), page);
        }else{
            dataStore.load(page);
        }
    }
}
