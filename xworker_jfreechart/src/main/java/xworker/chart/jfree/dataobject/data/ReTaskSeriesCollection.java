package xworker.chart.jfree.dataobject.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;

public class ReTaskSeriesCollection extends TaskSeriesCollection implements DataObjectReloadableDataset{
	private static final long serialVersionUID = -8811781197469143344L;
	private Thing config;
	
	public ReTaskSeriesCollection(Thing config){
		super();
		
		this.config = config;
	}
	
	@Override
	public void reload(List<DataObject> datas) {
		this.removeAll();
		
		String seriesName = config.getString("taskSeriesName");
		String taskDescription = config.getString("taskDescription");
		String taskStart = config.getString("taskStart");
		String taskEnd = config.getString("taskEnd");
		
		Map<String, TaskSeries> context = new HashMap<String, TaskSeries>();
		for(DataObject r : datas){
			String name = r.getString(seriesName);
			TaskSeries s = context.get(name);
			if(s == null){
				s = new TaskSeries(name);
				this.add(s);
				context.put(name, s);
			}
			
			s.add(new Task(r.getString(taskDescription), r.getDate(taskStart), r.getDate(taskEnd)));
		}	
	}
}
