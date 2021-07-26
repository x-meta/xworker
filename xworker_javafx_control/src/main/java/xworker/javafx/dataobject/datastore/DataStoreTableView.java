package xworker.javafx.dataobject.datastore;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataStore;
import xworker.dataObject.DataStoreListener;
import xworker.javafx.thing.form.AttributeDataStore;
import xworker.util.UtilData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataStoreTableView implements DataStoreListener {
    public static final String DATASTORE = "dataStore";
    public static final String VALUES = "values";

    DataStore dataStore;
    TableView<DataObject> tableView;
    boolean check = false;

    public DataStoreTableView(DataStore dataStore, TableView<DataObject> tableView){
        this.dataStore = dataStore;
        this.tableView = tableView;
        this.tableView.getProperties().put(DATASTORE, dataStore);
        this.check = UtilData.isTrue(dataStore.getThing().doAction("isCheck", dataStore.getActionContext()));

        //tableView.itemsProperty().bind(dataStore.datasProperty());
        dataStore.addListener(this);
    }

    @Override
    public void onReconfig(final DataStore dataStore, Thing dataObject) {
        //清空原有的列
        this.tableView.getColumns().clear();

        if(check){
            //增加选择列
            new CheckTableColumn(tableView);
        }

        List<Thing> tableColumnThings = null;
        if(dataStore.getThing() != null){
            tableColumnThings = dataStore.getThing().getChilds("TableColumn");
        }

        Map<String, TableColumn<DataObject, Object>> columnGroups = new HashMap<>();
        for(Thing attr : dataObject.getChilds("attribute")){
            if(attr.getBoolean("viewField") && attr.getBoolean("showInTable") && attr.getBoolean("gridEditor")){
                TableColumn<DataObject, Object> column = null;
                //查看是否自定了tableClumns
                boolean selfDefined = false;
                if(tableColumnThings != null){
                    for(int i=0; i<tableColumnThings.size(); i++){
                        Thing tableColumnThing = tableColumnThings.get(i);

                        String attributeName = tableColumnThing.getStringBlankAsNull("attributeName");
                        if(attr.getMetadata().getName().equals(attributeName)){
                            //通过名字匹配，如果匹配则创建
                            column = tableColumnThing.doAction("create", dataStore.getActionContext());
                            tableColumnThings.remove(i);
                            selfDefined = true;
                            break;
                        }
                    }
                }
                if(column == null){
                    column = new TableColumn<>();
                    column.setCellFactory(DataStoreTableViewCellFactory.getInstance());
                }

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

                //Cell值得工厂
                if(column.getCellValueFactory() == null) {
                    column.setCellValueFactory(DataStoreTableViewCellValueFactory.getInstance());
                }

                if(!selfDefined) {
                    //是否可编辑
                    if (attr.getBoolean("readOnly") || !attr.getBoolean("editable")) {
                        column.setEditable(false);
                    }

                    //列是否隐藏
                    if (attr.getBoolean("gridHidden")) {
                        column.setVisible(false);
                    }

                    //宽度
                    column.setText(attr.getMetadata().getLabel());
                    int width = attr.getInt("gridWidth");
                    if (width <= 0) {
                        width = 80;
                    }
                    if (width > 0) {
                        column.setPrefWidth(width);
                    }
                    column.setResizable(attr.getBoolean("gridResizable"));

                    //是否可排序
                    if (attr.getBoolean("gridSortable")) {
                        column.setSortable(true);
                    }
                }

                String inputtype = attr.getStringBlankAsNull("inputtype");
                if("select".equals(inputtype) || "inputSelect".equals(inputtype)) {
                    //数据仓库
                    DataStore attributeDataStore = AttributeDataStore.getDataStore(attr, null);
                    if (attributeDataStore != null) {
                        //加载数据
                        attributeDataStore.load(new HashMap<>());
                        column.getProperties().put(DataStoreTableView.DATASTORE, attributeDataStore);
                    } else {
                        List<Thing> values = attr.getAllChilds("value");
                        if (values.size() > 0) {
                            column.getProperties().put(DataStoreTableView.VALUES, values);
                        }
                    }
                }

                tableView.getColumns().add(column);
            }
        }

        if(tableColumnThings != null && tableColumnThings.size() > 0){
            for(Thing tableColumnThing : tableColumnThings){
                TableColumn<DataObject, Object> tableColumn = tableColumnThing.doAction("create", dataStore.getActionContext());
                if(tableColumn != null){
                    tableView.getColumns().add(tableColumn);
                }
            }
        }

    }

    @Override
    public void onLoaded(DataStore dataStore) {
        tableView.setItems(FXCollections.observableList(dataStore.getDatas()));
    }

    @Override
    public void onChanged(DataStore dataStore) {
        tableView.setItems(FXCollections.observableList(dataStore.getDatas()));
    }

    @Override
    public void beforeLoad(DataStore dataStore, Thing condition, Map<String, Object> params) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tableView.getItems().clear();
            }
        });

    }
}
