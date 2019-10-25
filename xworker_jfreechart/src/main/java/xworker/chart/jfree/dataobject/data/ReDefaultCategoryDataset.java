package xworker.chart.jfree.dataobject.data;

import java.util.List;

import org.jfree.data.category.DefaultCategoryDataset;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;

public class ReDefaultCategoryDataset extends DefaultCategoryDataset implements DataObjectReloadableDataset{
	private static final long serialVersionUID = 1L;
	private Thing config;
	
	public ReDefaultCategoryDataset(Thing config) {
		super();
		
		this.config = config;
	}
	
	@Override
	public void reload(List<DataObject> datas) {
		this.clear();
			
		 //重新加载数据
	    String rowKey = config.getString("rowName");
	    String columnKey = config.getString("columnName");
	    String valueName = config.getString("valueName");
	    
	    //如果值太多显示很慢，因此按间隔选择
	    int interval = 1;
	    if(datas.size() > 100){
	        interval = datas.size() / 100;
	    }
	    int index = 0;
	    for(DataObject record : datas){
	        if(index % interval != 0){
	            index ++;
	            continue;
	        } 
	        
	        String row = "";
	        String column = "";
	        double value = 0;
	        if(rowKey != null && !"".equals(rowKey)){
	            row = record.getString(rowKey);
	        }
	        if(columnKey != null && !"".equals(columnKey)){
	            column = record.getString(columnKey);
	        }
	        if(row == null){
	            //log.info("row=null, rowKey=" + rowKey);
	            row = "";
	        }
	        if(column == null){
	            //log.info("column=null, columnKey=" + columnKey);
	            column = "";
	        }
	        
	        value = record.getDouble(valueName);
	        //log.info("row=" + row + ",column=" + column + ",value=" + value);
	        this.addValue(value, row, column);   
	    }	    
	}
}
