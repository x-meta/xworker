package xworker.util;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Descriptors implements Comparable<Descriptors>{
    Thing thing;
    List<Thing> things = new ArrayList<Thing>();
    List<Descriptors> childs = new ArrayList<Descriptors>();

    public Descriptors(ActionContext actionContext){
        World world= World.getInstance();

        Thing coreThings = world.getThing("xworker.lang.LangThings");
        if(coreThings != null){
            initThings(coreThings);
        }

        Thing registorThing = world.getThing("xworker.lang.ThingsIndex");
        List<Thing> thingsList = XWorkerUtils.searchRegistThings(registorThing, "child", new ArrayList<String>(), actionContext);
        if(thingsList != null) {
            for (Thing things : thingsList) {
                if (!things.getMetadata().getPath().equals("xworker.lang.LangThings")) {
                    initThings(things);
                }
            }
        }

        this.sort();
    }

    private void initThings(Thing things){
        for(Thing child : things.getAllChilds()) {
            String thingName = child.getThingName();
            if ("Category".equals(thingName)) {
                Descriptors group = null;
                for(Descriptors g : childs){
                    if(g.equals(child)){
                        group = g;
                        break;
                    }
                }
                if(group == null){
                    group = new Descriptors(child);
                    childs.add(group);
                }

                group.initThings(child);
            } else if ("Thing".equals(thingName)) {
                this.things.add(child);
            }
        }
    }

    public Descriptors(Thing thing){
        this.thing = thing;
    }

    public boolean equals(Thing thing){
        String thingName = thing.getThingName();
        if("Category".equals(thingName) && this.thing.getMetadata().getName().equals(thing.getMetadata().getName())){
            return true;
        }else{
            return false;
        }
    }

    public void sort(){
        Collections.sort(childs);

        Collections.sort(things, new Comparator<Thing>() {
            @Override
            public int compare(Thing o1, Thing o2) {
                return o1.getMetadata().getLabel().compareTo(o2.getMetadata().getLabel());
            }
        });

        for(Descriptors child : childs){
            child.sort();
        }
    }

    public Thing getThing(){
        return thing;
    }

    public List<Thing> getThings(){
        return things;
    }

    public List<Descriptors> getChilds(){
        return childs;
    }

    @Override
    public int compareTo(Descriptors o) {
        return thing.getMetadata().getLabel().compareTo(o.thing.getMetadata().getLabel());
    }
}
