package xworker.javafx.util;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;
import xworker.dataObject.DataObject;
import xworker.javafx.dataobject.DataObjectForm;
import xworker.javafx.dataobject.DataObjectFormType;

import java.util.function.Consumer;


public class DataObejctFXUtils {
    public static void showCreateDataObjectDialog(Thing dataObject, Consumer<DataObject> consumer, ActionContext actionContext){
        Dialog<DataObject> dialog = new Dialog<DataObject>();
        dialog.setTitle(UtilString.getString("lang:d=新建&en=New ", actionContext) + dataObject.getMetadata().getLabel());
        DataObjectForm form = new DataObjectForm();
        form.setType(DataObjectFormType.CREATE);
        form.setDataObject(dataObject);
        dialog.getDialogPane().setHeader(form.getFormNode());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            DataObject data = form.getDataObject();
            try {
                data = data.create(actionContext);
                if(data == null){
                    Alert alert = new Alert(Alert.AlertType.ERROR, UtilString.getString("lang:d=创建数据失败！&en=Create data failed!", actionContext), ButtonType.OK);
                    alert.showAndWait();
                    event.consume();
                }else{
                    dialog.setResult(data);
                }
            }catch(Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                alert.showAndWait();
                event.consume();
            }
        });

        if(consumer != null){
            dialog.showAndWait().ifPresent(consumer);
        }else{
            dialog.showAndWait();
        }
    }
}
