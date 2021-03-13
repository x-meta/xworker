package xworker.javafx.beans.binding;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableObjectValue;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import java.util.Locale;

public class ObjectExpressionActions {
    public static void createAsString(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ObjectExpression<?> parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }

        Locale locale = self.doAction("getLocale", actionContext);
        String format = self.doAction("getFormat", actionContext);
        StringBinding binding = null;
        if(locale != null && format != null){
            binding = parent.asString();
        }else if(format != null){
            binding = parent.asString();
        }else {
            parent.asString();
        }

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createIsEqualTo(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ObjectExpression<?> parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        BooleanBinding binding = null;
        ObservableObjectValue<?> other = self.doAction("getOther", actionContext);
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
        ObjectExpression<?> parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        BooleanBinding binding = null;
        ObservableObjectValue<?> other = self.doAction("getOther", actionContext);
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
        ObjectExpression<?> parent = actionContext.getObject("parent");
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
        ObjectExpression<?> parent = actionContext.getObject("parent");
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


}
