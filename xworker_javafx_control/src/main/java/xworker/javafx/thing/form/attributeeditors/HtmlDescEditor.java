package xworker.javafx.thing.form.attributeeditors;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.web.HTMLEditor;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.javafx.thing.form.AttributeEditor;
import xworker.javafx.thing.form.ThingForm;

public class HtmlDescEditor extends AttributeEditor {
    HTMLEditor editor;
    String thingPath;

    public HtmlDescEditor(ThingForm thingForm, Thing attribute) {
        super(thingForm, attribute);
    }

    @Override
    public Node createEditor() {
        editor = new HTMLEditor();
        editor.setPrefHeight(300);
        editor.setMinHeight(200);
        GridPane.setVgrow(editor, Priority.ALWAYS);
        GridPane.setHgrow(editor, Priority.ALWAYS);
        editor.accessibleTextProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                thingForm.modified(HtmlDescEditor.this);
            }
        });
        return editor;
    }

    @Override
    public void setValue(Object value) {
        thingPath = null;
        String htmlText = null;
        if(value instanceof Thing){
            htmlText = ((Thing) value).getMetadata().getDescription();
            thingPath = ((Thing) value).getMetadata().getPath();
        }else if(value instanceof String){
            Thing thing = World.getInstance().getThing((String) value);
            if(thing != null){
                thingPath = thing.getMetadata().getPath();
                htmlText = thing.getMetadata().getDescription();
            }
        }
        if(htmlText == null){
            editor.setHtmlText("");
        }else{
            editor.setHtmlText(htmlText);
        }
    }

    @Override
    public Object getValue() {
        return thingPath;
    }
}
