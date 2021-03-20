package xworker.javafx.thing.editor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.codes.XmlCoder;
import org.xmeta.util.UtilString;
import org.xml.sax.SAXException;
import xworker.lang.executor.Executor;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.function.Consumer;

public class ThingEditorXml implements ThingEditorContentNode{
    private static final String TAG = ThingEditorXml.class.getName();
    ThingEditor thingEditor;
    Node node;

    javafx.scene.control.Button formButton;
    javafx.scene.control.Button saveButton;
    javafx.scene.control.TextArea xmlText;

    Thing thing;
    boolean setting = false;
    boolean modified = false;

    public ThingEditorXml(ThingEditor thingEditor){
        this.thingEditor = thingEditor;

        ActionContext actionContext = new ActionContext();
        Thing nodeThing = World.getInstance().getThing("xworker.javafx.thing.editor.XmlNode/@Nodes/@xmlBox");
        node = nodeThing.doAction("create", actionContext);

        formButton = actionContext.getObject("formButton");
        saveButton = actionContext.getObject("saveButton");
        xmlText = actionContext.getObject("xmlText");

        formButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                thingEditor.showForm();
            }
        });

        xmlText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!setting){
                    thingEditor.modifiedProperty.set(true);
                    modified = true;
                }
            }
        });
        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                save();
            }
        });
    }

    @Override
    public boolean save() {
        if(modified) {
            String xml = xmlText.getText();
            Thing th = new Thing();
            try {
                XmlCoder.parse(th, xml);
                thing.getChilds().clear();
                thing.paste(th);

                thingEditor.refresh();
                modified = false;
            } catch (Exception e) {
                Executor.error(TAG, "Save xml error", e);
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean beforeChangeThing(Thing newThing) {
        if(newThing != this.thing && modified){
            String message = UtilString.getString("lang:d=已修改，要保存么？&en=Has modified, save it?",
                    thingEditor.actionContext);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.YES, ButtonType.NO);
            alert.setTitle(UtilString.getString("lang:d=保存&ne=Save", thingEditor.actionContext));
            alert.showAndWait().ifPresent(new Consumer<ButtonType>() {
                @Override
                public void accept(ButtonType buttonType) {
                    if(buttonType == ButtonType.YES) {
                        save();
                    }
                }
            });
        }

        return true;
    }

    @Override
    public void setThing(Thing thing, Thing descriptor) {
        this.thing = thing;
        try {
            setting = true;
            xmlText.setText(XmlCoder.encodeToString(thing));
            modified = false;
        } finally {
            setting = false;
        }
    }

    @Override
    public Node getNode() {
        return node;
    }
}
