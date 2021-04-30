package xworker.javafx.thing.form.attributeeditors;

import javafx.scene.Node;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;
import org.xmeta.Thing;
import xworker.javafx.thing.form.AttributeEditor;
import xworker.javafx.thing.form.ThingForm;
import xworker.javafx.util.ThingValueStringConverter;

import java.util.ArrayList;
import java.util.List;

public class MultiComboEditor extends AttributeEditor{
    CheckComboBox<Thing> comboBox;
    List<Thing> values;
    StringConverter<Thing> converter = null;

    public MultiComboEditor(ThingForm thingForm, Thing attribute) {
        super(thingForm, attribute);

        values = attribute.getAllChilds("value");
        converter = new ThingValueStringConverter(values);
    }

    @Override
    public Node createEditor() {
        comboBox = new CheckComboBox<>();
        double size = attribute.getDouble("size");
        if(size > 0 ){
            comboBox.setMaxWidth(size * 7);
        }else{
            comboBox.setMaxWidth(200);
        }

        comboBox.setConverter(converter);
        comboBox.getItems().addAll(values);

        return comboBox;
    }

    @Override
    public void setValue(Object value) {
        comboBox.getCheckModel().clearChecks();
        List<Thing> checkThings = new ArrayList<>();
        if (value instanceof Thing) {
            comboBox.getCheckModel().check((Thing) value);
        } else if (value != null) {
            String[] vs = String.valueOf(value).split("[,]");
            for (Thing valueThing : values) {
                String v = valueThing.getStringBlankAsNull("value");
                if (v != null) {
                    for (String vv : vs) {
                        if (v.equals(vv)) {
                            comboBox.getCheckModel().check(valueThing);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public Object getValue() {
        String text = null;
        for(Thing value : comboBox.getCheckModel().getCheckedItems()){
            String label = value.getStringBlankAsNull("value");
            if(label != null) {
                if (text == null) {
                    text = label;
                } else {
                    text = text + "," + label;
                }
            }
        }

        return text;
    }
}
