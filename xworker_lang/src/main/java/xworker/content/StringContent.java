package xworker.content;

import org.xmeta.Thing;

public class StringContent implements Content<String>{
    String type;
    String content;
    Thing thing;

    public StringContent(Thing thing, String type, String content){
        this.thing = thing;
        this.type = type;
        this.content = content;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public Thing getThing() {
        return thing;
    }
}
