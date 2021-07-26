package xworker.ide.index.entities;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectEntity;
import xworker.dataObject.query.Condition;

/**
 * <b>语言</b><p/>
 * 
 */
public class Languages extends DataObjectEntity<Languages> {
    public Languages(){
        super(new DataObject("xworker.ide.db.dbindex.dataObject.Languages"));
    }
      
    public void setId(long value){
        this.put("id", value);
    }
    
    public long getId(){
        return this.getLong("id");
    }
    public void setThing(String value){
        this.put("thing", value);
    }
    
    public String getThing(){
        return this.getString("thing");
    }
    public void setAttribute(String value){
        this.put("attribute", value);
    }
    
    public String getAttribute(){
        return this.getString("attribute");
    }
    public void setLang(String value){
        this.put("lang", value);
    }
    
    public String getLang(){
        return this.getString("lang");
    }
    public void setLastmodify(long value){
        this.put("lastmodify", value);
    }
    
    public long getLastmodify(){
        return this.getLong("lastmodify");
    }
    
    @Override
    public Languages createInstance() {
        return new Languages();
    }
   
    public void setContent(String value){
        this.put("content", value);
    }
    
    public String getContent(){
        return this.getString("content");
    }
    
    public static Languages getLanguage(Thing thing, String attribute, String lang) {
    	Condition condition = new Condition();
    	condition.eq("thing", thing.getMetadata().getPath());
    	condition.eq("attribute", attribute);
    	condition.eq("lang", lang);
    	
    	Languages l = new Languages();
    	return l.load(new ActionContext(), condition);    	
    }
    
    public static int createBatch(List<Languages> datas){
	       Thing dataObjectThing = World.getInstance().getThing("xworker.ide.db.dbindex.dataObject.Languages");
        List<DataObject> dataObjects = new ArrayList<DataObject>();
        for(Languages data : datas) {
        	   if(data.getDataObject() != null) {
        		   dataObjects.add(data.getDataObject());
            }
        }
        Integer count = dataObjectThing.doAction("createBatch", new ActionContext(), "datas", dataObjects);
        if(count == null){
            return 0;
        }else{
            return count;
        }
    }
}