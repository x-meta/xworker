package xworker.ai.neuroph.data;

import org.neuroph.core.data.DataSet;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;

public class FileDataSet {
	public static DataSet create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		String filePath = (String) self.doAction("getFilePath", actionContext);
		
		int inputsCount = self.getInt("inputsCount", 0, actionContext);
		int outputsCount = self.getInt("outputsCount", 0, actionContext);
		String d = self.getString("delimiter");
		String delimiter = self.getString("delimiter", "\t", actionContext);
		if("\\u".equals(d)){
			delimiter = " ";
		}else if("\\t".equals(d)){
			delimiter = "\t";
		}
		DataSet dataSet = DataSet.createFromFile(filePath, inputsCount, outputsCount, delimiter, false);
		
		Thing normalizer = self.getThing("Normalizer@0");
		if(normalizer != null){
			return (DataSet) normalizer.doAction("normalize", actionContext, UtilMap.toMap("dataSet", dataSet));
		}else{
			return dataSet;
		}
	}
	
	public static String getFilePath(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		return self.getString("filePath", null, actionContext);
	}
}
