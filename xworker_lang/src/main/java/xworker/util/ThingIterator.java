package xworker.util;

import org.xmeta.Thing;

import java.util.List;

public class ThingIterator {
    Thing thing;

    public ThingIterator(Thing thing){
        this.thing = thing;
    }

    public Thing pre(){
        Thing parent = thing.getParent();
        if(parent != null){
            List<Thing> childs = parent.getChilds();
            int index = childs.indexOf(thing);
            if(index <= 0){
                this.thing = parent;
            }else{
                this.thing = childs.get(index - 1);
            }
        }

        return thing;
    }

    public Thing getThing(){
        return thing;
    }

    public Thing next(){
        return next(false);
    }

    private Thing next(boolean back){
        if(!back){
            if(thing.getChilds().size() > 0){
                this.thing = thing.getChilds().get(0);
                return this.thing;
            }
        }

        Thing parent = thing.getParent();
        if(parent != null){
            List<Thing> childs = parent.getChilds();
            int index = childs.indexOf(thing);
            if(index == childs.size() - 1){
                this.thing = parent;
                next(true);
            }else{
                this.thing = childs.get(index + 1);
            }
        }

        return thing;
    }
}
