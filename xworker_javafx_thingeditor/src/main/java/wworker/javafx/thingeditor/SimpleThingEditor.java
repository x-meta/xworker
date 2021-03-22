package wworker.javafx.thingeditor;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;
import org.xmeta.ActionContext;
import org.xmeta.Index;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionField;
import wworker.javafx.thingeditor.dialog.CreateThingDialog;
import xworker.javafx.thing.editor.ThingEditor;
import xworker.javafx.thing.model.IndexTableViewModelEvent;
import xworker.javafx.util.FXThingLoader;
import xworker.lang.executor.Executor;

import javax.xml.soap.Node;
import java.io.File;
import java.util.function.Consumer;

public class SimpleThingEditor extends Application {
    private static final String TAG = SimpleThingEditor.class.getName();
    ActionContext actionContext = new ActionContext();

    @ActionField
    public javafx.scene.control.SplitPane mainSplitPane;

    @ActionField
    public javafx.scene.control.TabPane mainTabPane;

    @ActionField
    public xworker.javafx.thing.model.IndexTableViewModel packageTabModel;

    @ActionField
    public javafx.scene.control.TreeView<Index> projectTree;

    @ActionField
    public javafx.scene.control.SplitPane topSplitPane;

    @ActionField
    public javafx.scene.web.WebView webView;

    @Override
    public void start(Stage primaryStage) throws Exception {
        World world = World.getInstance();

        Thing mainNodeThing = world.getThing("xworker.javafx.thingeditor.SimpleThingEditor/@Nodes/@mainSplitPane");
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

                packageTabModel.setIndex(newValue.getValue());
            }
        });
        packageTabModel.setOnOpenIndex(new EventHandler<IndexTableViewModelEvent>() {
            @Override
            public void handle(IndexTableViewModelEvent event) {
                Executor.info(TAG, "PackageView OpenIndex");
                Index index = event.getIndex();
                if(Index.TYPE_THING.equals(index.getType())){
                    Thing thing = World.getInstance().getThing(index.getPath());
                    openThintEditor(thing);
                }
            }
        });
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
                }
            }
        });
    }

    /**
     * 打开一个新的创建模型对话框。
     *
     * @return 返回创建的模型，可能为null
     */
    public void openCreateThingDialog(Consumer<Thing> consumer){
        CreateThingDialog dialog = new CreateThingDialog();
        dialog.showAndWait().ifPresent(consumer);
    }

    /**
     * 打开模型编辑器，如果对应的编辑器已经存在则选中。
     *
     * @param thing
     */
    public void openThintEditor(Thing thing){
        if(thing == null){
            throw new NullPointerException();
        }

        thing = thing.getRoot();
        for(Tab tab : mainTabPane.getTabs()){
            if(tab.getProperties().get("thing") == thing){
                mainTabPane.getSelectionModel().select(tab);
                return;
            }
        }

        Tab tab = new Tab();
        tab.getProperties().put("thing", thing);
        tab.setText(thing.getMetadata().getLabel());
        tab.setTooltip(new Tooltip(thing.getMetadata().getThingManager().getName()
                + ":" + thing.getMetadata().getPath()));

        ThingEditor editor = new ThingEditor();
        tab.setContent(editor.getThingEditorNode());
        editor.setThing(thing);

        mainTabPane.getTabs().add(tab);
        mainTabPane.getSelectionModel().select(tab);

    }

    public static void run(){
        Application.launch();
    }

    public void projectTreeRefresh(){
        Executor.info(TAG, "projectTreeRefresh");
    }

    public static void runAsStage() throws Exception{
        Stage primaryStage = new Stage();

        SimpleThingEditor editor = new SimpleThingEditor();
        editor.start(primaryStage);

    }

    public static void main(String[] args){
        try {
            World world = World.getInstance();
            world.init("./xworker/");

            world.addFileThingManager("xworker_javafx_thingeditor",
                    new File("./xworker_javafx_thingeditor/src/main/resources/"), false, true);
            Application.launch(args);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
