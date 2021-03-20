package xworker.javafx.thing.form.attributeeditors;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.xmeta.Thing;
import xworker.javafx.thing.form.AttributeEditor;
import xworker.javafx.thing.form.ThingForm;

public class TextAreaEditor extends AttributeEditor {
    TextArea textArea;
    public TextAreaEditor(ThingForm thingForm, Thing attribute, Property<Object> valueProperty) {
        super(thingForm, attribute, valueProperty);
    }

    @Override
    public Node createEditor() {
        textArea = new TextArea();
        textArea.setPrefHeight(300);
        //总是自动扩展
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                thingForm.modified(TextAreaEditor.this
                );
            }
        });
        return textArea;
    }

    @Override
    public void setValue(Object value) {
        if(value == null){
            textArea.setText("");
        }else{
            textArea.setText(String.valueOf(value));
        }
    }

    @Override
    public Object getValue() {
        return textArea.getText();
    }
}
