package xworker.javafx.thingeditor.dialog;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.xmeta.*;
import org.xmeta.annotation.ActionField;
import org.xmeta.util.UtilString;
import xworker.javafx.util.FXThingLoader;
import xworker.javafx.util.JavaFXUtils;
import xworker.util.Descriptors;

import java.util.function.Consumer;


public class CreateThingDialog extends Dialog<Thing> {
    @ActionField
    public javafx.scene.control.Button cancelButton;
    @ActionField
    public javafx.scene.control.TableView<Thing> classTabView;
    @ActionField
    public javafx.scene.control.TextField classText;
    @ActionField
    public javafx.scene.control.TreeView<Descriptors> classTreeView;
    @ActionField
    public javafx.scene.control.ChoiceBox<String> codeChoiceBox;
    @ActionField
    public javafx.scene.control.TextField nameText;
    @ActionField
    public javafx.scene.control.TextField pathText;
    @ActionField
    public javafx.scene.control.TextField searchText;
    @ActionField
    public javafx.scene.control.Button selectClassButton;
    @ActionField
    public javafx.scene.control.Button selectPathButton;
    @ActionField
    public javafx.scene.web.WebView webView;

    Category category;
    ActionContext actionContext = new ActionContext();

    public CreateThingDialog(){
        super();
        World world = World.getInstance();
        Thing nodeThing = world.getThing("xworker.javafx.thingeditor.dialogs.CreateThingDialog/@Content/@mainRoot");
        Node contentRoot = FXThingLoader.load(this, nodeThing, actionContext);

        for(ThingCoder coder : world.getThingCoders()){
            codeChoiceBox.getItems().add(coder.getType());
        }
        codeChoiceBox.getSelectionModel().selectFirst();

        DialogPane pane = this.getDialogPane();
        pane.setContent(contentRoot);
        pane.getButtonTypes().add(ButtonType.OK);
        pane.getButtonTypes().add(ButtonType.CANCEL);

        this.setDialogPane(pane);
        this.setWidth(800);
        this.setHeight(600);
        this.setResizable(true);
        this.setTitle(UtilString.getString("lang:d=新建模型&en=New Model", actionContext));
        this.setResultConverter(new Callback<ButtonType, Thing>() {
            @Override
            public Thing call(ButtonType param) {
                if(param == ButtonType.OK){

                }
                return null;
            }
        });

        //初始化模型列表
        Descriptors thingsGroup = new Descriptors(actionContext);
        initDescriptors(thingsGroup);
        classTreeView.setCellFactory(new Callback<TreeView<Descriptors>, TreeCell<Descriptors>>() {
            @Override
            public TreeCell<Descriptors> call(TreeView<Descriptors> param) {
                return new TreeCell<Descriptors>(){
                    @Override
                    protected void updateItem(Descriptors item, boolean empty) {
                        super.updateItem(item, empty);

                        if(item == null || empty){
                            this.setText(null);
                            this.setGraphic(null);
                            return;
                        }

                        this.setText(item.getThing().getMetadata().getLabel());
                        Image image = JavaFXUtils.getImage("icons/folder.png");
                        if(image != null){
                            this.setGraphic(new ImageView(image));
                        }
                    }
                };
            }
        });
        classTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Descriptors>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<Descriptors>> observable, TreeItem<Descriptors> oldValue, TreeItem<Descriptors> newValue) {
                if(newValue != null){
                    classTabView.getItems().clear();

                    Descriptors descs = newValue.getValue();
                    for(Thing thing : descs.getThings()){
                        classTabView.getItems().add(thing);
                    }

                    JavaFXUtils.showThingDesc(descs.getThing(), webView);
                }
            }
        });

        TableColumn<Thing, String> nameColumn = new TableColumn<>();
        nameColumn.setPrefWidth(300);
        nameColumn.setText(UtilString.getString("lang:d=名称&en=Name", actionContext));
        nameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Thing, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Thing, String> param) {
                Thing thing  = param.getValue();
                return new SimpleStringProperty(thing.getMetadata().getLabel());
            }
        });
        classTabView.getColumns().add(nameColumn);
        classTabView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Thing>() {
            @Override
            public void changed(ObservableValue<? extends Thing> observable, Thing oldValue, Thing newValue) {
                if(newValue != null){
                    String descriptor = newValue.getStringBlankAsNull("descriptor");
                    if(descriptor != null){
                        classText.setText(descriptor);

                        Thing desc = World.getInstance().getThing(descriptor);
                        JavaFXUtils.showThingDesc(desc, webView);
                    }

                }
            }
        });

        pane.lookupButton(ButtonType.OK).setDisable(true);
        pane.lookupButton(ButtonType.OK).addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(category == null){
                    String content = UtilString.getString("lang:d=未设置路径，要保存为临时模型么？！&en=Category not setted, are you want to create a temporary model?", actionContext);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, content, ButtonType.OK, ButtonType.CANCEL);
                    alert.setTitle(CreateThingDialog.class.getName());
                    alert.showAndWait().ifPresent(new Consumer<ButtonType>() {
                        @Override
                        public void accept(ButtonType buttonType) {
                            if(buttonType == ButtonType.CANCEL){
                                event.consume();
                            }
                        }
                    });
                }
                if(category != null) {
                    String path = category.getName() + "." + nameText.getText();
                    if (World.getInstance().getThing(path) != null) {
                        String header = UtilString.getString("lang:d=不能创建模型，" + path
                                + "模型已经存在!&en=Can not create model, model " + path + " already exists!", actionContext);
                        String content = UtilString.getString("lang:d=请使用其它名字！&en=Please use other name!", actionContext);
                        Alert alert = new Alert(Alert.AlertType.WARNING, content, ButtonType.OK);
                        alert.setTitle(CreateThingDialog.this.getTitle());
                        alert.setHeaderText(header);
                        alert.show();
                        event.consume();
                    }
                }
            }
        });

        nameText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue == null || "".equals(newValue.trim())){
                    pane.lookupButton(ButtonType.OK).setDisable(true);
                }else{
                    pane.lookupButton(ButtonType.OK).setDisable(false);
                }
            }
        });

        this.setResultConverter(new Callback<ButtonType, Thing>() {
            @Override
            public Thing call(ButtonType param) {
                if(param == ButtonType.OK) {
                    String path = nameText.getText();
                    if(category != null && category.getName() != null && !"".equals(category.getName())){
                        path = category.getName() + "." + path;
                    }
                    Thing thing = new Thing(classText.getText());
                    if(category != null){
                        thing.saveAs(category.getThingManager().getName(), path);
                    }
                    thing.set("name", nameText.getText());

                    return thing;
                }

                return null;
            }
        });
    }

    public void setCategory(Category category){
        if(category == null){
            throw new NullPointerException();
        }

        this.category = category;
        pathText.setText(category.getThingManager().getName() + ":" + category.getName());
    }

    private void initDescriptors(Descriptors descriptors){
        TreeItem<Descriptors> rootItem = new TreeItem<>();
        for(Descriptors child : descriptors.getChilds()){
            initDescriptors(rootItem, child);
        }

        classTreeView.setRoot(rootItem);
        //rootItem.setExpanded(true);
    }

    private void initDescriptors(TreeItem<Descriptors> parentItem, Descriptors descriptors){
        TreeItem<Descriptors> treeItem = new TreeItem<>(descriptors);
        parentItem.getChildren().add(treeItem);

        for(Descriptors child : descriptors.getChilds()){
            initDescriptors(treeItem, child);
        }
    }

    public void cancel(){
        setResult(null);
        this.close();
    }
}
