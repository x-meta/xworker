package xworker.swt.html.chart;

import java.util.List;

import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;

import xworker.swt.reacts.DataReactor;
import xworker.swt.reacts.DataReactorContext;
import xworker.swt.reacts.WidgetDataReactor;

public class EChartsDataReactor extends WidgetDataReactor{
	ECharts echarts = null;
	public EChartsDataReactor(ActionContainer actions, Thing self, ActionContext actionContext) {
		super((Widget) actions.doAction("getControl", actionContext), self, actionContext);
		
		echarts = actions.getActionContext().getObject("echarts");
	}

	@Override
	protected void widgetDoOnSelected(List<Object> datas, DataReactorContext context) {
	}

	@Override
	protected void widgetDoOnUnselected(DataReactorContext context) {
	}

	@Override
	protected void widgetDoOnAdded(int index, List<Object> datas, DataReactorContext context) {
		echarts.update("records", this.datas);
	}

	@Override
	protected void widgetDoOnRemoved(List<Object> datas, DataReactorContext context) {
		echarts.update("records", this.datas);
	}

	@Override
	protected void widgetDoOnUpdated(List<Object> datas, DataReactorContext context) {
		echarts.update("records", this.datas);
	}

	@Override
	protected void widgetDoOnLoaded(List<Object> datas, DataReactorContext context) {
		echarts.update("records", datas);
	}
	
	public static DataReactor create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ActionContainer echarts = self.doAction("getEcharts", actionContext);
		if(echarts != null) {
			EChartsDataReactor dr = new EChartsDataReactor(echarts, self, actionContext);
			actionContext.g().put(self.getMetadata().getName(), dr);
			return dr;			
		}
		
		return null;
	}

}
