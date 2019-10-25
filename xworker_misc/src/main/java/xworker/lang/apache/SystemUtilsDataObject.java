package xworker.lang.apache;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;

public class SystemUtilsDataObject {
	public static List<DataObject> doQuery(ActionContext actionContext) throws IllegalArgumentException, IllegalAccessException{
		List<DataObject> datas = new ArrayList<DataObject>();
		Field[] fields = SystemUtils.class.getFields();
		for(Field field : fields) {
			int mod = field.getModifiers();
			if(Modifier.isStatic(mod) && Modifier.isPublic(mod)) {
				DataObject data = new DataObject("xworker.org.apache.commons.lang3.SystemUtilsDataObject");
				data.put("name", field.getName());
				data.put("value", field.get(null));
				datas.add(data);
			}			
		}
		
		return datas;
	}
	
	public static Object run(ActionContext actionContext) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Thing self = actionContext.getObject("self");
		String fieldName = self.doAction("getField", actionContext);
		
		Field field = SystemUtils.class.getDeclaredField(fieldName);
		return field.get(null);
	}
}
