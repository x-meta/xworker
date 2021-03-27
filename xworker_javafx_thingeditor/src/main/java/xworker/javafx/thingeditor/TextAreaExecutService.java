package xworker.javafx.thingeditor;

import javafx.scene.control.TextArea;
import xworker.lang.executor.ExecuteRequest;
import xworker.lang.executor.services.AbstractLogService;

import java.util.Collections;
import java.util.List;

public class TextAreaExecutService extends AbstractLogService{
    TextArea textArea;

    public TextAreaExecutService(TextArea textArea){
        this.textArea = textArea;
    }

    @Override
    public List<ExecuteRequest> getRequestUIs() {
        return Collections.emptyList();
    }

    @Override
    public Thread getThread() {
        return Thread.currentThread();
    }

    @Override
    public void log(byte level, String msg) {
        if(textArea.getText().length() > 1024 * 1024){
            textArea.replaceText(0, 10 * 1024, "");
        }
        textArea.appendText(msg + "\n");
    }
}
