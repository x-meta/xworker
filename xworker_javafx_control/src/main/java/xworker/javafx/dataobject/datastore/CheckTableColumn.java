package xworker.javafx.dataobject.datastore;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import xworker.dataObject.DataObject;
import xworker.javafx.dataobject.beans.DataObjectBooleanProperty;

public class CheckTableColumn {
    TableView<DataObject> tableView;
    TableColumn<DataObject, Boolean> tableColumn = new TableColumn<>();
    CheckBox columnCheckBox = new CheckBox();

    public CheckTableColumn(TableView<DataObject> tableView){
        this.tableView = tableView;
        this.tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DataObject>() {
            @Override
            public void changed(ObservableValue<? extends DataObject> observable, DataObject oldValue, DataObject newValue) {
                for(DataObject dataObject : tableView.getItems()){
                    boolean have = false;
                    for(DataObject data : tableView.getSelectionModel().getSelectedItems()){
                        if(data == dataObject){
                            have = true;
                        }
                    }
                    if(!have){
                        dataObject.setChecked(false);
                    }else{
                        dataObject.setChecked(true);
                    }
                }
            }
        });

        tableColumn.setSortable(false);
        tableColumn.setPrefWidth(24);
        tableColumn.setGraphic(columnCheckBox);
        tableColumn.setCellFactory(new Callback<TableColumn<DataObject, Boolean>, TableCell<DataObject, Boolean>>() {
            @Override
            public TableCell<DataObject, Boolean> call(TableColumn<DataObject, Boolean> param) {
                CheckBoxTableCell<DataObject, Boolean> cell =  new CheckBoxTableCell<>();

                return cell;
            }
        });
        tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataObject, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<DataObject, Boolean> param) {
                DataObject dataObject = param.getValue();
                DataObjectBooleanProperty property = new DataObjectBooleanProperty(dataObject, DataObject.CHECKED_ATTRIBUTE_NAME);
                property.addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        int index = tableView.getItems().indexOf(dataObject);
                        if(newValue){
                            if(!tableView.getSelectionModel().isSelected(index)) {
                                tableView.getSelectionModel().select(dataObject);
                            }
                        }else{
                            if(tableView.getSelectionModel().isSelected(index)) {
                                tableView.getSelectionModel().clearSelection();
                            }
                        }
                    }
                });
                return property;
            }
        });

        tableView.getColumns().add(tableColumn);

        columnCheckBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tableView.getSelectionModel().clearSelection();
                if(columnCheckBox.isSelected()){
                    for(DataObject rowData : tableView.getItems()){
                        rowData.set(DataObject.CHECKED_ATTRIBUTE_NAME, true);
                        tableView.getSelectionModel().select(rowData);
                    }
                }else{
                    for(DataObject rowData : tableView.getItems()){
                        rowData.set(DataObject.CHECKED_ATTRIBUTE_NAME, false);
                    }
                }
            }
        });
    }

    public TableView<DataObject> getTableView() {
        return tableView;
    }

    public TableColumn<DataObject, Boolean> getTableColumn() {
        return tableColumn;
    }

    public CheckBox getColumnCheckBox() {
        return columnCheckBox;
    }

    public static boolean isChecked(DataObject dataObject){
        return dataObject.getBoolean(DataObject.CHECKED_ATTRIBUTE_NAME);
    }
}
