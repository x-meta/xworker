package xworker.javafx.dataobject.dialogs;

import javafx.application.Platform;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Window;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionField;
import xworker.dataObject.DataObject;
import xworker.javafx.util.FXThingLoader;

import java.util.List;

public class BatchInsertDataObjectDialog {
    @ActionField
    public javafx.scene.control.Label failureText;
    @ActionField
    public javafx.scene.control.ProgressBar progressBar;
    @ActionField
    public javafx.scene.control.Label repeatedText;
    @ActionField
    public javafx.scene.layout.VBox rootVBox;
    @ActionField
    public javafx.scene.control.Label successText;
    @ActionField
    public javafx.scene.control.Label totalText;

    List<DataObject> dataObjectList;
    ActionContext actionContext;
    Thing condition;
    Thing self;

    int total = 0;
    int successed = 0;
    int repeated = 0;
    int failed = 0;
    int current = 0;

    public BatchInsertDataObjectDialog(Thing self, Thing condition, List<DataObject> dataObjectList, ActionContext actionContext){
        this.self = self;
        this.condition = condition;
        this.dataObjectList = dataObjectList;
        this.actionContext = actionContext;
        total = dataObjectList.size();

        Thing thing = World.getInstance().getThing("xworker.javafx.dataobject.prototype.BachInsertDataObjectDialog/@Nodes/@rootVBox");
        FXThingLoader.load(this, thing, new ActionContext());
        rootVBox.setStyle("-fx-background-color:#FFFFFF;-fx-background-radius:5 5 5 5;-fx-border-color:#000; -fx-border-radius:3 3 3 3;");
    }

    public void updateStatus(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                failureText.setText(String.valueOf(failed));
                repeatedText.setText(String.valueOf(repeated));
                successText.setText(String.valueOf(successed));
                progressBar.setProgress(1d * current / total);
            }
        });
    }

    public void show(Window owner){
        Popup popup = new Popup();
        popup.getContent().add(rootVBox);
        popup.setWidth(300);
        popup.setHeight(50);

        popup.show(owner);

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    for (DataObject dataObject : dataObjectList) {
                        current++;

                        if(condition != null){
                            List<DataObject> datas = dataObject.query(actionContext, condition, dataObject);
                            if(datas != null && datas.size() > 0){
                                repeated++;

                                self.doAction("onRepeated", actionContext, "data", dataObject);
                                updateStatus();
                                continue;
                            }
                        }

                        try{
                            dataObject = dataObject.create(actionContext);
                            if(dataObject == null){
                                failed++;
                                self.doAction("onFailed", actionContext, "data", dataObject, "exception", null);
                                failureText.setText(String.valueOf(failed));
                            }else{
                                successed++;
                                self.doAction("onSuccessed", actionContext, "data", dataObject);
                                successText.setText(String.valueOf(successed));
                            }
                        }catch(Exception e){
                            failed++;
                            self.doAction("onFailed", actionContext, "data", dataObject, "exception", null);
                            failureText.setText(String.valueOf(failed));
                        }

                    }
                    updateStatus();

                }finally{
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            popup.hide();
                        }
                    });

                    System.out.println("pop window hidded!");
                }
            }
        }).start();


    }
}
