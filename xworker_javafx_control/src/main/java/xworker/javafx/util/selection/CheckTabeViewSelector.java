package xworker.javafx.util.selection;

import javafx.scene.control.TableView;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.javafx.util.Selector;

import java.util.ArrayList;
import java.util.List;

public class CheckTabeViewSelector implements Selector<DataObject> {
    TableView<DataObject> tableView;

    public CheckTabeViewSelector(TableView<DataObject> tableView){
        this.tableView = tableView;
    }

    @Override
    public List<DataObject> getSelectItems() {
        List<DataObject> datas = new ArrayList<>();
        for(DataObject item : tableView.getItems()){
            if(item.isChecked()){
                datas.add(item);
            }
        }

        return datas;
    }

    @Override
    public DataObject getSelectItem() {
        List<DataObject> datas = getSelectItems();
        return datas.size() > 0 ? datas.get(0) : null;
    }

    public static CheckTabeViewSelector create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        TableView<DataObject> tableView = self.doAction("getTableView", actionContext);
        if(tableView != null){
            return new CheckTabeViewSelector(tableView);
        }else{
            return null;
        }
    }
}
