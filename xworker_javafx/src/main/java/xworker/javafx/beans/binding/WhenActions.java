package xworker.javafx.beans.binding;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.When;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableNumberValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableStringValue;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.util.UtilData;

public class WhenActions {
    public static void create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ObservableBooleanValue condition = self.doAction("getCondition", actionContext);
        if(condition == null){
            condition = actionContext.getObject("parent");
        }

        When when = new When(condition);
        actionContext.g().put(self.getMetadata().getName(), when);

        actionContext.peek().put("parent", when);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createThenBoolean(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        When parent = actionContext.getObject("parent");

        Object value = self.doAction("getValue", actionContext);
        When.BooleanConditionBuilder builder = null;
        if(value instanceof ObservableBooleanValue){
            builder = parent.then((ObservableBooleanValue) value);
        }else{
            builder = parent.then(UtilData.getBoolean(value, false));
        }

        actionContext.peek().put("parent", builder);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createThenNumber(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        When parent = actionContext.getObject("parent");

        Object value = self.doAction("getValue", actionContext);
        When.NumberConditionBuilder builder = null;
        if(value instanceof ObservableNumberValue) {
            builder = parent.then((ObservableNumberValue) value);
        }else if(value instanceof Number) {
            Number n = (Number) value;
            if (value instanceof Integer) {
                builder = parent.then(n.intValue());
            } else if (value instanceof Float) {
                builder = parent.then(n.floatValue());
            } else if (value instanceof Double) {
                builder = parent.then(n.doubleValue());
            } else if (value instanceof Long) {
                builder = parent.then(n.longValue());
            } else {
                builder = parent.then(n.doubleValue());
            }
        }else{
            builder = parent.then(UtilData.getDouble(value, 0));
        }

        actionContext.peek().put("parent", builder);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createThenString(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        When parent = actionContext.getObject("parent");

        Object value = self.doAction("getValue", actionContext);
        When.StringConditionBuilder builder = null;
        if(value instanceof ObservableStringValue){
            builder = parent.then((ObservableStringValue) value);
        }else{
            builder = parent.then(String.valueOf(value));
        }

        actionContext.peek().put("parent", builder);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createThenObject(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        When parent = actionContext.getObject("parent");

        Object value = self.doAction("getValue", actionContext);
        When.ObjectConditionBuilder builder = null;
        if(value instanceof ObservableObjectValue){
            builder = parent.then((ObservableObjectValue) value);
        }else{
            builder = parent.then(value);
        }

        actionContext.peek().put("parent", builder);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createBooleanOtherwise(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        When.BooleanConditionBuilder parent = actionContext.getObject("parent");

        Object binding = null;
        Object value = self.doAction("getValue", actionContext);
        if(value instanceof ObservableBooleanValue){
            binding = parent.otherwise((ObservableBooleanValue) value);
        }else{
            binding = parent.otherwise(UtilData.getBoolean(value, false));
        }

        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createObjectOtherwise(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        When.ObjectConditionBuilder parent = actionContext.getObject("parent");

        Object binding = null;
        Object value = self.doAction("getValue", actionContext);
        When.ObjectConditionBuilder builder = (When.ObjectConditionBuilder) parent;
        if(value instanceof ObservableObjectValue){
            binding = builder.otherwise((ObservableObjectValue) value);
        }else{
            binding = builder.otherwise(value);
        }

        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createNumberOtherwise(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        When.NumberConditionBuilder parent = actionContext.getObject("parent");

        Object binding = null;
        Object value = self.doAction("getValue", actionContext);
        if(value instanceof ObservableNumberValue){
            binding = parent.otherwise((ObservableNumberValue) value);
        }else if(value instanceof Number) {
            Number n = (Number) value;
            if (value instanceof Integer) {
                binding = parent.otherwise(n.intValue());
            } else if (value instanceof Float) {
                binding = parent.otherwise(n.floatValue());
            } else if (value instanceof Double) {
                binding = parent.otherwise(n.doubleValue());
            } else if (value instanceof Long) {
                binding = parent.otherwise(n.longValue());
            } else {
                binding = parent.otherwise(n.doubleValue());
            }
        }else{
            binding = parent.otherwise(UtilData.getDouble(value, 0));
        }

        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createStringOtherwise(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        When.StringConditionBuilder parent = actionContext.getObject("parent");

        Object binding = null;
        Object value = self.doAction("getValue", actionContext);
        if(value instanceof ObservableStringValue){
            binding = parent.otherwise((ObservableStringValue) value);
        }else{
            binding = parent.otherwise(String.valueOf(value));
        }

        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }
}
