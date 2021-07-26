package xworker.javafx.thing.editor.thingmenus;

import org.xmeta.ActionContext;

public class CodeWindow {
    public static void init(ActionContext actionContext){
        javafx.scene.control.TextArea codeTextArea = actionContext.getObject("codeTextArea");
        String code = actionContext.getObject("code");
        if(code != null){
            codeTextArea.setText(code);
        }
    }

    public static void close(ActionContext actionContext){
        javafx.stage.Stage stage = actionContext.getObject("stage");
        stage.close();
    }
}
