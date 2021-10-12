package xworker.dataObject.query;

import org.xmeta.Thing;

import java.util.Date;

public class ConditionValue {
    Condition condition;
    Object value;
    String type;
    Thing attribute;

    public ConditionValue(Condition condition, Object value, Thing attribute){
        this.condition = condition;
        this.value = value;
        this.attribute = attribute;

        if(attribute != null){
            type = attribute.getStringBlankAsNull("type");
        }
        if(type == null) {
            type = condition.getConditionThing().getStringBlankAsNull("type");
        }
        if(type == null){
            if(value instanceof Long){
                type = "long";
            }else if(value instanceof Integer){
                type = "int";
            }else if(value instanceof Byte){
                type = "byte";
            }else if(value instanceof Double){
                type = "double";
            }else if(value instanceof Float){
                type = "float";
            }else if(value instanceof Short){
                type = "short";
            }else if(value instanceof Byte[]){
                type = "byte[]";
            }else if(value instanceof Date){
                type = "datetime";
            } else {
                type = "string";
            }
        }
    }

    public Condition getCondition() {
        return condition;
    }

    public Object getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public Thing getAttribute() {
        return attribute;
    }

    @Override
    public String toString() {
        return "ConditionValue{" +
                "condition=" + condition.getAttributeName() +
                ", value=" + value +
                '}';
    }
}
