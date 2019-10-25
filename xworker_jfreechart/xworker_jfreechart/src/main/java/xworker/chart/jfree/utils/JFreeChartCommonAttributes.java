package xworker.chart.jfree.utils;

import java.io.IOException;
import java.util.Locale;

import org.jfree.chart.plot.PlotOrientation;
import org.jfree.util.TableOrder;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.StringUtils;

public class JFreeChartCommonAttributes {
	public String title;
	public String categoryAxisLabel;
	public String valueAxisLabel;
	public PlotOrientation orientation;
	public boolean legend;
	public boolean tooltips;
	public boolean urls;
	public Locale locale = Locale.getDefault();	
	public TableOrder order;
	public boolean dateAxis;
	
	public JFreeChartCommonAttributes(Thing self, ActionContext actionContext) throws IOException{
		title = StringUtils.getString(self, "title", actionContext);
		categoryAxisLabel = StringUtils.getString(self, "categoryAxisLabel", actionContext);
		valueAxisLabel = StringUtils.getString(self, "valueAxisLabel", actionContext);
		if("VERTICAL".equals(self.getString("orientation"))){
			orientation = PlotOrientation.VERTICAL;
		}else{
			orientation = PlotOrientation.HORIZONTAL;
		}
		legend = self.getBoolean("legend");
		tooltips = self.getBoolean("tooltips");
		urls = self.getBoolean("urls");
		if("BY_COLUMN".equals(self.getString("order"))){
			order = TableOrder.BY_COLUMN;
		}else{
			order = TableOrder.BY_ROW;
		}
		dateAxis = self.getBoolean("dateAxis");
		
	}
}
