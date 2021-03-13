package xworker.javafx.control;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.Control;
import xworker.javafx.layout.RegionActions;
import xworker.javafx.util.JavaFXUtils;

public class ControlActions {
	public static void init(Control node, Thing thing, ActionContext actionContext) {
		RegionActions.init(node, thing, actionContext);

		if(thing.valueExists("contextMenu")){
			ContextMenu contextMenu = JavaFXUtils.getObject(thing, "contextMenu", actionContext);
			if(contextMenu != null) {
				node.setContextMenu(contextMenu);
			}
		}
		if(thing.valueExists("skin")){
			Skin<?> skin = JavaFXUtils.getObject(thing, "skin", actionContext);
			if(skin != null) {
				node.setSkin(skin);
			}
		}
		if(thing.valueExists("tooltip")){
			Tooltip tooltip = JavaFXUtils.getObject(thing, "tooltip", actionContext);
			if(tooltip != null) {
				node.setTooltip(tooltip);
			}
		}
	}
}
