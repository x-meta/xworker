package xworker.javafx.application;

import java.util.Optional;

import org.xmeta.ActionContext;
import org.xmeta.util.UtilString;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ThingApplication extends Application{
	private static boolean started = false;
	
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
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		//primaryStage.setIconified(true);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setContentText(UtilString.getString("lang:d=确实要关闭JavaFX么？&en=Are you want to close JavaFX?", new ActionContext()));
				Optional<ButtonType> result = alert.showAndWait();
				 if (result.isPresent() && result.get() == ButtonType.OK) {
					 
				 }	else {
					 event.consume();
				 }
					 
			}
			
		});
		primaryStage.setHeight(10);
		primaryStage.setWidth(10);
		primaryStage.show();
		//primaryStage.hide();
		ThingApplication.started = true;
	}
	
	
	public static void main(String[] args) {
		checkStart();
	}

}
