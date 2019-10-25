package xworker.http.actions.jfreechart;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.chart.jfree.dataobject.DataObjectChart;
import xworker.chart.jfree.dataobject.DataObjectChartFactory;
import xworker.dataObject.DataObject;

public class JFreechartActions {
	@SuppressWarnings("unchecked")
	public static byte[] exportImage(ActionContext actionContext) throws OgnlException, IOException{
		Thing self = (Thing) actionContext.get("self");
		
		List<DataObject> doAction = (List<DataObject>) self.doAction("getDatas", actionContext);
		List<DataObject> datas = doAction;
		if(datas == null){
			throw new ActionException("datas is null, path=" + self.getMetadata().getPath());
		}
		
		JFreeChart chart = (JFreeChart) self.doAction("getJFreechart", actionContext, UtilMap.toMap("datas", datas));
		if(chart == null){
			throw new ActionException("jfreechart is null, path=" + self.getMetadata().getPath());
		}
		
		int width = self.getInt("width", 100, actionContext);
		int height = self.getInt("height", 200, actionContext);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ChartUtilities.writeChartAsPNG(bout, chart, width, height);
		
		byte[] bytes = bout.toByteArray();
		
		if(self.getBoolean("exportToServlet")){
			HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
			response.setContentType("image/x-png;image/png");
			response.setContentLength(bytes.length);
			
			response.getOutputStream().write(bytes);
		}
		
		return bytes;
	}
	
	@SuppressWarnings("unchecked")
	public static List<DataObject> getDatas(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//去变量
		String datasStr = self.getStringBlankAsNull("datas");
		if(datasStr != null){
			return (List<DataObject>) UtilData.getData(self, "datas", actionContext);
		}
		
		//通过query查询
		Thing query = null;
		if(self.getStringBlankAsNull("queryPath") != null){
			query = World.getInstance().getThing(self.getString("queryPath"));
		}else{
			query = self.getThing("Query@0");
		}
		
		if(query != null){
			return (List<DataObject>) query.getAction().run(actionContext);
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static JFreeChart getJFreechart(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		
		List<DataObject> datas = (List<DataObject>) actionContext.get("datas");
		Thing jfree = null;
		if(self.getStringBlankAsNull("jfreechartPath") != null){
			jfree = World.getInstance().getThing(self.getString("jfreechartPath"));
		}else{
			jfree = self.getThing("JFreechart@0");
		}
		
		if(jfree != null){
			try{
				Bindings bindings = actionContext.push();
				bindings.put("self", jfree);
				
				DataObjectChart chart = DataObjectChartFactory.create(actionContext, datas);
			    if(chart != null){
			    	return chart.chart;
			    }
			}finally{
				actionContext.pop();
			}
		}
		
		return null;
	}
}
