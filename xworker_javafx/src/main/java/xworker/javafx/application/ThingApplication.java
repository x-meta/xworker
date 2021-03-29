package xworker.javafx.application;

import java.util.Optional;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import xworker.javafx.stage.StageActions;

public class ThingApplication extends Application{
	private static boolean started = false;
	private static Thing thing = null;
	private static ActionContext actionContext = null;
	
	public static synchronized void checkStart() {
		if(started == false) {
			new Thread() {
				public void run() {					
					Application.launch(ThingApplication.class, new String[] {});
					
					//ThingApplication.started = false;
					System.out.println("JavaFX is exited.");
				}
			}.start();
						
			while(started == false) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public ThingApplication() {
	}

	public static void create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		if(!started) {
			ThingApplication.thing = self;
			ThingApplication.actionContext = actionContext;

			checkStart();
		}else{
			StageActions.create(actionContext);
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		//primaryStage.setIconified(true);
		if(thing != null && actionContext != null) {
			//通过Application第一次启动的
			StageActions.init(primaryStage, thing, actionContext);

			actionContext.g().put(thing.getMetadata().getName(), primaryStage);
			actionContext.peek().put("parent", primaryStage);
			for(Thing child : thing.getChilds()) {
				child.doAction("create", actionContext);
			}

			primaryStage.show();
		}else{
			//通过Stage第一次启动的
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent event) {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setContentText(UtilString.getString("lang:d=确实要关闭JavaFX么？&en=Are you want to close JavaFX?", new ActionContext()));
					Optional<ButtonType> result = alert.showAndWait();
					if (result.isPresent() && result.get() == ButtonType.OK) {

					} else {
						event.consume();
					}

				}

			});
			primaryStage.setHeight(10);
			primaryStage.setWidth(10);
			primaryStage.show();
			//primaryStage.hide();
		}
		ThingApplication.started = true;
	}

	public static void setStarted(){
		ThingApplication.started = true;
	}
	
	public static void main(String[] args) {
		checkStart();
	}

}
