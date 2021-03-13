package xworker.javafx.beans.binding;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.MapExpression;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.collections.ObservableMap;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class MapExpressionActions {
    public static void createAsString(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        MapExpression<?,?> parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }

        StringBinding binding = parent.asString();

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createIsEqualTo(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        MapExpression<?,?> parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        BooleanBinding binding = null;
        ObservableMap<?, ?> other = self.doAction("getOther", actionContext);
        if(other != null){
            binding = parent.isEqualTo(other);
        }
        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createIsNotEqualTo(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        MapExpression<?,?> parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        BooleanBinding binding = null;
        ObservableMap<?, ?> other = self.doAction("getOther", actionContext);
        if(other != null){
            binding = parent.isNotEqualTo(other);
        }
        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createIsNotNull(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        MapExpression<?,?> parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        BooleanBinding binding = parent.isNotNull();
        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createIsNull(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        MapExpression<?, ?> parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        BooleanBinding binding = parent.isNull();
        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createValueAt(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        MapExpression parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        ObjectBinding binding = null;
        Object key = self.doAction("getIndex", actionContext);
        if(key != null){
            binding = parent.valueAt(key);
        }

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }
}
