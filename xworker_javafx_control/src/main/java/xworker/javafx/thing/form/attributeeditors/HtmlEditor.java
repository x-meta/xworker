package xworker.javafx.thing.form.attributeeditors;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.web.HTMLEditor;
import org.xmeta.Thing;
import xworker.javafx.thing.form.AttributeEditor;
import xworker.javafx.thing.form.ThingForm;

public class HtmlEditor extends AttributeEditor {
    HTMLEditor editor;
    public HtmlEditor(ThingForm thingForm, Thing attribute) {
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
                thingForm.modified(HtmlEditor.this);
            }
        });

        return editor;
    }

    @Override
    public void setValue(Object value) {
        if(value == null){
            editor.setHtmlText("");
        }else{
            editor.setHtmlText(String.valueOf(value));
        }
    }

    @Override
    public Object getValue() {
        String html = editor.getHtmlText();
        String tag = "<body contenteditable=\"true\">";
        int index1 = html.indexOf(tag);
        if(index1 == -1){
            return html;
        }
        index1 =  index1 + tag.length();
        try {
            return html.substring(index1, html.length() - 14);
        }catch(Exception e){
            return html.substring(index1, html.length());
        }
    }
}
