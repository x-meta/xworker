package xworker.javafx.scene.chart;

import javafx.collections.ObservableList;
import javafx.scene.chart.CategoryAxis;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

import java.util.Iterator;

public class CategoryAxisActions {
    public static void init(CategoryAxis node, Thing thing, ActionContext actionContext){
        AxisActions.init(node, thing, actionContext);

        if(thing.valueExists("endMargin")){
            node.setEndMargin(thing.getDouble("endMargin"));
        }
        if(thing.valueExists("gapStartAndEnd")){
            node.setGapStartAndEnd(thing.getBoolean("gapStartAndEnd"));
        }
        if(thing.valueExists("startMargin")){
            node.setStartMargin(thing.getDouble("startMargin"));
        }


    }

    public static CategoryAxis create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Object categories = null;
        if(self.valueExists("categories")){
            categories = JavaFXUtils.getObject(self, "categories", actionContext);
        }
        CategoryAxis node = null;
        if(categories instanceof ObservableList){
            node = new CategoryAxis((ObservableList<String>) categories);
        }else{
            node = new CategoryAxis();
            if(categories instanceof Iterable){
                Iterable<Object> iterable = (Iterable<Object>) categories;
                Iterator<Object> iter = iterable.iterator();
                while(iter.hasNext()){
                    node.getCategories().add(String.valueOf(iter.next()));
                }
            }
        }

        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
