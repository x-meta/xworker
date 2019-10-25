package xworker.app.view.swt.widgets.chart;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilThing;

import xworker.swt.design.Designer;

public class ChartCommon {
	public static Object create(ActionContext actionContext){
		World world = World.getInstance();
		Thing self = actionContext.getObject("self");

		Thing template = world.getThing("xworker.app.view.swt.widgets.chart.ChartTemplate");

		ActionContext ac = new ActionContext();
		ac.put("parentContext", actionContext);
		Map<String, Object> data = new HashMap<String, Object>();
		data.putAll(self.getAttributes());
		data.put("chartType", self.getThingName());
		ac.put("data", data);
		Thing dataObject = UtilThing.getThingFromAttributeOrChilds(self, "dataObject", "dataObjects@0");
		Thing queryConfig = UtilThing.getThingFromAttributeOrChild(self, "queryConfig", "queryConfig@0");
		if(dataObject != null){
		    ac.put("dataObject", dataObject.getMetadata().getPath());
		}
		if(queryConfig != null){
		    ac.put("queryConfig", queryConfig.getMetadata().getPath());
		}

		Thing chartThing = ((Thing) template.doAction("process", ac)).getChilds().get(0);
		if(chartThing != null){
		     ac.put("parent", actionContext.get("parent"));
		     Composite composite = null;
		     Designer.pushCreator(self);
		     try{
		    	 composite = (Composite) chartThing.doAction("create", ac);
		     }finally{
		    	 Designer.popCreator();
		     }
		     
		     actionContext.peek().put("parent", composite);
		     for(Thing child : self.getChilds()){
		         child.doAction("create", actionContext);
		     }
		     
		     Thing thing = self.detach();
		     thing.setData("self", self);
		     thing.setData("actionContext", ac);
		     thing.setData("composite", composite);
		     
		     actionContext.getScope(0).put(self.getMetadata().getName(), thing);
		     Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		     
		     return composite;
		}else{
		     return null;
		}
	}
	
	public static Object getDataObject(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		return UtilThing.getThingFromAttributeOrChilds(self, "dataObject", "dataObjects@0");
	}
	
	public static void parentSelected(ActionContext actionContext){
		//World world = World.getInstance();
		Thing self = actionContext.getObject("self");
		
		ActionContext ac = (ActionContext) self.getData("actionContext");

		self = (Thing) self.getData("self");
		if(self.getBoolean("useParentQueryValues")){
		    ac.g().put("queryValues", actionContext.get("queryValues"));		    
		}else{
		    String dataToQueryValues = self.getStringBlankAsNull("dataToQueryValues");
		    Map<String, Object> data = actionContext.getObject("data");
		    ac.g().put("queryValues", convertValues(dataToQueryValues, data));
		}

		//println("queryValues=" + ac.g().get("queryValues"));
		((Thing) ac.get("dataStore")).doAction("load", ac, "params", ac.g().get("queryValues"));
	}
	
	public static Object convertValues(String valuesConfig, Map<String, Object> data){
		Map<String, Object> values = new HashMap<String, Object>();   
	    if(valuesConfig != null && data != null){
	        for(String qs : valuesConfig.split("[,]")){
	            String[] q = qs.split("[:]");
	            if(q.length == 2){
	                 values.put(q[0].trim(), data.get(q[1].trim()));
	            }
	        }
	    }
	    //println(valuesConfig + "=" + values);
	    return values;
	}
}
