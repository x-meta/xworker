package xworker.javafx.thing.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import org.xmeta.*;
import org.xmeta.index.FileIndex;
import org.xmeta.index.ParentIndex;
import org.xmeta.thingManagers.FileThingManager;
import org.xmeta.util.UtilString;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class IndexTableViewModel {
    Index index = Index.getInstance();
    TableView<Index> tableView;

    public IndexTableViewModel(TableView<Index> tableView, ActionContext actionContext){
        this.tableView = tableView;

        //清空已有的列
        tableView.getColumns().clear();
        TableColumn<Index, String> nameColumn = new TableColumn<>();
        nameColumn.setText(UtilString.getString("lang:d=名称&en=Name", actionContext));
        nameColumn.setCellFactory(new Callback<TableColumn<Index, String>, TableCell<Index, String>>() {
            @Override
            public TableCell<Index, String> call(TableColumn<Index, String> param) {
                TextFieldTableCell<Index, String> cell = new TextFieldTableCell<Index, String>(){
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        Index index = (Index) this.getTableRow().getItem();
                        if(index == null){
                            return;
                        }
                        setText(index.getLabel());

                        Image image = IndexUtil.getImage(index);
                        if(image != null){
                            setGraphic(new ImageView(image));
                        }
                    }
                };

                return cell;
            }
        });
        nameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Index, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Index, String> param) {
                return new SimpleStringProperty(param.getValue().getName());
            }
        });
        nameColumn.setPrefWidth(200);
        tableView.getColumns().add(nameColumn);

        TableColumn<Index, String> labelColumn = new TableColumn<>();
        labelColumn.setText(UtilString.getString("lang:d=标签&en=Label", actionContext));
        labelColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Index, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Index, String> param) {
                return new SimpleStringProperty(param.getValue().getLabel());
            }
        });
        labelColumn.setCellFactory(new Callback<TableColumn<Index, String>, TableCell<Index, String>>() {
            @Override
            public TableCell<Index, String> call(TableColumn<Index, String> param) {
                return new TextFieldTableCell<>();
            }
        });
        labelColumn.setPrefWidth(200);
        tableView.getColumns().add(labelColumn);

        TableColumn<Index, String> dateColumn = new TableColumn<>();
        dateColumn.setPrefWidth(150);
        dateColumn.setText(UtilString.getString("lang:d=日期&en=Date", actionContext));
        dateColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Index, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Index, String> param) {
                Index index = param.getValue();
                String date = null;
                if(index.getLastModified() <= 0){
                    date = "";
                }else{
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    date = sf.format(new Date(index.getLastModified()));
                }

                return new SimpleStringProperty(date);
            }
        });
        dateColumn.setCellFactory(new Callback<TableColumn<Index, String>, TableCell<Index, String>>() {
            @Override
            public TableCell<Index, String> call(TableColumn<Index, String> param) {
                return new TextFieldTableCell<>();
            }
        });
        tableView.getColumns().add(dateColumn);

        tableView.setRowFactory(new Callback<TableView<Index>, TableRow<Index>>() {
            @Override
            public TableRow<Index> call(TableView<Index> param) {
                TableRow<Index> tableRow = new TableRow<>();
                tableRow.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if(event.getClickCount() >= 2 && event.getButton() == MouseButton.PRIMARY && !tableRow.isEmpty()){
                            Index index = tableRow.getItem();
                            if(index.getType().equals("parent")){
                                index = index.getParent().getParent();
                            }
                            if(index.getType().equals(Index.TYPE_THING) || index.getType().equals(Index.TYPE_FILE)) {
                            }else {
                                IndexTableViewModel.this.setIndex(index);
                            }

                            EventHandler<IndexTableViewModelEvent> handler = IndexTableViewModel.this.getOnOpenIndex();
                            if(handler != null){
                                handler.handle(new IndexTableViewModelEvent(index));
                            }
                        }
                    }
                });
                return tableRow;
            }
        });

        setIndex(Index.getInstance());
    }

    public void setIndex(Index index){
        tableView.getItems().clear();

        if(index.getParent() != null){
            //不是根节点
            tableView.getItems().add(new ParentIndex(index));
        }
        //index.refresh();
        for(Index child : index.getChilds()){
            tableView.getItems().add(child);
        }

        //刷新表格的文件，取消了Project，此功能目前不正常暂时屏蔽
        File fdir = null;
        if(index.getType() == Index.TYPE_CATEGORY){
            String path = index.getPath();
            ThingManager thingManager = ((Category) index.getIndexObject()).getThingManager();
            if(thingManager instanceof FileThingManager){
                fdir = new File(((FileThingManager) thingManager).getFilePath(), path.replace('.', '/'));
            }
        }else if(index.getType() == Index.TYPE_THINGMANAGER){
            ThingManager thingManager = (ThingManager) index.getIndexObject();
            if(thingManager instanceof FileThingManager){
                fdir = new File(((FileThingManager) thingManager).getFilePath());
            }
        }
        if(fdir != null){
            for(File f : fdir.listFiles()){
                if(f.isDirectory() || isThingFile(f.getName())) continue;
                FileIndex fileIndex = new FileIndex(index, f);
                tableView.getItems().add(fileIndex);
            }
        }
    }

    public boolean isThingFile(String fileName){
        for(ThingCoder coder : World.getInstance().getThingCoders()){
            if(fileName.endsWith(coder.getType())){
                return true;
            }
        }

        return false;
    }

    public final ObjectProperty<EventHandler<IndexTableViewModelEvent>> onOpenIndexProperty() { return onOpenIndex; }
    public final void setOnOpenIndex(EventHandler<IndexTableViewModelEvent> value) { onOpenIndexProperty().set(value); }
    public final EventHandler<IndexTableViewModelEvent> getOnOpenIndex() { return onOpenIndexProperty().get(); }
    private ObjectProperty<EventHandler<IndexTableViewModelEvent>> onOpenIndex = new ObjectPropertyBase<EventHandler<IndexTableViewModelEvent>>() {
        @Override
        public Object getBean() {
            return IndexTableViewModel.this;
        }

        @Override
        public String getName() {
            return "onOpenIndex";
        }
    };

    public static void create(ActionContext actionContext) {
        Thing self = actionContext.getObject("self");
        TableView<Index> parent = actionContext.getObject("parent");
        IndexTableViewModel model = new IndexTableViewModel(parent, actionContext);
        actionContext.g().put(self.getMetadata().getName(), model);

        actionContext.peek().put("parent", model);
        for (Thing child : self.getChilds()) {
            child.doAction("create", actionContext);
        }
    }
}
