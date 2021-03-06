package xworker.javafx.scene.web;

import javafx.scene.web.PopupFeatures;
import javafx.scene.web.PromptData;
import javafx.scene.web.WebEngine;
import javafx.util.Callback;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

import java.io.File;

public class WebEngineActions {
    public static void init(WebEngine node, Thing thing, ActionContext actionContext){
        if(thing.valueExists("confirmHandler")){
            Callback<String,Boolean> confirmHandler = JavaFXUtils.getObject(thing, "confirmHandler", actionContext);
            if(confirmHandler != null) {
                node.setConfirmHandler(confirmHandler);
            }
        }
        if(thing.valueExists("createPopupHandler")){
            Callback<PopupFeatures,WebEngine> createPopupHandler = JavaFXUtils.getObject(thing, "createPopupHandler", actionContext);
            if(createPopupHandler != null) {
                node.setCreatePopupHandler(createPopupHandler);
            }
        }
        if(thing.valueExists("javaScriptEnabled")){
            node.setJavaScriptEnabled(thing.getBoolean("javaScriptEnabled"));
        }
        if(thing.valueExists("promptHandler")){
            Callback<PromptData,String> promptHandler = JavaFXUtils.getObject(thing, "promptHandler", actionContext);
            if(promptHandler != null){
                node.setPromptHandler(promptHandler);
            }
        }
        if(thing.valueExists("userAgent")){
            node.setUserAgent(thing.getString("userAgent"));
        }
        if(thing.valueExists("userDataDirectory")){
            File userDataDirectory = thing.doAction("getUserDataDirectory", actionContext);
            if(userDataDirectory != null) {
                node.setUserDataDirectory(userDataDirectory);
            }
        }
        if(thing.valueExists("userStyleSheetLocation")){
            node.setUserStyleSheetLocation(thing.getString("userStyleSheetLocation"));
        }
    }
}
