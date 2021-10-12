package xworker.content;

import org.xmeta.Thing;

public class ThingRegistContent implements Content<Thing>{
    Thing thing;
    String type;
    Thing content;
    String descriptor;
    String contentDefaultOpenMethod;
    String contentDisplayType;
    String registType;

    public ThingRegistContent(Thing thing, String type, Thing content){
        this.thing = thing;
        this.type = type;
        this.content = content;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Thing getContent() {
        return content;
    }

    @Override
    public Thing getThing() {
        return thing;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public String getContentDefaultOpenMethod() {
        return contentDefaultOpenMethod;
    }

    public void setContentDefaultOpenMethod(String contentDefaultOpenMethod) {
        this.contentDefaultOpenMethod = contentDefaultOpenMethod;
    }

    public String getContentDisplayType() {
        return contentDisplayType;
    }

    public void setContentDisplayType(String contentDisplayType) {
        this.contentDisplayType = contentDisplayType;
    }

    public String getRegistType() {
        return registType;
    }

    public void setRegistType(String registType) {
        this.registType = registType;
    }
}
