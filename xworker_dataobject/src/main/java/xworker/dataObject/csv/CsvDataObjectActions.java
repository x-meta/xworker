package xworker.dataObject.csv;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import ognl.OgnlException;

public class CsvDataObjectActions {
	public static Object getCsvSource(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		String csvText = self.doAction("getCsvText", actionContext);
		if(csvText != null && !"".equals(csvText)){
			return csvText;
		}
		
		return self.doAction("getCsvFile", actionContext);		
	}
}
