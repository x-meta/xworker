package xworker.javafx.control.cell;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.MapValueFactory;

public class MapValueFactoryActions {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static MapValueFactory<Object> create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Object parent = actionContext.getObject("TableColumn");
		
		Object key = self.doAction("getKey", actionContext);
		MapValueFactory<Object> mf = new MapValueFactory<Object>(key);
		actionContext.g().put(self.getMetadata().getName(), mf);
		
		if(parent instanceof TableColumn) {
			((TableColumn) parent).setCellValueFactory(mf);
		}
		return mf;
	}
}
