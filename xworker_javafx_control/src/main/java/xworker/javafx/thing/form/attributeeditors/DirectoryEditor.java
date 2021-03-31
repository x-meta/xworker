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
import javafx.stage.DirectoryChooser;
import org.xmeta.Thing;
import xworker.javafx.thing.form.AttributeEditor;
import xworker.javafx.thing.form.ThingForm;

import java.io.File;

public class DirectoryEditor extends AttributeEditor {
    HBox hbox;
    TextField textField;
    Button selectButton;

    public DirectoryEditor(ThingForm thingForm, Thing attribute) {
        super(thingForm, attribute);
    }

    @Override
    public Node createEditor() {
        hbox = new HBox();
        textField = new TextField();
        int size = attribute.getInt("size");
        if(size > 0 ){
            textField.setMaxWidth(size * 6);
        }else{
            textField.setMaxWidth(120);
        }
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                thingForm.modified(DirectoryEditor.this);
            }
        });
        selectButton = new Button();
        selectButton.setText("*");
        selectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser fc = new DirectoryChooser();
                String path = textField.getText();
                if(path != null){
                    fc.setInitialDirectory(new File(path));
                }
                File file = fc.showDialog(hbox.getScene().getWindow());
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
