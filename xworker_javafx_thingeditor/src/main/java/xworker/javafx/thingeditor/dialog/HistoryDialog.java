package xworker.javafx.thingeditor.dialog;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;
import xworker.javafx.thing.editor.ThingEditor;
import xworker.lang.executor.Executor;
import xworker.util.ThingBackupUtil;

import java.io.File;
import java.util.List;

@ActionClass
public class HistoryDialog {
    private static final String TAG = HistoryDialog.class.getName();

    @ActionField
    public javafx.stage.Stage stage;
    @ActionField
    public javafx.scene.control.Button closeButton;
    @ActionField
    public javafx.scene.control.ListView<File> historyListView;
    @ActionField
    public javafx.scene.control.Button restoreButton;
    @ActionField
    public javafx.scene.layout.VBox thingEditorNode;
    @ActionField
    Thing currentThing;
    @ActionField
    ActionContext actionContext;
    /** 用于浏览历史模型的模型编辑器，用于当前窗口 */
    ThingEditor thingEditor;
    /** 编辑currentThing的那个模型编辑器 */
    ThingEditor currentThingEditor;

    public void init(){
        currentThingEditor = (ThingEditor) actionContext.getObject("thingEditor");

        if(currentThing != null){
            historyListView.setCellFactory(new Callback<ListView<File>, ListCell<File>>() {
                @Override
                public ListCell<File> call(ListView<File> param) {
                    return new TextFieldListCell<File>(){
                        @Override
                        public void updateItem(File item, boolean empty) {
                            super.updateItem(item, empty);

                            if(item == null || empty){
                                return;
                            }

                            setText(item.getName());
                        }
                    };
                }
            });
            historyListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<File>() {
                @Override
                public void changed(ObservableValue<? extends File> observable, File oldValue, File newValue) {
                    if(newValue != null){
                        if(thingEditor == null){
                            thingEditor = new ThingEditor();
                            VBox.setVgrow(thingEditor.getThingEditorNode(), Priority.ALWAYS);
                            thingEditorNode.getChildren().add(thingEditor.getThingEditorNode());
                        }

                        try {
                            thingEditor.setThing(ThingBackupUtil.getThing(currentThing, newValue));
                        }catch(Exception e){
                            Executor.warn(TAG, "Get thing from backup error", e);
                        }
                    }
                }
            });

            List<File> backUps = ThingBackupUtil.getBackupFiles(currentThing.getRoot());
            historyListView.getItems().addAll(backUps);
        }
    }

    public void restore(){
        File file = historyListView.getSelectionModel().getSelectedItem();
        if(file != null && currentThingEditor != null){
            try {
                Thing thing = ThingBackupUtil.restore(currentThing.getRoot(), file.getAbsolutePath());

                currentThingEditor.setThing(thing);
            }catch (Exception e){
                Executor.warn(TAG, "Restore thing error", e);
            }
        }
        stage.close();
    }

    public void close(){
        stage.close();
    }
}
