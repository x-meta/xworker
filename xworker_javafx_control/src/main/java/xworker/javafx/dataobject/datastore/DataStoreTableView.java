package xworker.javafx.dataobject.datastore;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.javafx.dataobject.DataStore;
import xworker.javafx.dataobject.DataStoreListener;

import java.util.HashMap;
import java.util.Map;


public class DataStoreTableView implements DataStoreListener {
    DataStore dataStore;
    TableView<DataObject> tableView;

    public DataStoreTableView(DataStore dataStore, TableView<DataObject> tableView){
        this.dataStore = dataStore;
        this.tableView = tableView;

        //tableView.itemsProperty().bind(dataStore.datasProperty());
        dataStore.addListener(this);
    }

    @Override
    public void onReconfig(DataStore dataStore, Thing dataObject) {
        //清空原有的列
        this.tableView.getColumns().clear();

        Map<String, TableColumn<DataObject, Object>> columnGroups = new HashMap<>();
        for(Thing attr : dataObject.getChilds("attribute")){
            if(attr.getBoolean("viewField") && attr.getBoolean("showInTable") && attr.getBoolean("gridEditor")){
                TableColumn<DataObject, Object> column = new TableColumn<>();
                //保存属性定义到TableColumn上
                column.setUserData(attr);

                //分组
                String columnGroup = attr.getStringBlankAsNull("columnGroup");
                if(columnGroup != null) {
                    TableColumn<DataObject, Object> parent = null;
                    for(String gname : columnGroup.split("[,]")) {
                        TableColumn<DataObject, Object> cg = columnGroups.get(gname);
                        if(cg == null) {
                            cg = new TableColumn<>();
                            cg.setText(gname);
                            columnGroups.put(gname, cg);
                            if (parent != null) {
                                parent.getColumns().add(cg);
                            }
                        }
                    }
                    parent.getColumns().add(column);
                }

                //Cell的工厂
                column.setCellFactory(DataStoreTableViewCellFactory.getInstance());

                //Cell值得工厂
                column.setCellValueFactory(DataStoreTableViewCellValueFactory.getInstance());

                //宽度
                column.setText(attr.getMetadata().getLabel());
                int width = attr.getInt("gridWidth");
                if(width <= 0){
                    width = 80;
                }
                if(width > 0){
                    column.setPrefWidth(width);
                }
                column.setResizable(attr.getBoolean("gridResizable"));

                //是否可排序
                if(attr.getBoolean("gridSortable")){
                    column.setSortable(true);
                }
                tableView.getColumns().add(column);
            }
        }

    }

    @Override
    public void onLoaded(DataStore dataStore) {
        tableView.setItems(dataStore.getDatas());
    }
}
