package xworker.javafx.thing.form.attributeeditors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import org.xmeta.Thing;
import xworker.javafx.thing.form.AttributeEditor;
import xworker.javafx.thing.form.ThingForm;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DatePickEditor extends AttributeEditor {
    DatePicker datePicker = null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DatePickEditor(ThingForm thingForm, Thing attribute) {
        super(thingForm, attribute);
    }

    @Override
    public Node createEditor() {
        datePicker = new DatePicker();
        int size = attribute.getInt("size");
        if(size > 0 ){
            datePicker.setPrefWidth(size * 1.5);
        }
        datePicker.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
                thingForm.modified(DatePickEditor.this);
            }
        });
        return datePicker;
    }

    @Override
    public void setValue(Object value) {
        if(value instanceof  String && !"".equals(value)){
            LocalDate date = LocalDate.parse((String) value, formatter);
            if(date != null) {
                datePicker.setValue(date);
            }
        }else if(value instanceof  LocalDate){
            datePicker.setValue((LocalDate) value);
        }else if(value instanceof  Date){
            Date date = (Date) value;
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            datePicker.setValue(localDate);
        }else{
            datePicker.setValue(null);
        }
    }

    @Override
    public Object getValue() {
        LocalDate date = datePicker.getValue();
        if(date != null){
            return date.format(formatter);
        }
        return null;
    }
}
