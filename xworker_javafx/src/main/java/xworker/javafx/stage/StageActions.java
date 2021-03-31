package xworker.javafx.stage;

import javafx.stage.Modality;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.application.Platform;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import xworker.javafx.application.ThingApplication;
import xworker.javafx.util.JavaFXUtils;

public class StageActions {
	public static void run(final ActionContext actionContext){
		final Thing self = actionContext.getObject("self");

		ThingApplication.checkStart();

		Platform.runLater(new Runnable() {
			public void run() {
				Stage stage = create(self, actionContext);
				stage.show();
			}
		});
	}

	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Stage stage = create(self, actionContext);
		return stage;
	}
	
	public static Stage create(Thing self, ActionContext actionContext) {
		String style = self.getStringBlankAsNull("style");
		StageStyle sstyle = null;
		if(style != null) {
			if("DECORATED".equals(style)) {
				sstyle = StageStyle.DECORATED;
			}else if("TRANSPARENT".equals(style)) {
				sstyle = StageStyle.TRANSPARENT;
			}else if("UNDECORATED".equals(style)) {
				sstyle = StageStyle.UNDECORATED;
			}else if("UNIFIED".equals(style)) {
				sstyle = StageStyle.UNIFIED;
			}else if("UTILITY".equals(style)) {
				sstyle = StageStyle.UTILITY;
			}
		}
		Stage stage = sstyle == null ? new Stage() : new Stage(sstyle);
		init(stage, self, actionContext);
		
		actionContext.g().put(self.getMetadata().getName(), stage);
		actionContext.peek().put("parent", stage);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		return stage;
	}

	public static void init(Stage stage, Thing thing, ActionContext actionContext) {
		if(thing.get("alwaysOnTop") != null) {
			stage.setAlwaysOnTop(thing.getBoolean("alwaysOnTop"));
		}

		String fullScreenExitHint = thing.getStringBlankAsNull("fullScreenExitHint");
		if(fullScreenExitHint != null) {
			stage.setFullScreenExitHint(fullScreenExitHint);
		}
		
		String fullScreenExitKey = thing.getStringBlankAsNull("fullScreenExitKey");
		if(fullScreenExitKey != null) {
			stage.setFullScreenExitKeyCombination(KeyCombination.valueOf(fullScreenExitKey));
		}
		
		if(thing.getStringBlankAsNull("fullScreen") != null) {
			stage.setFullScreen(thing.getBoolean("fullScreen"));
		}
		
		if(thing.getStringBlankAsNull("iconified") != null) {
			stage.setIconified(thing.getBoolean("iconified"));
		}
		
		if(thing.getStringBlankAsNull("maxHeight") != null) {
			stage.setMaxHeight(thing.getDouble("maxHeight"));
		}
		if(thing.getStringBlankAsNull("maxWidth") != null) {
			stage.setMaxWidth(thing.getDouble("maxWidth"));
		}
		if(thing.getStringBlankAsNull("minHeight") != null) {
			stage.setMinHeight(thing.getDouble("minHeight"));
		}
		if(thing.getStringBlankAsNull("minWidth") != null) {
			stage.setMinWidth(thing.getDouble("minWidth"));
		}
		if(thing.valueExists("modality")){
			stage.initModality(Modality.valueOf(thing.getString("modality")));
		}
		
		if(thing.getStringBlankAsNull("resizable") != null) {
			stage.setResizable(thing.getBoolean("resizable"));
		}
		
		String title = JavaFXUtils.getString(thing, "title", actionContext);
		if(title != null) {
			stage.setTitle(title);
		}
		
		WindowActions.init(thing, stage, actionContext);
	}

	public static void createNodes(ActionContext actionContext){
		Thing self = actionContext.getObject("self");

		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
	}
}
