package xworker.javafx.control;

import javafx.scene.Node;
import javafx.scene.control.*;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class GraphicActions {
    public static void createGraphic(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Object parent = actionContext.get("parent");

        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node){
                Node node = (Node) obj;
                if(parent instanceof Dialog) {
                    ((Dialog) parent).setGraphic(node);
                }else if(parent instanceof Labeled){
                    ((Labeled) parent).setGraphic(node);
                }else if(parent instanceof MenuItem){
                    ((MenuItem) parent).setGraphic(node);
                }else if(parent instanceof Menu){
                    ((Menu) parent).setGraphic(node);
                }else if(parent instanceof Tooltip){
                    ((Tooltip) parent).setGraphic(node);
                }else if(parent instanceof TableColumnBase){
                    ((TableColumnBase) parent).setGraphic(node);
                }else if(parent instanceof TreeItem){
                    ((TreeItem) parent).setGraphic(node);
                }
            }
        }
    }
}
