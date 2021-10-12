package xworker.workbench;

import org.xmeta.Thing;

import java.util.Map;

public abstract class EditorParams<T> implements Comparable<EditorParams<Object>>{
    Thing editor;
    T content;
    String id;

    @Override
    public int compareTo(EditorParams<Object> o) {
        if(getPriority() < o.getPriority()){
            return -1;
        }else if(getPriority() == o.getPriority()){
            return 0;
        }else {
            return 1;
        }
    }

    public EditorParams(Thing editor, String id, T content){
        this.editor = editor;
        this.id = id;
        this.content = content;
    }

    public abstract Map<String, Object> getParams();

    public Thing getEditor() {
        return editor;
    }

    public void setEditor(Thing editor) {
        this.editor = editor;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPriority(){
        return 0;
    }
}
