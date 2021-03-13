package xworker.javafx.beans.binding;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableBooleanValue;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class BooleanExpressionActions {
    public static void createAnd(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        BooleanExpression parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }

        ObservableBooleanValue other = self.doAction("getOther", actionContext);
        if(other != null){
            BooleanBinding binding = parent.and(other);
            actionContext.g().put(self.getMetadata().getName(), binding);
            actionContext.peek().put("parent", binding);
            for(Thing child : self.getChilds()){
                child.doAction("create", actionContext);
            }
        }
    }

    public static void createIsEqualTo(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        BooleanExpression parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }

        ObservableBooleanValue other = self.doAction("getOther", actionContext);
        if(other != null){
            BooleanBinding binding = parent.isEqualTo(other);
            actionContext.g().put(self.getMetadata().getName(), binding);
            actionContext.peek().put("parent", binding);
            for(Thing child : self.getChilds()){
                child.doAction("create", actionContext);
            }
        }
    }

    public static void createIsNotEqualTo(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        BooleanExpression parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }

        ObservableBooleanValue other = self.doAction("getOther", actionContext);
        if(other != null){
            BooleanBinding binding = parent.isNotEqualTo(other);
            actionContext.g().put(self.getMetadata().getName(), binding);
            actionContext.peek().put("parent", binding);
            for(Thing child : self.getChilds()){
                child.doAction("create", actionContext);
            }
        }
    }

    public static void createOr(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        BooleanExpression parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }

        ObservableBooleanValue other = self.doAction("getOther", actionContext);
        if(other != null){
            BooleanBinding binding = parent.or(other);
            actionContext.g().put(self.getMetadata().getName(), binding);
            actionContext.peek().put("parent", binding);
            for(Thing child : self.getChilds()){
                child.doAction("create", actionContext);
            }
        }
    }

    public static void createAsString(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        BooleanExpression parent = actionContext.getObject("parent");
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

    public static void createNot(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        BooleanExpression parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }

        BooleanBinding binding = parent.not();
        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }
}
