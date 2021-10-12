package xworker.swt.app.editors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolItem;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilFile;
import org.xmeta.util.UtilString;

import xworker.swt.app.IEditor;
import xworker.swt.util.SwtTextUtils;
import xworker.swt.util.SwtUtils;
import xworker.swt.xworker.Colorer;
import xworker.util.UtilFileIcon;
import xworker.workbench.EditorParams;

@ActionClass(creator="createInstance")
public class FileEditor {

    public static EditorParams<Object> createParams(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        Object content = actionContext.getObject("content");
        if(content instanceof File){
            File file = (File) content;
            if(file.isFile()){
                String subfix = file.getName();
                int index = subfix.lastIndexOf(".");
                if(index != -1) {
                    subfix = subfix.substring(index + 1, subfix.length()).toLowerCase();
                }else {
                    return new EditorParams<Object>(World.getInstance().getThing("xworker.swt.app.editors.SystemFileEditor"),
                            "File:" + file.getAbsolutePath(), file) {
                        @Override
                        public Map<String, Object> getParams() {
                            Map<String, Object> params = new HashMap<>();
                            params.put("fileName", file.getAbsolutePath());
                            return params;
                        }
                    };
                }

                //是否是文本文件
                boolean isText = false;
                if("txt".equals(subfix)) {
                    isText = true;

                }else {
                    for(String type : Colorer.getSupportCodeTypes()){
                        if(type.toLowerCase().equals(subfix)) {
                            isText = true;
                            break;
                        }
                    }
                }

                if(isText) {
                    return new EditorParams<Object>(World.getInstance().getThing("xworker.swt.app.editors.FileTextEditor"),
                            "File:" + file.getAbsolutePath(), file) {
                        @Override
                        public Map<String, Object> getParams() {
                            Map<String, Object> params = new HashMap<>();
                            params.put("file", file);
                            return params;
                        }
                    };
                }

                //是否是事物模型
                if(World.getInstance().isThingFile(file.getName())) {
                    String path = UtilFile.getFilePath(file.getAbsolutePath());
                    Thing thing = World.getInstance().getThing(path);
                    if(thing != null) {
                        return new EditorParams<Object>(World.getInstance().getThing("xworker.swt.app.editors.ThingEditor"),
                                "thing:" + file.getAbsolutePath(), file) {
                            @Override
                            public Map<String, Object> getParams() {
                                Map<String, Object> params = new HashMap<>();
                                params.put("thing", path);
                                return params;
                            }
                        };
                    }
                }

                //默认使用系统编辑器打开
                return new EditorParams<Object>(World.getInstance().getThing("xworker.swt.app.editors.SystemFileEditor"),
                        "File:" + file.getAbsolutePath(), file) {
                    @Override
                    public Map<String, Object> getParams() {
                        Map<String, Object> params = new HashMap<>();
                        params.put("fileName", file.getAbsolutePath());
                        return params;
                    }
                };
            }
        }
        return null;
    }

	public static Map<String, Object> createDataParams(ActionContext actionContext) throws IOException{
		Object data = actionContext.get("data");
		if(data instanceof File) {
			Map<String, Object> params = new HashMap<>();
			File file = (File) data;
			if(file.isDirectory()) {
				return null;
			}else {
				String subfix = file.getName();
				int index = subfix.lastIndexOf(".");
				if(index != -1) {
					subfix = subfix.substring(index + 1, subfix.length()).toLowerCase();
				}else {
					params.put(IEditor.EDITOR_THING, World.getInstance().getThing("xworker.swt.app.editors.SystemFileEditor"));
					params.put(IEditor.EDITOR_ID, "File:" + file.getCanonicalPath());
					params.put("fileName", file.getAbsolutePath());
					return params;
				}
				
				//是否是文本文件
				boolean isText = false;
				if("txt".equals(subfix)) {
					isText = true;
					
				}else {					
					for(String type : Colorer.getSupportCodeTypes()){
					    if(type.toLowerCase().equals(subfix)) {
					    	isText = true;
					    	break;
					    }
					}
				}
				
				if(isText) {
					params.put(IEditor.EDITOR_THING, World.getInstance().getThing("xworker.swt.app.editors.FileTextEditor"));
					params.put("file", file);
					params.put(IEditor.EDITOR_ID, "File:" + file.getCanonicalPath());
					return params;
				}
				
				//是否是事物模型
				if(World.getInstance().isThingFile(file.getName())) {
					String path = UtilFile.getFilePath(file.getAbsolutePath());
					Thing thing = World.getInstance().getThing(path);
					if(thing != null) {
						params.put(IEditor.EDITOR_THING, World.getInstance().getThing("xworker.swt.app.editors.ThingEditor"));
						params.put("thing", thing);
						params.put(IEditor.EDITOR_ID, "Thing:" + path);
						return params;
					}
				}
				
				//默认使用系统编辑器打开
				params.put(IEditor.EDITOR_THING, World.getInstance().getThing("xworker.swt.app.editors.SystemFileEditor"));
				params.put(IEditor.EDITOR_ID, "File:" + file.getCanonicalPath());
				params.put("fileName", file.getAbsolutePath());
				return params;
			}
		}else {
			return null;
		}
	}
	

