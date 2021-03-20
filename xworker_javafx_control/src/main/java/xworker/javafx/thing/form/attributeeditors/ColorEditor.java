package xworker.javafx.thing.form.attributeeditors;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import org.xmeta.Thing;
import xworker.javafx.thing.form.AttributeEditor;
import xworker.javafx.thing.form.ThingForm;

public class ColorEditor extends AttributeEditor {
    ColorPicker colorPicker = null;

    public ColorEditor(ThingForm thingForm, Thing attribute, Property<Object> valueProperty) {
        super(thingForm, attribute, valueProperty);
    }

    @Override
    public Node createEditor() {
        colorPicker = new ColorPicker();
        int size = attribute.getInt("size");
        if(size > 0 ){
            colorPicker.setPrefWidth(size * 1.5);
        }
        colorPicker.valueProperty().addListener(new ChangeListener<Color>() {
            @Override
            public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
                thingForm.modified(ColorEditor.this);
            }
        });
        return colorPicker;
    }

    @Override
    public void setValue(Object value) {
        if(value instanceof  String){
            Color color = Color.web((String) value);
            colorPicker.setValue(color);
        }else if(value instanceof  Color){
            colorPicker.setValue((Color) value);
        }else{
            colorPicker.setValue(null);
        }
    }

    @Override
    public Object getValue() {
        Color color = colorPicker.getValue();
        if(color != null){
            int r = (int)Math.round(color.getRed() * 255.0);
            int g = (int)Math.round(color.getGreen() * 255.0);
            int b = (int)Math.round(color.getBlue() * 255.0);

            return String.format("#%02x%02x%02x" , r, g, b);
        }else{
            return null;
        }
    }
}
