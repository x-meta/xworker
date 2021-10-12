package xworker.javafx.thingeditor;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.xmeta.*;
import org.xmeta.annotation.ActionField;
import org.xmeta.index.CategoryIndex;
import org.xmeta.util.UtilString;
import xworker.io.SystemIoRedirector;
import xworker.io.SystemIoRedirectorListener;
import xworker.javafx.application.ThingApplication;
import xworker.javafx.thing.editor.ThingEditor;
import xworker.javafx.thingeditor.dialog.CreateThingDialog;
import xworker.javafx.thingeditor.editors.EditorFactory;
import xworker.javafx.thingeditor.editors.PackageViewEditor;
import xworker.javafx.util.FXThingLoader;
import xworker.lang.executor.Executor;
import xworker.util.ThingUtils;
import xworker.util.UtilAction;
import xworker.util.XWorkerUtils;

import java.util.Comparator;
import java.util.function.Consumer;

public class SimpleThingEditor extends Application {
    private static final String TAG = SimpleThingEditor.class.getName();
    public static final String ID_PACKAGEVIEW = "PackageViewer";
    ActionContext actionContext = new ActionContext();

    @ActionField
    public javafx.scene.control.SplitPane mainSplitPane;

    @ActionField
    public javafx.scene.control.TabPane mainTabPane;

    @ActionField
    public javafx.scene.control.TreeView<Index> projectTree;

    @ActionField
    public javafx.scene.control.SplitPane topSplitPane;

    @ActionField
    public VBox rightBox;
    @ActionField
    public Tab packageViewTab;
    @ActionField
    public javafx.scene.control.MenuItem createThingMenuItem;
    @ActionField
    public javafx.scene.control.MenuItem rfreshMenuItem;
    @ActionField
    public javafx.scene.control.TextArea consoleArea;
    @ActionField
    public javafx.scene.control.MenuItem projectTreeNewCategoryMenuItem;
    @ActionField
    public javafx.scene.control.MenuItem projectTreeDeleteCategoryMenuItem;

    Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        World world = World.getInstance();

        Thing mainNodeThing = world.getThing("xworker.javafx.thingeditor.SimpleThingEditor/@Nodes/@mainPane");
        Parent rootNode = FXThingLoader.load(this, mainNodeThing, actionContext);

        Scene scene = new Scene(rootNode);
        primaryStage.setScene(scene);
        primaryStage.setWidth(1024);
        primaryStage.setHeight(768);
        primaryStage.setTitle("SimpleThingEditor");
        primaryStage.show();

        projectTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Index>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<Index>> observable, TreeItem<Index> oldValue, TreeItem<Index> newValue) {
                if(newValue == null){
                    return;
                }

                Index index = newValue.getValue();
                String type = index.getType();
                if(Index.TYPE_THINGMANAGER.equals(type) || Index.TYPE_CATEGORY.equals(type)){
                    index.refresh();
                }

                setPackageViewIndex(index);
                mainTabPane.getSelectionModel().select(packageViewTab);
            }
        });
        projectTree.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                TreeItem<Index> item  = projectTree.getSelectionModel().getSelectedItem();
                if(item != null){
                    Index index = item.getValue();
                    if(Index.TYPE_CATEGORY.equals(index.getType())){
                        projectTreeDeleteCategoryMenuItem.setDisable(false);
                    }else{
                        projectTreeDeleteCategoryMenuItem.setDisable(true);
                    }
                    if(Index.TYPE_CATEGORY.equals(index.getType()) || Index.TYPE_THINGMANAGER.equals(index.getType())){
                        createThingMenuItem.setDisable(false);
                        projectTreeNewCategoryMenuItem.setDisable(false);
                    }else{
                        createThingMenuItem.setDisable(true);
                        projectTreeNewCategoryMenuItem.setDisable(true);
                    }
                }
            }
        });

        TextAreaExecutService logService = new TextAreaExecutService(consoleArea);
        Executor.addDefaultExecutorService(logService);

        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN),
                new Runnable() {
                    @Override public void run() {
                        save();
                    }
                }
        );

        XWorkerUtils.setIde(new JavaFXIde(this));
        SystemIoRedirector.addListener(new SystemIoRedirectorListener() {
            @Override
            public void readLine(String line) {
                consoleArea.appendText(line + "\n");

                if(consoleArea.getText().length() > 1024 * 1024){
                    consoleArea.replaceText(0, 10 * 1024, "");
                }
            }

            @Override
            public void read(String text) {
                consoleArea.appendText(text);
                if(consoleArea.getText().length() > 1024 * 1024){
                    consoleArea.replaceText(0, 10 * 1024, "");
                }
            }
        });

        mainTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                if(newValue != null){
                    rightBox.getChildren().clear();

                    if(newValue.getUserData() instanceof Editor){
                        Editor<?> editor = (Editor<?>) newValue.getUserData();

                        Node structNode = editor.getStructureNode();
                        VBox.setVgrow(structNode, Priority.ALWAYS);

                        rightBox.getChildren().add(structNode);
                    }
                }
            }
        });
    }

    /**
     * 设置PackageView的索引，如果PackageView没有打开则先打开。
     *
     * @param index
     */
    public void setPackageViewIndex(Index index){
        Editor<Index> editor = (Editor<Index> ) getEditor(SimpleThingEditor.ID_PACKAGEVIEW);
        if(editor == null){
            editor = new PackageViewEditor();
        }
        editor.setContent(index);
        openEditor(editor);
    }

    /**
     * 根据标识获取编辑器。
     *
     * @param id
     * @return
     */
    public Editor<?> getEditor(String id){
        for(Tab tab : mainTabPane.getTabs()){
            if(id.equals(tab.getProperties().get("id"))){
                return (Editor<?>) tab.getUserData();
            }
        }

        return null;
    }

    public void openEditor(Editor<?> editor){
        String id = editor.getId();
        if(id == null){
            return;
        }
        editor.setSimpleThingEditor(this);
        editor.init();

        for(Tab tab : mainTabPane.getTabs()){
            if(id.equals(tab.getProperties().get("id"))){
                mainTabPane.getSelectionModel().select(tab);
                return;
            }
        }

        final Tab tab = new Tab();
        tab.getProperties().put("id", id);
        tab.setText(editor.getLabel());
        tab.setTooltip(new Tooltip(editor.getTooltip()));

        tab.setUserData(editor);
        tab.setContent(editor.getEditorNode());

        mainTabPane.getTabs().add(tab);
        mainTabPane.getSelectionModel().select(tab);
    }

    /**
     * 打开一个新建模型的编辑器，如果模型创建成功，那么在模型编辑器中打开该模型。
     */
    public void openCreateThingDialog(){
        openCreateThingDialog(new Consumer<Thing>() {
            @Override
            public void accept(Thing thing) {
                if(thing != null){
                    SimpleThingEditor.this.openThintEditor(thing);

                    PackageViewEditor editor = (PackageViewEditor) getEditor(SimpleThingEditor.ID_PACKAGEVIEW);
                    if(editor != null) {
                        editor.packageTabModel.refresh();
                    }
                }
            }
        });
    }

    /**
     * 打开一个新的创建模型对话框。
     *
     */
    public void openCreateThingDialog(Consumer<Thing> consumer){
        CreateThingDialog dialog = new CreateThingDialog();
        TreeItem<Index> item  = projectTree.getSelectionModel().getSelectedItem();
        if(item != null){
            Index index = item.getValue();
            if(Index.TYPE_THINGMANAGER.equals(index.getType())){
                dialog.setCategory(((ThingManager) index.getIndexObject()).getCategory(""));
            }else if(Index.TYPE_CATEGORY.equals(index.getType())){
                dialog.setCategory((Category) index.getIndexObject());
            }
        }

        dialog.showAndWait().ifPresent(consumer);
    }

    /**
     * 打开模型编辑器，如果对应的编辑器已经存在则选中。
     *
     * @param thing
     */
    public void openThintEditor(Thing thing){
        Editor<?> editor = EditorFactory.createEditor(thing);
        openEditor(editor);
    }

    public void removeEditor(Editor<?> editor){
        for(Tab tab : mainTabPane.getTabs()){
            if(tab.getUserData() == editor){
                mainTabPane.getTabs().remove(tab);
                return;
            }
        }
    }

    public void save(){
        Tab tab = mainTabPane.getSelectionModel().getSelectedItem();
        if(tab != null){
            Object userData = tab.getUserData();
            if(userData instanceof Editor){
                ((Editor<?>) userData).save();
            }
        }
    }

    public void saveAll(){
        for(Tab tab : mainTabPane.getTabs()){
            if(tab != null){
                Object userData = tab.getUserData();
                if(userData instanceof ThingEditor){
                    ((ThingEditor) userData).save();
                }
            }
        }
    }
    public static void run(){
        //SimpeleThingEditor是第一个应用
        ThingApplication.setStarted();

        //启动注册缓存
        ThingUtils.startRegistThingCache();

        Application.launch(SimpleThingEditor.class);
    }

    //xworker.javafx.thingeditor.JavaFXThingEditor/@run1
    public static void runByAction(ActionContext actionContext){
        Action startWebServer = actionContext.getObject("startWebServer");

        if(startWebServer != null){
            try{
                startWebServer.run(actionContext);
            }catch (Exception ignore){
            }
        }

        run();
    }

    /**
     * 作为在已经启动的JavaFX的应用中启动。
     */
    public void start(){
        Stage stage = new Stage();
        try {
            //启动注册缓存
            ThingUtils.startRegistThingCache();

            start(stage);
        }catch (Exception e){
            Executor.error(TAG, "Start SimpleThingEditor error", e);
        }
    }

    public void projectTreeRefresh(){
        Executor.info(TAG, "projectTreeRefresh");
    }

    public void refreshPackageView(){
        PackageViewEditor editor = (PackageViewEditor) getEditor(SimpleThingEditor.ID_PACKAGEVIEW);
        if(editor != null) {
            editor.packageTabModel.refresh();
        }
    }

    public static void runAsStage() throws Exception{
        Stage primaryStage = new Stage();

        SimpleThingEditor editor = new SimpleThingEditor();
        editor.start(primaryStage);

    }

    public void projectTreeNewCategory(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(UtilString.getString("lang:d=新建目录&en=Create Category", actionContext));
        dialog.setHeaderText(UtilString.getString("lang:d=请输入新的目录名。&en=Please input new category name.", actionContext));
        dialog.showAndWait().ifPresent(new Consumer<String>() {
            @Override
            public void accept(String s) {
                if(s != null && !"".equals(s)){
                    TreeItem<Index> parentItem = projectTree.getSelectionModel().getSelectedItem();
                    Index index = parentItem.getValue();
                    ThingManager thingManager = null;
                    String categoryName = null;
                    if(Index.TYPE_CATEGORY.equals(index.getType())){
                        Category category = (Category) index.getIndexObject();
                        thingManager = category.getThingManager();
                        categoryName = category.getName() + "." + s;
                    }else if(Index.TYPE_THINGMANAGER.equals(index.getType())){
                        thingManager = (ThingManager) index.getIndexObject();
                        categoryName = s;
                    }

                    if(thingManager != null && thingManager.getCategory(categoryName) == null) {
                        if(thingManager.createCategory(categoryName)){
                            Category newCategory = thingManager.getCategory(categoryName);
                            Index newIndex = new CategoryIndex(index, newCategory);
                            TreeItem<Index> treeItem = new TreeItem<>(newIndex);
                            parentItem.getChildren().add(treeItem);
                            parentItem.getChildren().sort(new Comparator<TreeItem<Index>>() {
                                @Override
                                public int compare(TreeItem<Index> o1, TreeItem<Index> o2) {
                                    return o1.getValue().getName().compareTo(o2.getValue().getName());
                                }
                            });

                            refreshPackageView();
                        }

                    }
                }
            }
        });
    }

    public void projectTreeRemoveCategory(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(UtilString.getString("lang:d=删除目录&en=Remove Category", actionContext));
        alert.setHeaderText(UtilString.getString("lang:d=确定要删除该目录么？子目录和目录下的模型将会一起删除。" +
                "&en=Are you sure you want to delete this directory? " +
                "The subdirectory and the model under the directory will be deleted together.", actionContext));
        alert.showAndWait().ifPresent(new Consumer<ButtonType>() {
            @Override
            public void accept(ButtonType buttonType) {
                if(buttonType == ButtonType.OK){
                    TreeItem<Index> parentItem = projectTree.getSelectionModel().getSelectedItem();
                    Index index = parentItem.getValue();
                    if(Index.TYPE_CATEGORY.equals(index.getType())){
                        Category category = (Category) index.getIndexObject();
                        if(category.getThingManager().removeCategory(category.getName())){
                            parentItem.getParent().getChildren().remove(parentItem);
                        }
                    }
                }
            }
        });
    }

    public void exit(){
        Platform.exit();
    }

    public void openThingDialog(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(UtilString.getString("lang:d=打开模型或动作&en=Open model or action", actionContext));
        dialog.setHeaderText(UtilString.getString("lang:d=请输入模型的路径或动作的类名！&en=Please input modle path or action class name!", actionContext));
        dialog.showAndWait().ifPresent(new Consumer<String>() {
            @Override
            public void accept(String s) {
                if(s != null){
                    //打开数据对象或者脚本对象
                    String str = s;
                    World world = World.getInstance();
                    Thing thing = world.getThing(str);
                    if(thing != null){
                        openThintEditor(thing);
                    }else{
                        String className = str;
                        str = str.replace('/', '.'); //Groovy编译错误是/号，转化为.号
                        str = str.replace('\\', '.');
                        Thing scriptObject = UtilAction.getActionThing(str);
                        if(scriptObject != null){
                            openThintEditor(scriptObject);
                        }else {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.getButtonTypes().add(ButtonType.OK);
                            alert.setHeaderText(UtilString.getString("lang:d=找不到要打开的模型或动作！&Can not find the model or action!", actionContext));
                            alert.show();
                            /*
                            //试图打开类
                            try {
                                def cls = Class.forName(className);
                                if (cls != null) {
                                    openClass(actionContext, "className", className);
                                    shell.dispose();
                                    return;
                                }
                            } catch (Exception e) {
                            }

                            def message = UtilString.getString("lang:d=找不到模型或类，&en=Can not find model or class, ", actionContext);
                            message = message + str;
                            notExists(actionContext, "message", message);

                             */
                        }
                    }
                }
            }
        });
    }

    public void clearConsole(){
        consoleArea.setText("");
    }
}