    public void saveFile() throws IOException{
        //xworker.swt.app.editors.FileTextEditor/@EditorComposite/@codeEditor1/@listeners/@okButtonSelection/@saveFile
    	File file = actionContext.getObject("file");
    	FileOutputStream fout = new FileOutputStream(file);
        try{
            fout.write(SwtTextUtils.getText(codeEditor).getBytes());
            //tab.setText(file.getName());
            actionContext.g().put("dirty", false);
            editorThing.doAction("editorChanged", actionContext);
        }finally{
            fout.close();
        }
    }
    
    public void codeModifyed(){
        //xworker.swt.app.editors.FileTextEditor/@EditorComposite/@codeEditor1/@listeners/@codeEditorModify/@codeModifyed
        //noChangeEvent是setContent时设置的，避免设置时就变成脏数据
        if(!UtilData.isTrue(actionContext.get("noChangeEvent"))){
            actionContext.g().put("dirty", true);
            
            //是编辑器事物，方法继承于Editor事物
            editorThing.doAction("editorChanged", actionContext);
            
            //触发编辑器的修改事件
            editor.fireStateChanged();
        }
    }
    
    public void caretMoved(){
        //xworker.swt.app.editors.FileTextEditor/@EditorComposite/@codeEditor1/@CaretListener/@actions/@caretMoved
        String lineCaret = "0:0";
        Point point = SwtTextUtils.getCaretRowColOffset(codeEditor);
        lineCaret = "" + point.x + " : " + point.y;
        
        if(checkWidget("statusLabel", actionContext)){
        	Label statusLabel = actionContext.getObject("statusLabel");
            statusLabel.setText(lineCaret);
        }
        
        if(checkWidget("caretItem", actionContext)){
            //println "xxxxx";
            caretItem.setText(lineCaret);  
            
            String writableStr = UtilString.getString("lang:d=可写&en=Writable", actionContext);
            if(file.canWrite() == false){
                writableStr = UtilString.getString("lang:d=只读&en=ReadOnley", actionContext);
            }  
            writableItem.setText(writableStr);
        }
    }
    
    //检查组件是否有效
    public boolean checkWidget(String name, ActionContext actionContext){
    	ToolItem obj = actionContext.getObject(name);
        if(obj != null && obj.isDisposed() == false){
            return true;
        }else{
            return false;
        }
    }
    
    public Object setContent() throws IOException{
        //xworker.swt.app.editors.FileTextEditor/@ActionContainer/@setContent
        Map<String, Object> params = actionContext.getObject("params");
        Action noParams = actionContext.getObject("noParams");
        Action noFile = actionContext.getObject("noFile");
        
        //判断参数是否存在
        if(params == null){    
            noParams.run(actionContext);
            return null;
        }
        
        //判断文件是否可编辑
        Object fileObj = params.get("file");
        File file  = null;
        if(fileObj instanceof String){
            file = new File((String) fileObj);
        }else {
        	file = (File) fileObj;
        }
        if(file == null || !(file instanceof File) || !file.isFile()){
            noFile.run(actionContext);
            return null;
        }
        
        actionContext.g().put("file", fileObj);
        
        FileInputStream fin = new FileInputStream(file);
        actionContext.g().put("noChangeEvent", true);
        try{
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            
            SwtTextUtils.chooseFileType(codeEditor, codeEditor.getData("textColorer"), file.getName());
            
            String str = new String(bytes);    
            SwtTextUtils.setText(codeEditor, str);
            
            actionContext.g().put("dirty", false);
            editorThing.doAction("editorChanged", actionContext, "dirty", false);
        }finally{
            fin.close();
            actionContext.g().put("noChangeEvent", false);
        }
        return null;
    }
    
