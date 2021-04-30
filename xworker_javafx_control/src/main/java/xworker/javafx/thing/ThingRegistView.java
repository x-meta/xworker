package xworker.javafx.thing;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionField;
import xworker.content.Content;
import xworker.content.QuickContent;
import xworker.javafx.thing.thingregist.*;
import xworker.javafx.util.FXThingLoader;
import xworker.javafx.util.JavaFXUtils;
import xworker.lang.executor.Executor;
import xworker.util.ThingGroup;
import xworker.util.XWorkerUtils;

import javax.swing.text.AbstractDocument;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThingRegistView {
    private static final String TAG = ThingRegistView.class.getName();

    private static Map<String, ContentViewFactory> contentViewFactoryMap = new HashMap<>();
    static{
        contentViewFactoryMap.put("url", new UrlContentViewFactory());
        contentViewFactoryMap.put("thingDesc", new ThingDescContentViewFactory());
        contentViewFactoryMap.put("thingControl", new ThingControlContentViewFactory());
        contentViewFactoryMap.put("thingEditor", new ThingEditorContentViewFactory());
        contentViewFactoryMap.put("thingRegist", new ThingRegistContentViewFactory());
        contentViewFactoryMap.put("composite", new CompositeContentViewFactory());
        contentViewFactoryMap.put("shell", new ShellContentViewFactory());
        contentViewFactoryMap.put("uiFlow", new UIFlowContentViewFactory());
        contentViewFactoryMap.put("uiFunction", new UIFunctionContentViewFactory());
        contentViewFactoryMap.put("autoDemo", new AutoDemoContentViewFactory());
        contentViewFactoryMap.put("swtDemo", new SwtDemoContentViewFactory());
        contentViewFactoryMap.put("swtGuide", new SwtGuideContentViewFactory());
        contentViewFactoryMap.put("webDemo", new WebDemoContentViewFactory());
        contentViewFactoryMap.put("thingDemo", new ThingEditorContentViewFactory());
        contentViewFactoryMap.put("action", new ThingControlContentViewFactory());
        contentViewFactoryMap.put("groovy", new GroovyContentViewFactory());
        contentViewFactoryMap.put("code", new GroovyContentViewFactory());
        contentViewFactoryMap.put("html", new HtmlContentViewFactory());

    }

    Node rootNode;

    Thing thing;
    Thing registThing;
    ActionContext parentContext;
    String registType = "child";
    ActionContext actionContext;
    List<Thing> thingList;

    @ActionField
    public javafx.scene.layout.VBox contentNode;
    @ActionField
    public javafx.scene.control.TabPane contentTabPane;
    @ActionField
    public javafx.scene.control.SplitPane mainSplitPane;
    @ActionField
    public javafx.scene.control.TreeView<ThingGroup> registTree;

    public ThingRegistView(Thing thing, ActionContext actionContext){
        this.thing = thing;
        this.parentContext = actionContext;
        this.actionContext = new ActionContext();
        this.actionContext.put("parentContext", actionContext);

        registThing = thing.doAction("getRegistThing", actionContext);
        registType = thing.doAction("getRegistType", actionContext);
        String displayMethod = thing.doAction("getDisplayMethod", actionContext);
        Thing prototype = null;
        World world = World.getInstance();
        if("nocontent".equals(displayMethod)){
            prototype = world.getThing("xworker.javafx.thing.regist.prototypes.ThingRegistView/@299/@308/@315/@335");
        }else if("showcontentontab".equals(displayMethod)){
            prototype = world.getThing("xworker.javafx.thing.regist.prototypes.ThingRegistView/@299/@308/@590/@362");
        }else{
            prototype = world.getThing("xworker.javafx.thing.regist.prototypes.ThingRegistView/@299/@308/@347/@362");
        }
        rootNode = FXThingLoader.load(this, prototype, this.actionContext);

        registTree.setCellFactory(new Callback<TreeView<ThingGroup>, TreeCell<ThingGroup>>() {
            @Override
            public TreeCell<ThingGroup> call(TreeView<ThingGroup> param) {
                return new TextFieldTreeCell<ThingGroup>(){
                    @Override
                    public void updateItem(ThingGroup item, boolean empty) {
                        super.updateItem(item, empty);

                        if(item == null || empty){
                            return;
                        }

                        if(item.getThing() != null) {
                            setText(item.getThing().getMetadata().getLabel());
                        }else{
                            setText(item.getGroup());
                        }

                        Image image = item.getThing() == null ? JavaFXUtils.getImage("icons/folder.png")
                                : JavaFXUtils.getImage(item.getThing());
                        if(image != null){
                            setGraphic(new ImageView(image));
                        }
                    }
                };
            }
        });
        registTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<ThingGroup>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<ThingGroup>> observable, TreeItem<ThingGroup> oldValue, TreeItem<ThingGroup> newValue) {
                if(newValue != null){
                    itemSelected(newValue.getValue());
                }
            }
        });

        search("");
    }

    protected void itemSelected(ThingGroup thingGroup){
        if(thing != null){
            //触发模型的selected事件
            thing.doAction("selected", actionContext, "thingGroup", thingGroup);
        }

        if(thingGroup.getThing() != null && showContent()){
            Thing thing = thingGroup.getThing();

            Content<?> content = QuickContent.getContent(thing, actionContext);
            if(content != null){
                ContentViewFactory contentViewFactory = contentViewFactoryMap.get(content.getType());
                if(contentViewFactory != null){
                    Node node = contentViewFactory.createContentNode(content, actionContext);
                    if(node != null){
                        showContent(node, thing);
                    }
                }else{
                    Executor.warn(TAG, "Cannot display content, content view factory is null, thing=" + thing.getMetadata().getPath());
                    return;
                }
            }
        }
    }

    private void showContent(Node node, Thing thing){
        if(contentNode != null){
            contentNode.getChildren().clear();

            VBox.setVgrow(node, Priority.ALWAYS);
            contentNode.getChildren().add(node);
        }else if(contentTabPane != null){
            for(Tab tab : contentTabPane.getTabs()){
                if(tab.getUserData() == thing){
                    contentTabPane.getSelectionModel().select(tab);
                    return;
                }
            }

            Tab tab = new Tab();
            tab.setText(thing.getMetadata().getLabel());
            tab.setContent(node);
            tab.setUserData(thing);
            contentTabPane.getTabs().add(tab);
            contentTabPane.getSelectionModel().select(tab);
        }
    }

    public boolean showContent(){
        return contentNode != null || contentTabPane != null;
    }

    public void search(String q){
        thingList = XWorkerUtils.searchRegistThings(registThing, registType, Collections.emptyList(), this.actionContext);
        ThingGroup thingGroup = new ThingGroup();
        thingGroup.addThings(thingList);
        thingGroup.sort();

        TreeItem<ThingGroup> rootItem = new TreeItem<>();
        registTree.setRoot(rootItem);

        for(ThingGroup child : thingGroup.getChilds()) {
            initTreeItem(child, rootItem);
        }
    }

    private void initTreeItem(ThingGroup thingGroup, TreeItem<ThingGroup> parent){
        TreeItem<ThingGroup> treeItem = new TreeItem<>(thingGroup);
        parent.getChildren().add(treeItem);

        for(ThingGroup child : thingGroup.getChilds()){
            initTreeItem(child, treeItem);
        }
    }

    public Node getRootNode(){
        return rootNode;
    }

    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ThingRegistView view = new ThingRegistView(self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), view);

        actionContext.peek().put("parent", view.getRootNode());
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
        return view.getRootNode();
    }
}
