package xworker.javafx.beans.property;

import javafx.beans.property.*;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class MapAdapterActions {
    public static void create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        MapAdapter map = new MapAdapter(self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), map);

        actionContext.peek().put("parent", map);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public void createBooleanProperty(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        MapAdapter map = actionContext.getObject("parent");
        SimpleBooleanProperty property = new SimpleBooleanProperty();
        map.setProperty(self.getMetadata().getName(), property);

        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public void createDoubleProperty(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        MapAdapter map = actionContext.getObject("parent");
        SimpleDoubleProperty property = new SimpleDoubleProperty();
        map.setProperty(self.getMetadata().getName(), property);

        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public void createFloatProperty(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        MapAdapter map = actionContext.getObject("parent");
        SimpleFloatProperty property = new SimpleFloatProperty();
        map.setProperty(self.getMetadata().getName(), property);

        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public void createIntegerProperty(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        MapAdapter map = actionContext.getObject("parent");
        SimpleIntegerProperty property = new SimpleIntegerProperty();
        map.setProperty(self.getMetadata().getName(), property);

        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }
    public void createLongProperty(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        MapAdapter map = actionContext.getObject("parent");
        SimpleLongProperty property = new SimpleLongProperty();
        map.setProperty(self.getMetadata().getName(), property);

        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public void createObjectProperty(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        MapAdapter map = actionContext.getObject("parent");
        SimpleObjectProperty property = new SimpleObjectProperty();
        map.setProperty(self.getMetadata().getName(), property);

        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }
    public void createStringProperty(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        MapAdapter map = actionContext.getObject("parent");
        SimpleStringProperty property = new SimpleStringProperty();
        map.setProperty(self.getMetadata().getName(), property);

        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

}
