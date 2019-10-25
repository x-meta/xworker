package xworker.chart.jfree.dataobject.data;

import java.util.List;

import org.jfree.data.general.WaferMapDataset;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;

public class ReWaferMapDataset extends WaferMapDataset implements DataObjectReloadableDataset{
	private static final long serialVersionUID = -8811781197469143344L;
	private Thing config;
	
	public ReWaferMapDataset(Thing config){
		super(config.getInt("maxChipX"), config.getInt("maxChipY"), config.getDouble("chipSpace"));
		
		this.config = config;
	}
	
	@Override
	public void reload(List<DataObject> datas) {
		//先清空数据
		
		String v = config.getString("waferV");
		String x = config.getString("waferX");
		String y = config.getString("waferY");
		
		for(DataObject r : datas){
			this.addValue(r.getInt(v), r.getInt(x),r.getInt(y));
		}	
	}

}
