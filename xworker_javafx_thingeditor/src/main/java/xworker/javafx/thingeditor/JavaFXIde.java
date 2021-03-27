package xworker.javafx.thingeditor;

import javafx.scene.control.Alert;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.lang.actions.ActionContainer;
import xworker.lang.executor.Executor;
import xworker.util.IIde;

import java.io.File;
import java.util.Map;

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
    public void ideShowMessageBox(String title, String message, int style) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.show();
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
