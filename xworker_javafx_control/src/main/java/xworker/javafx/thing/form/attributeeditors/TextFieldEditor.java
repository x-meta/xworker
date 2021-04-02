package xworker.javafx.thing.form.attributeeditors;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import org.xmeta.Thing;
import xworker.javafx.beans.property.ProxyProperty;
import xworker.javafx.thing.form.AttributeEditor;
import xworker.javafx.thing.form.ThingForm;
import xworker.javafx.util.FXCodeAssistor;

public class TextFieldEditor extends AttributeEditor {
    TextField textField;

    public TextFieldEditor(ThingForm thingForm, Thing attribute){
        super(thingForm, attribute);
    }

    @Override
    public Node createEditor() {
        double size = attribute.getDouble("size");
        textField = new TextField();
        if(size > 0 ){
            textField.setMaxWidth(size * 7);
        }else{
            textField.setMaxWidth(200);
        }

        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                thingForm.modified(TextFieldEditor.this);
            }
        });

        FXCodeAssistor assistor = FXCodeAssistor.bind(thingForm.getThing(), textField, thingForm.getActionContext());
        assistor.setThingFactory(new Callback<FXCodeAssistor, Thing>() {
            @Override
            public Thing call(FXCodeAssistor param) {
                return thingForm.getThing();
            }
        });
        return textField;
    }

    @Override
    public void setValue(Object value) {
        if(value == null){
            textField.setText("");
        }else{
            textField.setText(String.valueOf(value));
        }
    }

    @Override
    public Object getValue() {
        return textField.getText();
    }
}
