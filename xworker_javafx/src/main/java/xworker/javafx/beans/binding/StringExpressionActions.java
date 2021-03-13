package xworker.javafx.beans.binding;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.value.ObservableStringValue;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class StringExpressionActions {
    public static void createGreaterThan(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        StringExpression parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        BooleanBinding binding = null;
        Object other = self.doAction("getOther", actionContext);
        if(other instanceof ObservableStringValue){
            binding = parent.greaterThan((ObservableStringValue) other);
        }else{
            binding = parent.greaterThan(String.valueOf(other));
        }

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createGreaterThanOrEqualTo(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        StringExpression parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        BooleanBinding binding = null;
        Object other = self.doAction("getOther", actionContext);
        if(other instanceof ObservableStringValue){
            binding = parent.greaterThanOrEqualTo((ObservableStringValue) other);
        }else{
            binding = parent.greaterThanOrEqualTo(String.valueOf(other));
        }

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createIsEqualTo(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        StringExpression parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        BooleanBinding binding = null;
        Object other = self.doAction("getOther", actionContext);
        if(other instanceof ObservableStringValue){
            binding = parent.isEqualTo((ObservableStringValue) other);
        }else{
            binding = parent.isEqualTo(String.valueOf(other));
        }

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createIsEqualToIgnoreCase(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        StringExpression parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        BooleanBinding binding = null;
        Object other = self.doAction("getOther", actionContext);
        if(other instanceof ObservableStringValue){
            binding = parent.isEqualToIgnoreCase((ObservableStringValue) other);
        }else{
            binding = parent.isEqualToIgnoreCase(String.valueOf(other));
        }

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createIsNotEqualTo(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        StringExpression parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        BooleanBinding binding = null;
        Object other = self.doAction("getOther", actionContext);
        if(other instanceof ObservableStringValue){
            binding = parent.isNotEqualTo((ObservableStringValue) other);
        }else{
            binding = parent.isNotEqualTo(String.valueOf(other));
        }

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createIsNotEqualToIgnoreCase(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        StringExpression parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        BooleanBinding binding = null;
        Object other = self.doAction("getOther", actionContext);
        if(other instanceof ObservableStringValue){
            binding = parent.isNotEqualToIgnoreCase((ObservableStringValue) other);
        }else{
            binding = parent.isNotEqualToIgnoreCase(String.valueOf(other));
        }

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createLessThan(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        StringExpression parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        BooleanBinding binding = null;
        Object other = self.doAction("getOther", actionContext);
        if(other instanceof ObservableStringValue){
            binding = parent.lessThan((ObservableStringValue) other);
        }else{
            binding = parent.lessThan(String.valueOf(other));
        }

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createLessThanOrEqualTo(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        StringExpression parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        BooleanBinding binding = null;
        Object other = self.doAction("getOther", actionContext);
        if(other instanceof ObservableStringValue){
            binding = parent.lessThanOrEqualTo((ObservableStringValue) other);
        }else{
            binding = parent.lessThanOrEqualTo(String.valueOf(other));
        }

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createIsEmpty(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        StringExpression parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        BooleanBinding binding = parent.isEmpty();

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createIsNotEmpty(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        StringExpression parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        BooleanBinding binding = parent.isNotEmpty();

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createLength(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        StringExpression parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        IntegerBinding binding = parent.length();

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }
}
