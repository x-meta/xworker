package xworker.javafx.beans.binding;

import javafx.beans.binding.*;
import javafx.beans.value.ObservableNumberValue;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.util.UtilData;

import java.util.Locale;

public class NumberExpressionBaseActions {
    public static void createAsString(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        NumberExpressionBase parent = actionContext.getObject("parent");
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

    public static void createGreaterThan(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        NumberExpressionBase parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        Object other = self.doAction("getOther", actionContext);
        BooleanBinding binding = null;
        if(other != null){
            if(other instanceof ObservableNumberValue){
                binding = parent.greaterThan((ObservableNumberValue) other);
            }else if(other instanceof Integer){
                binding = parent.greaterThan((Integer) other);
            }else if(other instanceof Float){
                binding = parent.greaterThan((Float) other);
            }else if(other instanceof Long){
                binding = parent.greaterThan((Long) other);
            }else if(other instanceof Double){
                binding = parent.greaterThan((Double) other);
            }else{
                double value = UtilData.getDouble(other, 0);
                binding = parent.greaterThan(value);
            }
        }else{
            binding = parent.greaterThan(0);
        }

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createGreaterThanOrEqualTo(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        NumberExpressionBase parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        Object other = self.doAction("getOther", actionContext);
        BooleanBinding binding = null;
        if(other != null){
            if(other instanceof ObservableNumberValue){
                binding = parent.greaterThanOrEqualTo((ObservableNumberValue) other);
            }else if(other instanceof Number){
                double value = UtilData.getDouble(other, 0);
                binding = parent.greaterThanOrEqualTo(value);
            }
        }else{
            binding = parent.greaterThanOrEqualTo(0);
        }

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createIsEqualTo(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        NumberExpressionBase parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        Object other = self.doAction("getOther", actionContext);
        BooleanBinding binding = null;
        double epsilon = self.doAction("getEpsilon", actionContext);
        if (other != null) {
            if (other instanceof ObservableNumberValue) {
                binding = parent.isEqualTo((ObservableNumberValue) other, epsilon);
            }else if(other instanceof Integer){
                binding = parent.isEqualTo((Integer) other, epsilon);
            }else if(other instanceof Float){
                binding = parent.isEqualTo((Float) other, epsilon);
            }else if(other instanceof Long){
                binding = parent.isEqualTo((Long) other);
            }else if(other instanceof Double){
                binding = parent.isEqualTo((Double) other, epsilon);
            }else {
                double value = UtilData.getDouble(other, 0);
                binding = parent.isEqualTo(value, epsilon);
            }
        } else {
            binding = parent.isEqualTo(0);
        }

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createIsNotEqualTo(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        NumberExpressionBase parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        Object other = self.doAction("getOther", actionContext);
        BooleanBinding binding = null;
        double epsilon = self.doAction("getEpsilon", actionContext);
        if (other != null) {
            if (other instanceof ObservableNumberValue) {
                binding = parent.isNotEqualTo((ObservableNumberValue) other, epsilon);
            } else if(other instanceof Integer){
                binding = parent.isNotEqualTo((Integer) other, epsilon);
            }else if(other instanceof Float){
                binding = parent.isNotEqualTo((Float) other, epsilon);
            }else if(other instanceof Long){
                binding = parent.isNotEqualTo((Long) other);
            }else if(other instanceof Double){
                binding = parent.isNotEqualTo((Double) other, epsilon);
            }else {
                double value = UtilData.getDouble(other, 0);
                binding = parent.isNotEqualTo(value, epsilon);
            }
        } else {
            binding = parent.isNotEqualTo(0);
        }

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createLessThan(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        NumberExpressionBase parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        Object other = self.doAction("getOther", actionContext);
        BooleanBinding binding = null;
        if(other != null){
            if(other instanceof ObservableNumberValue){
                binding = parent.lessThan((ObservableNumberValue) other);
            }else if(other instanceof Integer){
                binding = parent.lessThan((Integer) other);
            }else if(other instanceof Float){
                binding = parent.lessThan((Float) other);
            }else if(other instanceof Long){
                binding = parent.lessThan((Long) other);
            }else if(other instanceof Double){
                binding = parent.lessThan((Double) other);
            }else {
                double value = UtilData.getDouble(other, 0);
                binding = parent.lessThan(value);
            }
        }else{
            binding = parent.lessThan(0);
        }

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createLessThanOrEqualTo(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        NumberExpressionBase parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        Object other = self.doAction("getOther", actionContext);
        BooleanBinding binding = null;
        if(other != null){
            if(other instanceof ObservableNumberValue){
                binding = parent.lessThanOrEqualTo((ObservableNumberValue) other);
            }else if(other instanceof Integer){
                binding = parent.lessThanOrEqualTo((Integer) other);
            }else if(other instanceof Float){
                binding = parent.lessThanOrEqualTo((Float) other);
            }else if(other instanceof Long){
                binding = parent.lessThanOrEqualTo((Long) other);
            }else if(other instanceof Double){
                binding = parent.lessThanOrEqualTo((Double) other);
            }else {
                double value = UtilData.getDouble(other, 0);
                binding = parent.lessThanOrEqualTo(value);
            }
        }else{
            binding = parent.lessThanOrEqualTo(0);
        }

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createAdd(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        NumberExpressionBase parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        Object other = self.doAction("getOther", actionContext);
        Object binding = null;
        if(other != null){
            if(other instanceof ObservableNumberValue){
                binding = parent.add((ObservableNumberValue) other);
            }else if(other instanceof Integer){
                binding = parent.add((Integer) other);
            }else if(other instanceof Float){
                binding = parent.add((Float) other);
            }else if(other instanceof Long){
                binding = parent.add((Long) other);
            }else if(other instanceof Double){
                binding = parent.add((Double) other);
            }else {
                double value = UtilData.getDouble(other, 0);
                binding = parent.add(value);
            }
        }else{
            binding = parent.add(0);
        }

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createDivide(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        NumberExpressionBase parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        Object other = self.doAction("getOther", actionContext);
        Object binding = null;
        if(other != null){
            if(other instanceof ObservableNumberValue){
                binding = parent.divide((ObservableNumberValue) other);
            }else if(other instanceof Integer){
                binding = parent.divide((Integer) other);
            }else if(other instanceof Float){
                binding = parent.divide((Float) other);
            }else if(other instanceof Long){
                binding = parent.divide((Long) other);
            }else if(other instanceof Double){
                binding = parent.divide((Double) other);
            }else {
                double value = UtilData.getDouble(other, 0);
                binding = parent.divide(value);
            }
        }else{
            binding = parent.divide(0);
        }

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createMultiply(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        NumberExpressionBase parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        Object other = self.doAction("getOther", actionContext);
        Object binding = null;
        if(other != null){
            if(other instanceof ObservableNumberValue){
                binding = parent.multiply((ObservableNumberValue) other);
            }else if(other instanceof Integer){
                binding = parent.multiply((Integer) other);
            }else if(other instanceof Float){
                binding = parent.multiply((Float) other);
            }else if(other instanceof Long){
                binding = parent.multiply((Long) other);
            }else if(other instanceof Double){
                binding = parent.multiply((Double) other);
            }else {
                double value = UtilData.getDouble(other, 0);
                binding = parent.multiply(value);
            }
        }else{
            binding = parent.multiply(0);
        }

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createNegate(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        NumberExpressionBase parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        Object binding = parent.negate();

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createSubtract(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        NumberExpressionBase parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }
        Object other = self.doAction("getOther", actionContext);
        Object binding = null;
        if(other != null){
            if(other instanceof ObservableNumberValue){
                binding = parent.subtract((ObservableNumberValue) other);
            }else if(other instanceof Integer){
                binding = parent.subtract((Integer) other);
            }else if(other instanceof Float){
                binding = parent.subtract((Float) other);
            }else if(other instanceof Long){
                binding = parent.subtract((Long) other);
            }else if(other instanceof Double){
                binding = parent.subtract((Double) other);
            }else {
                double value = UtilData.getDouble(other, 0);
                binding = parent.subtract(value);
            }
        }else{
            binding = parent.subtract(0);
        }

        actionContext.g().put(self.getMetadata().getName(), binding);
        actionContext.peek().put("parent", binding);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }
}
