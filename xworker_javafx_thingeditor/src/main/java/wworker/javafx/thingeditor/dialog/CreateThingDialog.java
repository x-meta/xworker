package wworker.javafx.thingeditor.dialog;

import javafx.scene.Node;
import javafx.scene.control.Dialog;

import javafx.scene.control.DialogPane;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.ThingCoder;
import org.xmeta.World;
import org.xmeta.annotation.ActionField;
import xworker.javafx.util.FXThingLoader;
import xworker.util.ThingGroup;


public class CreateThingDialog extends Dialog<Thing> {
    @ActionField
    public javafx.scene.control.Button cancelButton;
    @ActionField
    public javafx.scene.control.TableView<Thing> classTabView;
    @ActionField
    public javafx.scene.control.TextField classText;
    @ActionField
    public javafx.scene.control.TreeView<ThingGroup> classTreeView;
    @ActionField
    public javafx.scene.control.ChoiceBox<String> codeChoiceBox;
    @ActionField
    public javafx.scene.control.TextField nameText;
    @ActionField
    public javafx.scene.control.Button okButton;
    @ActionField
    public javafx.scene.control.TextField pathText;
    @ActionField
    public javafx.scene.control.Button searchButton;
    @ActionField
    public javafx.scene.control.TextField searchText;
    @ActionField
    public javafx.scene.control.Button selectClassButton;
    @ActionField
    public javafx.scene.control.Button selectPathButton;
    @ActionField
    public javafx.scene.web.WebView webView;

    ActionContext actionContext = new ActionContext();

    public CreateThingDialog(){
        World world = World.getInstance();
        Thing nodeThing = world.getThing("xworker.javafx.thingeditor.dialogs.CreateThingDialog");
        DialogPane pane = FXThingLoader.load(this, nodeThing, actionContext);

        for(ThingCoder coder : world.getThingCoders()){
            codeChoiceBox.getItems().add(coder.getType());
        }

        this.setDialogPane(pane);
        this.setWidth(640);
        this.setHeight(480);
        this.setResizable(true);
    }

    public void cancel(){
        setResult(null);
        this.close();
    }
}
