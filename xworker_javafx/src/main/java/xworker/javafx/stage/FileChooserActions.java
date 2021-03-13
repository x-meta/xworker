package xworker.javafx.stage;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import java.io.File;

public class FileChooserActions {
    public static Object run(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        String title = self.doAction("getTitle", actionContext);
        String type = self.doAction("getType", actionContext);
        File initialDirectory = self.doAction("getInitialDirectory", actionContext);
        String initialFileName = self.doAction("getInitialFileName", actionContext);
        String selectedExtensionFilter = self.doAction("getSelectedExtensionFilter", actionContext);

        FileChooser fc = new FileChooser();
        if(title != null){
            fc.setTitle(title);
        }
        if(initialDirectory != null){
            fc.setInitialDirectory(initialDirectory);
        }
        if(initialFileName != null){
            fc.setInitialFileName(initialFileName);
        }
        if(selectedExtensionFilter != null){
            for(String line : selectedExtensionFilter.split("[\n]")){
                String ls[] = line.split("[,]");
                if(ls.length > 1){
                    String[] fs = new String[ls.length - 1];
                    System.arraycopy(ls, 1, fs, 0, fs.length);
                    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(ls[0], fs));
                }
            }
        }

        Window ownerWindow = self.doAction("getOwnerWindow", actionContext);
        Object result = null;
        if("Open".equals(type)){
            result = fc.showOpenDialog(ownerWindow);
        }else if("OpenMultiple".equals(type)){
            result = fc.showOpenMultipleDialog(ownerWindow);
        }else{
            result = fc.showSaveDialog(ownerWindow);
        }

        self.doAction("onResult", actionContext, "result", result);
        return result;
    }
}
