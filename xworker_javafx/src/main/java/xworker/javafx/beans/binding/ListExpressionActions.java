package xworker.javafx.beans.binding;

import javafx.beans.binding.*;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableNumberValue;
import javafx.collections.ObservableList;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.util.UtilData;

public class ListExpressionActions {
    public static void createAsString(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ListExpression<?> parent = actionContext.getObject("parent");
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
        ListExpression<?> parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        BooleanBinding binding = null;
        ObservableList<?> other = self.doAction("getOther", actionContext);
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
        ListExpression<?> parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        BooleanBinding binding = null;
        ObservableList<?> other = self.doAction("getOther", actionContext);
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
        ListExpression<?> parent = actionContext.getObject("parent");
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
        ListExpression<?> parent = actionContext.getObject("parent");
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
        ListExpression<?> parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        ObjectBinding binding = null;
        Object index = self.doAction("getIndex", actionContext);
        if(index instanceof ObservableIntegerValue){
            binding = parent.valueAt((ObservableIntegerValue) index);
        }else{
            binding = parent.valueAt(UtilData.getInt(index, 0));
        }

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }
}
