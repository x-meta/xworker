package xworker.swt.app.editors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.UtilMap;

import xworker.content.ContentHandler;
import xworker.dataObject.DataObject;
import xworker.lang.executor.Executor;
import xworker.swt.app.IEditor;
import xworker.swt.util.SwtUtils;
import xworker.swt.xworker.content.SwtQuickContentHandler;
import xworker.workbench.EditorParams;

@ActionClass(creator="createInstance")
public class JdbcExplorer {
	private static final String TAG = JdbcExplorer.class.getName();


    public static EditorParams<Object> createParams(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Object content = actionContext.getObject("content");
        Object session = null;
        String path = null;
        if(content instanceof Thing){
            Thing thing = (Thing) content;
            if(thing.isThing("xworker.db.jdbc.DataSource")){
                session = thing;
                path = thing.getMetadata().getPath();
            }
        }
        if(session != null){
            return new EditorParams<Object>(self, "jdbcexplorer:" + path, session) {
                @Override
                public Map<String, Object> getParams() {
                    Map<String, Object> params = new HashMap<>();
                    params.put("dataSource", this.getContent());

                    return params;
                }
            };
        }

        return null;
    }

    public Object createDataParams(){
        //xworker.swt.app.editors.JdbcExplorer/@actions1/@createDataParams
        Object data = actionContext.getObject("data");
        
        if(data instanceof Thing){
        	Thing thing = (Thing) data;
           if(thing.isThing("xworker.db.jdbc.DataSource")){
               Map<String, Object> params = UtilMap.toMap("dataSource", data);
               params.put(IEditor.EDITOR_THING, world.getThing("xworker.swt.app.editors.JdbcExplorer"));
               params.put(IEditor.EDITOR_ID,  "DataSource:" + thing.getMetadata().getPath());
               return params;
           }
        }
        
        return null;
    }
    
    public void defaultSelected(){
        //xworker.swt.app.editors.JdbcExplorer/@EditorComposite/@mainSashForm/@dataObjectsComposite/@dataObjects/@actions/@defaultSelected
    	
        if(actionContext.get("thing") != null){
            actions.doAction("openComposite", actionContext, "composite", actionContext.get("thing"));
        }
    }
    
    public Object getTabItemKey(){
        //xworker.swt.app.editors.JdbcExplorer/@EditorComposite/@mainSashForm/@actions/@openComposite/@actions/@getTabItemKey
    	Thing composite = actionContext.getObject("composite");
        return composite.getMetadata().getPath();
    }
    
    public Object getTabItemText(){
        //xworker.swt.app.editors.JdbcExplorer/@EditorComposite/@mainSashForm/@actions/@openComposite/@actions/@getTabItemText
    	Thing composite = actionContext.getObject("composite");
        return composite.getMetadata().getLabel();
    }
    
    public Object getTabContentKey(){
        //xworker.swt.app.editors.JdbcExplorer/@EditorComposite/@mainSashForm/@actions/@openComposite/@actions/@getTabContentKey
    	Thing composite = actionContext.getObject("composite");
        return composite.getMetadata().getPath();
    }


    public void onOutlineCreated(){
        ActionContainer actions = actionContext.getObject("tools");
        Thing dataSource = actionContext.getObject("dataSource");
        Browser browser = actionContext.getObject("browser");
        if(browser != null && !browser.isDisposed()){
            if(dataSource != null){
                SwtUtils.setThingDesc(dataSource, browser);
            }

            SwtQuickContentHandler contentHandler = actions.doAction("getContentHandler", actionContext);
            if(contentHandler != null){
                contentHandler.setBrowser(browser);
            }
        }
    }
    
    public Object setContent(){
        //xworker.swt.app.editors.JdbcExplorer/@actions/@setContent
        Map<String, Object> params = actionContext.getObject("params");
        //获取数据源
        Object dataSource = params.get("dataSource");
        if(dataSource instanceof String){
            dataSource = world.getThing((String) dataSource);
        }
        if(dataSource == null){
            Executor.warn(TAG, "JdbcExplorer: dataSource is null");
            return null;
        }
        
        Map<String, Object> dbs = UtilMap.toMap(
            "oracle","xworker.app.db.indexs.ToolsOralceIndex",
            "derby","xworker.app.db.indexs.ToolsDerbyIndex",
            "mysql","xworker.app.db.indexs.ToolsMysqlIndex",
            "sqlserver","xworker.app.db.indexs.ToolsSqlserverIndex",
            "db2","xworker.app.db.indexs.ToolsDB2Index",
            "sqlite","xworker.app.db.indexs.ToolsSqliteIndex"
        );
            
        actionContext.g().put("dataSource", dataSource);
            
        //设置注册管理器
        Thing dataSourceThing = (Thing) dataSource;
        String exts = "xworker.app.db.manager.DbToolsComposites," + dataSourceThing.getMetadata().getPath();
        String dbType = dataSourceThing.getStringBlankAsNull("dbType");
        if(dbType != null){
            dbType = dbType.toLowerCase();
            for(String key : dbs.keySet()){
                if(dbType.indexOf(key) != -1){
                    exts = exts + "," + dbs.get(key);
                    break;
                }
            }
        }
        String toolsRegistThing = dataSourceThing.getStringBlankAsNull("toolsRegistThing");
        if(toolsRegistThing != null){
            exts = exts + "," + toolsRegistThing;
        }
        Thing rthing = new Thing();        
        rthing.set("extends", exts);
        
        ActionContainer tools = actionContext.getObject("tools");
        tools.doAction("init", actionContext, "thing", rthing, "type", "command");
        tools.doAction("refresh", actionContext);

        onOutlineCreated();
        return null;
    }
    
    public Object isSameContent(){
        //xworker.swt.app.editors.JdbcExplorer/@actions/@isSameContent
    	Map<String, Object> params = actionContext.getObject("params");
        Object dataSource = params.get("dataSource");
        if(dataSource instanceof String){
            dataSource = world.getThing((String) dataSource);
        }
        
        return dataSource == actionContext.get("dataSource");
    }
    
    public Object getSimpleTitle(){
        //xworker.swt.app.editors.JdbcExplorer/@actions/@getSimpleTitle
        if(actionContext.get("dataSource") != null){
        	Thing dataSource = actionContext.getObject("dataSource");
            return dataSource.getMetadata().getLabel();
        }else{
            return "No DataSource";
        }
    }
    
    public Object getTitle(){
        //xworker.swt.app.editors.JdbcExplorer/@actions/@getTitle
        if(actionContext.get("dataSource") != null){
        	Thing dataSource = actionContext.getObject("dataSource");
            return dataSource.getMetadata().getPath();
        }else{
            return "DataSource not setted!";
        }
    }
    

    public static JdbcExplorer createInstance(ActionContext actionContext){
        //return new MyClass();    
        String key = JdbcExplorer.class.getName();
        JdbcExplorer obj = actionContext.getObject(key);
        if(obj == null){
            obj = new JdbcExplorer();
            actionContext.g().put(key, obj);
        }
        
        return obj;
    }    
        
    @ActionField
    public ActionContext actionContext;
    
    public World world = World.getInstance();
    
    @ActionField
    public org.xmeta.util.ActionContainer actions;
}
