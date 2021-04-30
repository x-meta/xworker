package xworker.javafx.dataobject.cell;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.dataObject.DataObject;
import xworker.javafx.control.cell.NodeTableCell;

import java.util.List;

public class DataObjectEditDeleteCell {
    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Boolean edit = self.doAction("isEdit", actionContext);
        Boolean delete = self.doAction("isDelete", actionContext);
        Double editDialogWith = self.doAction("getEditDialogWith", actionContext);
        Double editDialogHeight = self.doAction("getEditDialogHeight", actionContext);

        Thing prototype = World.getInstance().getThing("xworker.javafx.dataobject.cell.prototypes.DataObjectEditDeleteCellPrototype");
        NodeTableCell<?,?> cell = prototype.doAction("create", actionContext);
        cell.getActionContext().g().put("editDialogWith", editDialogWith);
        cell.getActionContext().g().put("editDialogHeight", editDialogHeight);

        HBox hbox = cell.getActionContext().getObject("hbox");
        if(edit != null && !edit){
            Node editHyperLink = cell.getActionContext().getObject("editHyperLink");
            hbox.getChildren().remove(editHyperLink);
        }

        if(delete != null && !delete){
            Node deleteHyperLink = cell.getActionContext().getObject("deleteHyperLink");
            hbox.getChildren().remove(deleteHyperLink);
        }

        return cell;
    }

    public static void deleteOk(ActionContext actionContext){
        NodeTableCell<DataObject,?> cell = actionContext.getObject("cell");
        List<DataObject> dataObjects = actionContext.getObject("dataObjects");
        if(dataObjects != null){
            cell.getTableView().getItems().removeAll(dataObjects);
        }
    }
}
