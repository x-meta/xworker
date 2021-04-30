package xworker.content;

import org.xmeta.Thing;

public class ThingContent implements Content<Thing>{
    String type;
    Thing content;

    public ThingContent(String type, Thing content){
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
}
