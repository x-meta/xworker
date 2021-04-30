package xworker.javafx.dataobject;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Window;
import org.controlsfx.control.CheckListView;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataStore;
import xworker.javafx.dataobject.dialogs.BatchInsertDataObjectDialog;
import xworker.javafx.dataobject.dialogs.DataObjectDialog;
import xworker.javafx.util.Selector;
import xworker.javafx.util.SelectorFactory;
import xworker.util.UtilData;

import java.util.ArrayList;
import java.util.List;

public class DataObjectActions {
    public static void createDataObjectDialog(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        String title = self.doAction("getTitle", actionContext);
        DataStore dataStore = self.doAction("getDataStore", actionContext);
        Thing dataObject;
        if(dataStore != null){
            dataObject = dataStore.getDataObject();
        }else {
            dataObject = self.doAction("getDataObject", actionContext);
        }
        final String failMessage = self.doAction("getFailMessage", actionContext);
        final String exceptionMessage = self.doAction("getExceptionMessage", actionContext);
        Double width = self.doAction("getWidth", actionContext);
        Double height = self.doAction("getHeight", actionContext);
        Boolean multi = self.doAction("isMulti", actionContext);

        if(dataObject == null){
            throw new ActionException("Can not create dialog, dataobject is null, action=" + self.getMetadata().getPath());
        }

        DataObjectDialog<DataObject> dialog = new DataObjectDialog<>(dataObject, DataObjectFormType.CREATE);
        if(title == null){
            title = UtilString.getString("lang:d=新建&en=New ", actionContext) + dataObject.getMetadata().getLabel();
        }
        dialog.setTitle(title);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        final String alertTitle = title;
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            DataObject data = dialog.getDataObjectForm().getDataObject();
            try {
                data = data.create(actionContext);
                if(data == null){
                    String message = failMessage;
                    if(message == null || message.isEmpty()){
                        message = UtilString.getString("lang:d=创建数据失败！&en=Create data failed!", actionContext);
                    }
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(message);
                    alert.setTitle(alertTitle);
                    alert.showAndWait();
                    event.consume();
                }else{
                    if(dataStore != null){
                        dataStore.add(data);
                    }
                    dialog.setResult(data);

                    if(multi != null && multi){
                        event.consume();
                    }
                }
            }catch(Exception e){
                String message = exceptionMessage;
                if(message == null || message.isEmpty()){
                    message = e.getMessage();
                }
                Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
                alert.showAndWait();
                event.consume();
            }
        });

        if(width != null && width > 0){
            dialog.getDialogPane().setPrefWidth(width);
        }
        if(height != null && height > 0){
            dialog.getDialogPane().setPrefHeight(height);
        }
        dialog.setResultConverter(param -> {
            if(param == ButtonType.OK){
                return dialog.getResult();
            }else {
                return null;
            }
        });

        dialog.showAndWait().ifPresent(result -> {
            if(result != null){
                self.doAction("ok", actionContext, "data", result);
            }else{
                self.doAction("cancel", actionContext);
            }
        });
    }

    public static void editDataObjectDialog(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        String title = self.doAction("getTitle", actionContext);
        Object dataSource = self.doAction("getDataSource", actionContext);
        DataObject dataObject = null;
        if(dataSource instanceof DataObject){
            dataObject = (DataObject) dataSource;
        }else if(dataSource instanceof Selector){
            Selector<?> selector = (Selector<?>) dataSource;
            dataObject = (DataObject) selector.getSelectItem();
        }else{
            Selector<?> selector = SelectorFactory.getSelector(dataSource);
            if(selector != null){
                dataObject = (DataObject) selector.getSelectItem();
            }else{
                Thing dataSelectorThing = self.doAction("getDataSelector", actionContext);
                if(dataSelectorThing != null){
                    selector = dataSelectorThing.doAction("create");
                }
                if(selector != null){
                    dataObject = (DataObject) selector.getSelectItem();
                }
            }
        }
        //final String failMessage = self.doAction("getFailMessage", actionContext);
        final String exceptionMessage = self.doAction("getExceptionMessage", actionContext);
        Double width = self.doAction("getWidth", actionContext);
        Double height = self.doAction("getHeight", actionContext);
        String noDataMessage = self.doAction("getNoDataMessage", actionContext);

        if(dataObject == null){
            if(noDataMessage == null){
               noDataMessage = "Please select a record!";
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(noDataMessage);
            //alert.getButtonTypes().add(ButtonType.OK);
            alert.setTitle(title);
            alert.show();
            return;
        }

        DataObjectDialog<Integer> dialog = new DataObjectDialog<>(dataObject, DataObjectFormType.CREATE);
        if(title != null){
            dialog.setTitle(title);
        }else{
            Thing descriptor = dataObject.getMetadata().getDescriptor();
            dialog.setTitle(UtilString.getString("lang:d=编辑&en=Edit ", actionContext) + descriptor.getMetadata().getLabel());
        }
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            DataObject data = dialog.getDataObjectForm().getDataObject();
            try {
                int count = data.update(actionContext);
                dialog.setResult(count);
            }catch(Exception e){
                String message = exceptionMessage;
                if(message == null || message.isEmpty()){
                    message = e.getMessage();
                }
                Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
                alert.showAndWait();
                event.consume();
            }
        });

        if(width != null && width > 0){
            dialog.getDialogPane().setPrefWidth(width);
        }
        if(height != null && height > 0){
            dialog.getDialogPane().setPrefHeight(height);
        }
        dialog.setResultConverter(param -> {
            if(param == ButtonType.OK){
                return dialog.getResult();
            }else {
                return null;
            }
        });

        dialog.showAndWait().ifPresent(result -> {
            if(result != null){
                self.doAction("ok", actionContext, "count", result);
            }else{
                self.doAction("cancel", actionContext);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static void deleteDataObjectDialog(final ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        final DataStore dataStore = self.doAction("getDataStore", actionContext);
        Object dataSource = self.doAction("getDataSource", actionContext);
        String noDataMessage = self.doAction("getNoDataMessage", actionContext);
        String confirmMessage = self.doAction("getConfirmMessage", actionContext);
        String title = self.doAction("getTitle", actionContext);

        List<DataObject> datas = null;
        if(dataSource instanceof DataObject){
            datas = new ArrayList<>();
            datas.add((DataObject) dataSource);
        }else if(dataSource instanceof Selector){
            Selector<?> selector = (Selector<?>) dataSource;
            datas = (List<DataObject>) selector.getSelectItems();
        }else{
            Selector<?> selector = SelectorFactory.getSelector(dataSource);
            if(selector != null){
                datas = (List<DataObject>) selector.getSelectItems();
            }else{
                Thing dataSelectorThing = self.doAction("getDataSelector", actionContext);
                if(dataSelectorThing != null){
                    selector = dataSelectorThing.doAction("create");
                }
                if(selector != null){
                    datas = (List<DataObject>) selector.getSelectItems();
                }
            }
        }

        if(datas == null || datas.size() == 0){
            if(noDataMessage != null){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(noDataMessage);
                //alert.getButtonTypes().add(ButtonType.OK);
                alert.setTitle(title);
                alert.show();
                return;
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText(confirmMessage);
            //alert.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            alert.setTitle(title);
            final List<DataObject> dataObjects = datas;
            alert.showAndWait().ifPresent(param -> {
                if(param == ButtonType.OK) {
                    for(DataObject data : dataObjects){
                        data.delete(actionContext);
                    }

                    if(dataStore != null) {
                        dataStore.removeAll(dataObjects);
                    }

                    self.doAction("ok", actionContext, "dataObjects", dataObjects);
                }else{
                    self.doAction("cancel", actionContext, "dataObjects", dataObjects);
                }
            });
        }
    }

    public static void selectDataObjectDialog(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        DataObjectEditor  dataObjectEditor = new DataObjectEditor(self, actionContext);
        Dialog<DataObject> dialog = new Dialog<>();
        dialog.setTitle(self.getMetadata().getLabel());
        String title = self.doAction("getTitle", actionContext);
        if(title == null){
            title = self.getMetadata().getLabel();
        }
        dialog.setResizable(true);
        dialog.setTitle(title);
        dialog.getDialogPane().setContent(dataObjectEditor.getRootNode());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            List<DataObject> dataObjects = dataObjectEditor.getTableView().getSelectionModel().getSelectedItems();
            if(UtilData.isTrue(self.doAction("onSelected", actionContext, "dataObjects", dataObjects
                    , "dialog", dialog, "editor", dataObjectEditor))){
                event.consume();
            }
        });
        Double width = self.doAction("getWidth", actionContext);
        Double height = self.doAction("getHeight", actionContext);

        if(width != null && width > 0){
            dialog.setWidth(width);
        }
        if(height != null && height > 0){
            dialog.setHeight(height);
        }
        dialog.show();
        if(width != null && width > 0){
            dialog.setWidth(width);
        }
        if(height != null && height > 0){
            dialog.setHeight(height);
        }
    }

    public static void batchInsertDataObjectDialog(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        List<DataObject> dataObjects = self.doAction("getDataObjects", actionContext);
        Thing condition = self.doAction("getCondition", actionContext);
        Window owner = self.doAction("getOwnerWindow", actionContext);

        if(dataObjects != null && dataObjects.size() > 0){
            BatchInsertDataObjectDialog dialog = new BatchInsertDataObjectDialog(self, condition, dataObjects, actionContext);
            dialog.show(owner);
        }
    }
}
