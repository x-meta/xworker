package xworker.swt.xwidgets;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

import ognl.Ognl;
import ognl.OgnlException;
import xworker.util.UtilData;

public class ListDataViewer {
	public static void create(ActionContext actionContext) throws OgnlException, IOException{
		Thing self = (Thing) actionContext.get("self");
		
		Table table = (Table) actionContext.get("parent");
		List<Thing> columns = self.getChilds("ColumnMapping");
		for(Thing column : columns){
			TableColumn c = new TableColumn(table, SWT.None);
			c.setWidth(column.getInt("width"));
			c.setText(column.getMetadata().getLabel());
			//缓存ognl表达式
			c.setData(Ognl.parseExpression(column.getString("path")));
			
			String numberPattern = self.getStringBlankAsNull("numberPattern");
			if(numberPattern != null){
				c.setData("pattern", new DecimalFormat(numberPattern));
			}
			String datePattern = self.getStringBlankAsNull("datePattern");
			if(datePattern != null){
				c.setData("pattern", new SimpleDateFormat(datePattern));
			}
		}
				
		Thing thing = self.detach();
		thing.put("table", table);
		
		Object datas = UtilData.getData(self, "datas", actionContext);
		if(datas != null){
			thing.doAction("setDatas", actionContext, UtilMap.toMap("datas", datas));
		}
		actionContext.g().put(self.getMetadata().getName(), thing);
	}
	
	public static void setDatas(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//移除所有记录
		Table table = (Table) self.get("table");
		table.removeAll();
		
		Object datas = actionContext.get("datas");
		if(datas != null){
			Iterable<Object> iter = UtilData.getIterable(datas);
			for(Object object : iter){
				actionContext.peek().put("object", object);
				Object obj = self.doAction("filtData", actionContext);
				if(obj == null){
					continue;
				}
				
				String[] texts = new String[table.getColumnCount()];
				
				for(int i=0; i<table.getColumnCount(); i++){
					Object v = OgnlUtil.getValue(table.getColumn(i), obj);
					if(v != null){
						Format f = (Format) table.getColumn(i).getData("pattern");
						if(f != null){
							if(f instanceof SimpleDateFormat){
								texts[i] = ((SimpleDateFormat) f).format(UtilData.getDate(v, null));
							}else{
								texts[i] = ((DecimalFormat) f).format(v);
							}
						}else{
							texts[i] = v.toString();
						}
					}
				}

				TableItem item = new TableItem(table, SWT.None);
				item.setText(texts);
				item.setData(object);
			}
		}
	}
	
	public static Object filtData(ActionContext actionContext){
		return actionContext.get("object");
	}
}
