package xworker.javafx.dataobject.datastore;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataStore;
import xworker.dataObject.query.QueryConfig;
import xworker.javafx.util.FXThingLoader;
import xworker.poi.dataobject.POIDataObjectUtils;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataStoreContextMenu {
    DataStore dataStore;
    TableView<?> tableView;
    ContextMenu contextMenu;

    public DataStoreContextMenu(DataStore dataStore, TableView<?> tableView){
        this.dataStore = dataStore;
        this.tableView = tableView;
    }

    public void copyCellData(){
        ObservableList<TablePosition> cells = tableView.getSelectionModel().getSelectedCells();
        if(cells.size() > 0){
            TablePosition<?,?> tablePosition = cells.get(0);
            TableColumn<?,?> tableColumn = tablePosition.getTableColumn();
            Object value = tableColumn.getCellData(tablePosition.getRow());
            if (value != null) {
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(String.valueOf(value));
                clipboard.setContent(content);
            }
        }
    }

    public void copyRowData(){
        DataObject data = (DataObject) tableView.getSelectionModel().getSelectedItem();
        if(data != null){
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(data.toString());
            clipboard.setContent(content);
        }
    }

    public void exportTableDatas()  {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("*.xls");
        fileChooser.setTitle(UtilString.getString("lang:d=导出表格中的数据&en=Export table datas", new ActionContext()));
        File file = fileChooser.showSaveDialog(tableView.getScene().getWindow());
        if(file != null){
            List<DataObject> datas = new ArrayList<>();
            for(Object data : tableView.getItems()){
                datas.add((DataObject) data);
            }

            Popup popup = new Popup();
            popup.getContent().add(new Text(UtilString.getString("lang:d=正在导出，请稍候...&en=Exporting...", new ActionContext())));
            popup.show(tableView.getScene().getWindow());
            try {
                POIDataObjectUtils.exportToExcel(file, dataStore.getDataObject(), datas, true);

                if(Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file.getParentFile());
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally {
                popup.hide();
            }
        }
    }

    public void exportQueryDatas(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("*.xls");
        fileChooser.setTitle(UtilString.getString("lang:d=导出表格中的数据&en=Export table datas", new ActionContext()));
        File file = fileChooser.showSaveDialog(tableView.getScene().getWindow());
        if(file != null){
            QueryConfig queryConfig = dataStore.createQueryConfig();
            Popup popup = new Popup();
            Label  text = new Label(UtilString.getString("lang:d=正在导出，请稍候...&en=Exporting...", new ActionContext()));
            text.setMinWidth(200);
            text.setStyle(" -fx-background-color:grey;");
            VBox hBox = new VBox();
            ProgressBar progressBar = new ProgressBar();
            progressBar.setMinWidth(200);
            hBox.getChildren().addAll(text, progressBar);
            popup.getContent().add(hBox);
            popup.show(tableView.getScene().getWindow());
            new Thread(()->{
                try {
                    POIDataObjectUtils.exportToExcel(file, new DataObject(dataStore.getDataObject()), queryConfig, 65536,  true, dataStore.getActionContext());

                    if(Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(file.getParentFile());
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally {
                    Platform.runLater(popup::hide);
                }
            }).start();

        }
    }

    public static ContextMenu createContextMenu(TableView<?> tableView, DataStore dataStore){
        DataStoreContextMenu contextMenu = new DataStoreContextMenu(dataStore, tableView);
        Thing prototype = World.getInstance().getThing("xworker.javafx.dataobject.prototype.DataStoreContextMenu");
        ContextMenu menu = FXThingLoader.load(contextMenu, prototype, new ActionContext());
        contextMenu.contextMenu = menu;
        tableView.setContextMenu(menu);
        return menu;
    }

    public static Object createTableViewToolsMenu(ActionContext actionContext){
        TableView<?> tableView = actionContext.getObject("tableView");
        DataStore dataStore = actionContext.getObject("dataStore");

        return createContextMenu(tableView, dataStore);
    }
}
