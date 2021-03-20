package xworker.javafx.beans.property;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import xworker.util.UtilData;

public class ProxyProperty{
    Property p1;
    Property p2;

    public ProxyProperty(Property p1, Property p2){
        this.p1 = p1;
        this.p2 = p2;

        p1.addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                p2.setValue(newValue);
            }
        });
        p2.addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                p1.setValue(newValue);
            }
        });

    }

    private void setValue(Property property, Object value){
        if(property instanceof  BooleanProperty){
            ((BooleanProperty) property).set(UtilData.getBoolean(value, false));
        }else if(property instanceof DoubleProperty){
            ((DoubleProperty) property).set(UtilData.getDouble(value, 0));
        }else if(property instanceof FloatProperty){
            ((FloatProperty) property).set(UtilData.getFloat(value, 0));
        }else if(property instanceof IntegerProperty){
            ((IntegerProperty) property).set(UtilData.getInt(value, 0));
        }else if(property instanceof  LongProperty){
            ((LongProperty) property).set(UtilData.getLong(value, 0));
        }else if(property instanceof StringProperty){
            ((StringProperty) property).set(UtilData.getString(value, null));
        }else{
            property.setValue(value);
        }
    }

}
