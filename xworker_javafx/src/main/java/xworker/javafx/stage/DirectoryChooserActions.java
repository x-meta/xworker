package xworker.javafx.stage;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import java.io.File;

public class DirectoryChooserActions {
    public static Object run(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        String title = self.doAction("getTitle", actionContext);
        File initialDirectory = self.doAction("getInitialDirectory", actionContext);

        DirectoryChooser fc = new DirectoryChooser();
        if(title != null){
            fc.setTitle(title);
        }
        if(initialDirectory != null){
            fc.setInitialDirectory(initialDirectory);
        }
        Window ownerWindow = self.doAction("getOwnerWindow", actionContext);
        Object result = fc.showDialog(ownerWindow);

        self.doAction("onResult", actionContext, "result", result);
        return result;
    }
}
