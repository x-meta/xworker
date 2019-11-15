package xworker.swt.reacts;

import java.util.ArrayList;
import java.util.List;

import xworker.dataObject.DataObject;

public class DataReactorUtils {
	public static List<Object> toObjectList(Object ...dataObjects ){
		List<Object> list = new ArrayList<Object>();
		if(dataObjects != null) {
			for(int i=0; i<dataObjects.length; i++) {
				list.add(dataObjects[i]);
			}
		}
		
		return list;
	}
	
	public static List<DataObject> toDataObjectList(List<Object> datas){
		List<DataObject> ds = new ArrayList<DataObject>();
		if(datas != null) {
			for(Object data : datas) {
				if(data instanceof DataObject) {
					ds.add((DataObject) data);
				}
			}
		}
		
		return ds;
	}
}
