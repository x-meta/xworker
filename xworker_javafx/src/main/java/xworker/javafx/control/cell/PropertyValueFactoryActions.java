package xworker.javafx.control.cell;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public class PropertyValueFactoryActions {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static PropertyValueFactory<Object, Object> create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Object parent = actionContext.getObject("TableColumn");
		
		String key = self.doAction("getProperty", actionContext);
		PropertyValueFactory<Object, Object> mf = new PropertyValueFactory<Object, Object>(key);
		actionContext.g().put(self.getMetadata().getName(), mf);
		
		if(parent instanceof TableColumn) {
			((TableColumn) parent).setCellValueFactory(mf);
		}
		return mf;
	}
}
