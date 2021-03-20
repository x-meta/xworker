package xworker.javafx.thing.form.attributeeditors;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.xmeta.Thing;
import xworker.javafx.thing.form.AttributeEditor;
import xworker.javafx.thing.form.ThingForm;

import java.io.File;

public class FileEditor extends AttributeEditor {
    HBox hbox;
    TextField textField;
    Button selectButton;

    public FileEditor(ThingForm thingForm, Thing attribute, Property<Object> valueProperty) {
        super(thingForm, attribute, valueProperty);
    }

    @Override
    public Node createEditor() {
        hbox = new HBox();
        textField = new TextField();
        int size = attribute.getInt("size");
        if(size > 0 ){
            textField.setMaxWidth(size * 6);
        }else{
            textField.setMaxWidth(150);
        }
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                thingForm.modified(FileEditor.this);
            }
        });
        selectButton = new Button();
        selectButton.setText("*");
        selectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fc = new FileChooser();
                String path = textField.getText();
                if(path != null){
                    fc.setInitialFileName(path);
                }
                File file = fc.showOpenDialog(hbox.getScene().getWindow());
                if(file != null){
                    try {
                        String fileName = file.getCanonicalPath();
                        textField.setText(fileName);
                    }catch(Exception e){
                        textField.setText(file.getPath());
                    }
                }
            }
        });
        hbox.getChildren().addAll(textField, selectButton);
        return hbox;
    }

    @Override
    public void setValue(Object value) {
        if(value != null){
            textField.setText(String.valueOf(value));
        }else{
            textField.setText("");
        }
    }

    @Override
    public Object getValue() {
        return textField.getText();
    }
}
