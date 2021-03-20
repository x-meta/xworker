package xworker.javafx.dataobject.beans;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;

public class DataObjectPropertyActions {
    public static void create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        DataObject dataObject = self.doAction("getDataObject", actionContext);
        if(dataObject == null){
            Thing descriptor = self.doAction("getDescriptor", actionContext);
            if(descriptor == null){
                dataObject = new DataObject();
            }else{
                dataObject = new DataObject(descriptor);
            }
        }
        actionContext.g().put(self.getMetadata().getName(), dataObject);

        actionContext.peek().put("parent", dataObject);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createBooleanProperty(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        DataObject dataObject = actionContext.getObject("parent");
        String name = self.doAction("getAttributeName", actionContext);
        if(name == null){
            name = self.getMetadata().getName();
        }
        DataObjectBooleanProperty property = new DataObjectBooleanProperty(dataObject, name);
        actionContext.g().put(self.getMetadata().getName(), property);

        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createDoubleProperty(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        DataObject dataObject = actionContext.getObject("parent");
        String name = self.doAction("getAttributeName", actionContext);
        if(name == null){
            name = self.getMetadata().getName();
        }
        DataObjectDoubleProperty property = new DataObjectDoubleProperty(dataObject, name);
        actionContext.g().put(self.getMetadata().getName(), property);

        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createStringProperty(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        DataObject dataObject = actionContext.getObject("parent");
        String name = self.doAction("getAttributeName", actionContext);
        if(name == null){
            name = self.getMetadata().getName();
        }
        DataObjectStringProperty property = new DataObjectStringProperty(dataObject, name);
        actionContext.g().put(self.getMetadata().getName(), property);

        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createFloatProperty(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        DataObject dataObject = actionContext.getObject("parent");
        String name = self.doAction("getAttributeName", actionContext);
        if(name == null){
            name = self.getMetadata().getName();
        }
        DataObjectFloatProperty property = new DataObjectFloatProperty(dataObject, name);
        actionContext.g().put(self.getMetadata().getName(), property);

        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }
    public static void createIntegerProperty(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        DataObject dataObject = actionContext.getObject("parent");
        String name = self.doAction("getAttributeName", actionContext);
        if(name == null){
            name = self.getMetadata().getName();
        }
        DataObjectIntegerProperty property = new DataObjectIntegerProperty(dataObject, name);
        actionContext.g().put(self.getMetadata().getName(), property);

        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }
    public static void createObjectProperty(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        DataObject dataObject = actionContext.getObject("parent");
        String name = self.doAction("getAttributeName", actionContext);
        if(name == null){
            name = self.getMetadata().getName();
        }
        DataObjectObjectProperty property = new DataObjectObjectProperty(dataObject, name);
        actionContext.g().put(self.getMetadata().getName(), property);

        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

}
