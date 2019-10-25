package xworker.java.awt;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.InputEvent;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class Robot {
	public static final String DEFAULT_KEY = "__default_java_awt_robot__";
		
	/**
	 * 创建默认的Robot。
	 * 
	 * @param actionContext
	 * @return
	 * @throws AWTException
	 */
	public static java.awt.Robot create(ActionContext actionContext) throws AWTException{
		World world = World.getInstance();
		java.awt.Robot robot = (java.awt.Robot) world.getData(DEFAULT_KEY);
		if(robot != null){
			return robot;
		}else{
			robot = new java.awt.Robot();
			world.setData(DEFAULT_KEY, robot);
		}
		return robot;
	}
	
	public static Object createScreenCapture(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		java.awt.Robot robot = (java.awt.Robot) self.doAction("getRobot", actionContext);
		Rectangle rec = (Rectangle) self.doAction("getRectangle", actionContext);
		if(rec == null){
			Dimension screensize  = Toolkit.getDefaultToolkit().getScreenSize();
			rec = new Rectangle(screensize);
			return robot.createScreenCapture(rec);
		}else{
			return robot.createScreenCapture(rec);
		}
	}
	
	public static void delay(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		java.awt.Robot robot = (java.awt.Robot) self.doAction("getRobot", actionContext);
		int ms = self.getInt("ms");
		robot.delay(ms);
	}
	
	public static int getAutoDelay(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		java.awt.Robot robot = (java.awt.Robot) self.doAction("getRobot", actionContext);
		return robot.getAutoDelay();
	}
	
	public static Object getPixelColor(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		java.awt.Robot robot = (java.awt.Robot) self.doAction("getRobot", actionContext);
		Point point = (Point) self.doAction("getPoint", actionContext);
		return robot.getPixelColor(point.x, point.y);
	}
	
	public static Object isAutoWaitForIdle(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		java.awt.Robot robot = (java.awt.Robot) self.doAction("getRobot", actionContext);
		return robot.isAutoWaitForIdle();
	}
	
	public static void keyPressText(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		java.awt.Robot robot = (java.awt.Robot) self.doAction("getRobot", actionContext);
		
		String text = self.doAction("getText", actionContext);
		int interval = self.doAction("getInterval", actionContext);
		if(text != null){
			for(byte c : text.getBytes()){
				robot.keyPress(c);
				robot.keyRelease(c);
				
				if(interval > 0){
					try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public static void keyPress(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		java.awt.Robot robot = (java.awt.Robot) self.doAction("getRobot", actionContext);
		Integer keycode = (Integer) self.doAction("getKeycode", actionContext);
		robot.keyPress(keycode);
	}
	
	public static void keyRelease(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		java.awt.Robot robot = (java.awt.Robot) self.doAction("getRobot", actionContext);
		Integer keycode = (Integer) self.doAction("getKeycode", actionContext);
		robot.keyRelease(keycode);
	}
	
	public static void mouseMove(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		java.awt.Robot robot = (java.awt.Robot) self.doAction("getRobot", actionContext);
		Point point = (Point) self.doAction("getPoint", actionContext);
		robot.mouseMove(point.x, point.y);
	}
	
	public static void mouseMoveAndClick(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		java.awt.Robot robot = (java.awt.Robot) self.doAction("getRobot", actionContext);
		Point point = (Point) self.doAction("getPoint", actionContext);
		robot.mouseMove(point.x, point.y);
		
		Integer buttons = (Integer) self.doAction("getButtons", actionContext);
		robot.mousePress(buttons);	
		robot.mouseRelease(buttons);
	}
	
	public static void mouseClick(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		java.awt.Robot robot = (java.awt.Robot) self.doAction("getRobot", actionContext);
		Integer buttons = (Integer) self.doAction("getButtons", actionContext);
		robot.mousePress(buttons);	
		robot.mouseRelease(buttons);
	}
	
	public static void mousePress(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		java.awt.Robot robot = (java.awt.Robot) self.doAction("getRobot", actionContext);
		Integer buttons = (Integer) self.doAction("getButtons", actionContext);
		robot.mousePress(buttons);
	}
	
	public static void mouseRelease(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		java.awt.Robot robot = (java.awt.Robot) self.doAction("getRobot", actionContext);
		Integer buttons = (Integer) self.doAction("getButtons", actionContext);
		robot.mouseRelease(buttons);
	}
	
	public static void mouseWheel(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		java.awt.Robot robot = (java.awt.Robot) self.doAction("getRobot", actionContext);
		Integer wheelAmt = (Integer) self.doAction("getWheelAmt", actionContext);
		robot.mouseWheel(wheelAmt);
	}
	
	public static void setAutoDelay(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		java.awt.Robot robot = (java.awt.Robot) self.doAction("getRobot", actionContext);
		Integer ms = (Integer) self.doAction("getMs", actionContext);
		robot.setAutoDelay(ms);
	}
	
	public static void setAutoWaitForIdle(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		java.awt.Robot robot = (java.awt.Robot) self.doAction("getRobot", actionContext);
		Boolean isOn = (Boolean) self.doAction("getIsOn", actionContext);
		robot.setAutoWaitForIdle(isOn);
	}
	
	public static void waitForIdle(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		java.awt.Robot robot = (java.awt.Robot) self.doAction("getRobot", actionContext);
		robot.waitForIdle();
	}
	
	public static Object getIsOn(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String isOnVar = self.getStringBlankAsNull("isOnVar");
		if(isOnVar != null){
			return actionContext.get(isOnVar);
		}
		
		return self.getBoolean("isOn");
	}
	
	public static Object getMs(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String msVar = self.getStringBlankAsNull("msVar");
		if(msVar != null){
			return actionContext.get(msVar);
		}
		
		return self.getInt("ms");
	}
	
	public static Object getWheelAmt(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String wheelAmtVar = self.getStringBlankAsNull("wheelAmtVar");
		if(wheelAmtVar != null){
			return actionContext.get(wheelAmtVar);
		}
		
		return self.getInt("wheelAmt");
	}
	
	public static Object getButtons(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String buttonsVar = self.getStringBlankAsNull("buttonsVar");
		if(buttonsVar != null){
			return actionContext.get(buttonsVar);
		}
		
		int buttons = self.getInt("buttons");
		switch(buttons){
		case 1:
			return InputEvent.BUTTON1_MASK;
		case 2:
			return InputEvent.BUTTON2_MASK;
		case 3:
			return InputEvent.BUTTON3_MASK;
		}
		
		return buttons; 
	}
	
	public static Object getKeycode(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return self.getInt("keycode");
	}
	
	public static Object getPoint(ActionContext actionContext) {
		Thing self = (Thing) actionContext.get("self");
		String pointVarName = self.getStringBlankAsNull("pointVarName");
		if(pointVarName != null){
			return actionContext.get(pointVarName);
		}
		
		int x = self.getInt("x");
		int y = self.getInt("y");
		return new Point(x, y);
	}
	public static Object getRobot(ActionContext actionContext) throws AWTException{
		Thing self = (Thing) actionContext.get("self");
		String robotPath = self.getStringBlankAsNull("robot");
		if(robotPath != null){
			Thing thing = World.getInstance().getThing(robotPath);
			if(thing != null){
				return thing.doAction("create", actionContext);
			}
		}
		
		return create(actionContext);
	}
	
	public static Object getRectangle(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String rectangleVarName = self.getStringBlankAsNull("rectangleVarName");
		if(rectangleVarName != null){
			return (Rectangle) actionContext.get(rectangleVarName);
		}
		
		String xstr = self.getStringBlankAsNull("x");
		if(xstr != null){
			int x = self.getInt("x");
			int y = self.getInt("y");
			int height = self.getInt("height");
			int width = self.getInt("width");
			return new Rectangle(x, y,width,height);
		}else{
			return null;
		}
	}
}
