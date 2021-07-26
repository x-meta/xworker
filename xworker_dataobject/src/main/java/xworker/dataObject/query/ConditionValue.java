package xworker.dataObject.query;

import org.xmeta.Thing;

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
            type = "string";
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