    public Object isSameContent(){
        //xworker.swt.app.editors.FileTextEditor/@ActionContainer/@isSameContent
    	Map<String, Object> params = actionContext.getObject("params");
    	
        if(actionContext.get("params") == null){
            return false;
        }
        
        if(actionContext.get("file") == null){
            return false;
        }    
        
        if(file.equals(params.get("file"))){
            return true;
        }else{
            return false;
        }
    }
    public Object getSimpleTitle(){
        //xworker.swt.app.editors.FileTextEditor/@ActionContainer/@getSimpleTitle
        if(actionContext.get("file") != null){
            return file.getName();
        }else{
            return "";
        }
    }
    public Object getTitle(){
        //xworker.swt.app.editors.FileTextEditor/@ActionContainer/@getTitle
        if(actionContext.get("file") != null){
            return file.getAbsolutePath();
        }else{
            return "";
        }
    }
    public void doDispose(){
        //xworker.swt.app.editors.FileTextEditor/@ActionContainer/@doDispose
        if(editorComposite.isDisposed() == false){
            editorComposite.dispose();
        }
    }
    
    public void setFile() throws IOException{
        //xworker.swt.app.editors.FileTextEditor/@ActionContainer/@setFile
        actionContext.getScope(0).put("file", file);
        
        FileInputStream fin = new FileInputStream(file);
        byte[] bytes = new byte[fin.available()];
        fin.read(bytes);
        fin.close();
        
        SwtTextUtils.chooseFileType(codeEditor, codeEditor.getData("textColorer"), file.getName());
        
        String str = new String(bytes);
        SwtTextUtils.setText(codeEditor, str);
        CTabItem tab = actionContext.getObject("tab");
        if(tab != null) {
        	tab.setText(file.getName());
        }
    }
    
    public void attachTextEditor(){
        //xworker.swt.app.editors.FileTextEditor/@ActionContainer/@setFile/@ActionDefined/@attachTextEditor
        SwtTextUtils.chooseFileType(codeEditor, codeEditor.getData("textColorer"), file.getName());
        SwtTextUtils.attachTextEditor(codeEditor);        
    }
    
    public Object getIcon() throws IOException{
        //xworker.swt.app.editors.FileTextEditor/@ActionContainer/@getIcon
        if(actionContext.get("file") != null){
        	File file = actionContext.getObject("file");
            String iconStr = UtilFileIcon.getFileIcon(file, false);
            if(iconStr != null){
                return SwtUtils.createImage(editorComposite, iconStr, actionContext);
            }
        }
        
        return null;
    }

    public static FileEditor createInstance(ActionContext actionContext){
        //return new MyClass();    
        String key = FileEditor.class.getName();
        FileEditor obj = actionContext.getObject(key);
        if(obj == null){
            obj = new FileEditor();
            actionContext.g().put(key, obj);
        }
        
        return obj;
    }    
        
    @ActionField
    public org.eclipse.swt.widgets.Shell shell;
    
    @ActionField
    public org.xmeta.util.ActionContainer actions;
        
    @ActionField
    public Control codeEditor;
        
    @ActionField
    public org.xmeta.World world;
    
    @ActionField
    ActionContext actionContext;
    
    @ActionField
    Thing editorThing;
    
    @ActionField
    IEditor editor;
    
    @ActionField
    Composite editorComposite;
    
    @ActionField
    File file;
    
    @ActionField
    public org.eclipse.swt.widgets.ToolItem caretItem;
        
    @ActionField
    public org.eclipse.swt.widgets.ToolItem writableItem;
}
