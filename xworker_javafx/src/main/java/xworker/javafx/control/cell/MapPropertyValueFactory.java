package xworker.javafx.control.cell;

import javafx.beans.NamedArg;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.beans.property.*;

import java.util.Map;

public class MapPropertyValueFactory<T> implements Callback<TableColumn.CellDataFeatures<Map,T>, ObservableValue<T>> {

    private final String key;

    public MapPropertyValueFactory(final @NamedArg("key") String key) {
        this.key = key;
    }

    @Override public ObservableValue<T> call(TableColumn.CellDataFeatures<Map, T> cdf) {
        Map map = cdf.getValue();
        Object value = map.get(key);

        if (value instanceof ObservableValue) {
            return (ObservableValue)value;
        }

        if (value instanceof Boolean) {
            return (ObservableValue<T>) new MapBooleanProperty(map, key);
        } else if (value instanceof Integer) {
            return (ObservableValue<T>) new MapIntegerProperty(map, key);
        } else if (value instanceof Float) {
            return (ObservableValue<T>) new MapFloatProperty(map, key);
        } else if (value instanceof Long) {
            return (ObservableValue<T>) new MapLongProperty(map, key);
        } else if (value instanceof Double) {
            return (ObservableValue<T>) new MapDoubleProperty(map, key);
        } else if (value instanceof String) {
            return (ObservableValue<T>) new MapStringProperty(map, key);
        }

        // fall back to an object wrapper
        return new MapObjectProperty(map, key);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static MapPropertyValueFactory<Object> create(ActionContext actionContext) {
        Thing self = actionContext.getObject("self");
        Object parent = actionContext.getObject("parent");

        String key = self.doAction("getKey", actionContext);
        MapPropertyValueFactory<Object> mf = new MapPropertyValueFactory<Object>(key);
        actionContext.g().put(self.getMetadata().getName(), mf);

        if(parent instanceof TableColumn) {
            ((TableColumn) parent).setCellValueFactory(mf);
        }
        return mf;
    }
}
