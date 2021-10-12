package xworker.content;

import org.xmeta.Thing;

public class ThingContent implements Content<Thing>{
    Thing thing;
    String type;
    Thing content;

    public ThingContent(Thing thing, String type, Thing content){
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
}
