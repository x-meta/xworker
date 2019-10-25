package xworker.chart.jfree.dataobject.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.DefaultWindDataset;
import org.jfree.data.xy.WindDataset;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;

public class ReWindDataset implements DataObjectReloadableDataset, WindDataset{
	private Thing config;
	DefaultWindDataset dataset = new DefaultWindDataset();
	List<DatasetChangeListener> listeners = new ArrayList<DatasetChangeListener>();
	
	public ReWindDataset(Thing config){
		this.config = config;
	}
	
	@Override
	public void reload(List<DataObject> datas) {
		//先清空数据
		
		String windSeries = config.getString("windSeries");
		String windDate = config.getString("windDate");
		String windDirection = config.getString("windDirection");
		String windForce = config.getString("windForce");
		
		Map<String, List<Object[]>> items = new HashMap<String, List<Object[]>>();		
		for(DataObject r : datas){
			String name = r.getString(windSeries);
			List<Object[]> ds = items.get(name);
			if(ds == null){
				ds = new ArrayList<Object[]>();
				items.put(name, ds);
			}
			
			Object[] os = new Object[3];
			os[0] = r.getData(windDate);
			os[1] = r.getDouble(windDirection);
			os[2] = r.getDouble(windForce);
			
			ds.add(os);			
		}	
		
		String[] keys = new String[items.size()];
		items.keySet().toArray(keys);
		Object[][][] objs = new Object[items.size()][][];
		
		for(int i=0; i<keys.length; i++){
			List<Object[]> ds = items.get(keys[i]);
			objs[i] = new Object[ds.size()][];
			for(int n=0; n<ds.size(); n++){
				objs[i][n] = ds.get(n);
			}
		}
		
		dataset = new DefaultWindDataset(keys, objs);
		
		DatasetChangeEvent event  = new DatasetChangeEvent(this, dataset);
		for(DatasetChangeListener l: listeners){
			l.datasetChanged(event);
		}
	}

	@Override
	public DomainOrder getDomainOrder() {
		return dataset.getDomainOrder();
	}

	@Override
	public int getItemCount(int series) {
		return dataset.getItemCount(series);
	}

	@Override
	public Number getX(int series, int item) {
		return dataset.getX(series, item);
	}

	@Override
	public double getXValue(int series, int item) {
		return dataset.getXValue(series, item);
	}

	@Override
	public Number getY(int series, int item) {
		return dataset.getY(series, item);
	}

	@Override
	public double getYValue(int series, int item) {
		return dataset.getYValue(series, item);
	}

	@Override
	public int getSeriesCount() {
		return dataset.getSeriesCount();
	}

	@Override
	public Comparable getSeriesKey(int series) {
		return dataset.getSeriesKey(series);
	}

	@Override
	public int indexOf(Comparable arg0) {
		return dataset.indexOf(arg0);
	}

	@Override
	public void addChangeListener(DatasetChangeListener listener) {
		listeners.add(listener);
	}

	@Override
	public DatasetGroup getGroup() {
		return dataset.getGroup();
	}

	@Override
	public void removeChangeListener(DatasetChangeListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void setGroup(DatasetGroup group) {
		dataset.setGroup(group);
	}

	@Override
	public Number getWindDirection(int series, int item) {
		return dataset.getWindDirection(series, item);
	}

	@Override
	public Number getWindForce(int series, int item) {
		return dataset.getWindForce(series, item);
	}


}
