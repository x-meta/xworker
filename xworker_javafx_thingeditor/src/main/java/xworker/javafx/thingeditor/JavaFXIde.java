package xworker.javafx.thingeditor;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;
import xworker.lang.actions.ActionContainer;
import xworker.lang.executor.Executor;
import xworker.util.Callback;
import xworker.util.IIde;

import java.io.File;
import java.util.Map;
import java.util.function.Consumer;

public class JavaFXIde implements IIde {
    private static final String TAG = JavaFXIde.class.getName();
    SimpleThingEditor simpleThingEditor;

    public JavaFXIde(SimpleThingEditor simpleThingEditor){
        this.simpleThingEditor = simpleThingEditor;
    }

    @Override
    public void ideOpenFile(File file) {
        Executor.info(TAG, "Open file not implemented");
    }

    @Override
    public void ideOpenThing(Thing thing) {
        simpleThingEditor.openThintEditor(thing);
    }

    @Override
    public void ideOpenThingAndSelectCodeLine(Thing thing, String codeAttrName, int line) {
        simpleThingEditor.openThintEditor(thing);
    }

    @Override
    public void ideDoAction(String name, Map<String, Object> parameters) {
        Executor.info(TAG, "Do action not implemented");
    }

    @Override
    public ActionContainer getActionContainer() {
        return null;
    }

    @Override
    public ActionContext getActionContext() {
        return simpleThingEditor.actionContext;
    }

    @Override
    public Object getIDEShell() {
        return null;
    }

    @Override
    public void ideShowMessageBox(String title, String message, int style, Callback<Integer, Void> callback) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if((style & IIde.ICON_QUESTION) == IIde.ICON_QUESTION){
            alert.setAlertType(Alert.AlertType.CONFIRMATION);
        }else if((style & IIde.ICON_ERROR) == IIde.ICON_ERROR){
            alert.setAlertType(Alert.AlertType.ERROR);
        }else if((style & IIde.ICON_WARNING) == IIde.ICON_WARNING){
            alert.setAlertType(Alert.AlertType.WARNING);
        }else{
            alert.setAlertType(Alert.AlertType.INFORMATION);
        }
        alert.setTitle(title);
        alert.setHeaderText(message);

        final ButtonType ABORT = new ButtonType(UtilString.getString("lang:d=忽略&en=Ignore", getActionContext()));
        final ButtonType IGNORE = new ButtonType(UtilString.getString("lang:d=忽略&en=Ignore", getActionContext()));
        final ButtonType OPEN = new ButtonType(UtilString.getString("lang:d=打开&en=Open", getActionContext()));
        final ButtonType RETRY = new ButtonType(UtilString.getString("lang:d=重试&en=Retry", getActionContext()));
        final ButtonType SAVE = new ButtonType(UtilString.getString("lang:d=保存&en=Save", getActionContext()));
        if((style & IIde.OK) == IIde.OK) {
            alert.getButtonTypes().add(ButtonType.OK);
        }
        if((style & IIde.ABORT) == IIde.ABORT) {
            alert.getButtonTypes().add(ABORT);
        }
        if((style & IIde.CANCEL) == IIde.CANCEL) {
            alert.getButtonTypes().add(ButtonType.CANCEL);
        }if((style & IIde.IGNORE) == IIde.IGNORE) {
            alert.getButtonTypes().add(IGNORE);
        }
        if((style & IIde.NO) == IIde.NO) {
            alert.getButtonTypes().add(ButtonType.NO);
        }
        if((style & IIde.OPEN) == IIde.OPEN) {
            alert.getButtonTypes().add(OPEN);
        }
        if((style & IIde.RETRY) == IIde.RETRY) {
            alert.getButtonTypes().add(RETRY);
        }
        if((style & IIde.SAVE) == IIde.SAVE) {
            alert.getButtonTypes().add(SAVE);
        }
        if((style & IIde.YES) == IIde.YES) {
            alert.getButtonTypes().add(ButtonType.YES);
        }

        alert.showAndWait().ifPresent(new Consumer<ButtonType>() {
            @Override
            public void accept(ButtonType buttonType) {
                if(callback != null){
                    if(buttonType == ButtonType.OK){
                        callback.call(IIde.OK);
                    }else if(buttonType == ButtonType.CANCEL){
                        callback.call(IIde.CANCEL);
                    }else if(buttonType == ButtonType.NO){
                        callback.call(IIde.NO);
                    }else if(buttonType == ABORT){
                        callback.call(IIde.ABORT);
                    }else if(buttonType == IGNORE){
                        callback.call(IIde.IGNORE);
                    }else if(buttonType == OPEN){
                        callback.call(IIde.OPEN);
                    }else if(buttonType == RETRY){
                        callback.call(IIde.RETRY);
                    }else if(buttonType == SAVE){
                        callback.call(IIde.SAVE);
                    }
                }
            }
        });

    }

    @Override
    public boolean isThingExplorer() {
        return true;
    }

    @Override
    public boolean isDisposed() {
        return false;
    }
}
