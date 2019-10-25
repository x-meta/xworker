package xworker.chart.jfree;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;

public class JfreeCommonActions {
	public static Object getDataset(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Object dataset = null;
		String datasetVar = self.getStringBlankAsNull("dataset");
		if(datasetVar != null  && !"".equals(datasetVar)){
		    dataset = OgnlUtil.getValue(datasetVar, actionContext);
		}

		Thing datasetThing = null;
		if(dataset == null){
			String datasetPath = self.getStringBlankAsNull("datasetPath");
			if(datasetPath != null){
				datasetThing = World.getInstance().getThing(datasetPath);
			}
		}
		if(datasetThing == null){
			datasetThing = self.getThing("Dataset@0");
			if(datasetThing != null && datasetThing.getChilds().size() > 0){
				datasetThing = datasetThing.getChilds().get(0);
			}
		}
		
	    if(datasetThing != null){
	        dataset = datasetThing.doAction("create", actionContext);
	    }
		
		return dataset;
	}
}
