package xworker.swt.xwidgets.clock;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;
import xworker.swt.util.ServerPushSession;

public class Clock implements PaintListener, Runnable{
	private static Logger logger = LoggerFactory.getLogger(Clock.class);
	
	Thing thing;
	ActionContext actionContext;
	Canvas canvas;
	ClockDrawer clockDrawer;
	Date date;
	ClockTask task;
	Thread clockTaskThread = null;
	ServerPushSession pushSession = new ServerPushSession();
	
	public Clock(Thing thing, Canvas canvas, ActionContext actionContext) {
		this.thing = thing;
		this.canvas = canvas;
		this.actionContext = actionContext;
		
		this.canvas.addPaintListener(this);
		
		//时间
		this.date = thing.doAction("getDate", actionContext);
		if(this.date == null) {
			this.date = new Date();
		}	
		
		//时钟绘画者
		Thing clockDrawerThing = thing.doAction("getClockDrawer", actionContext);
		if(clockDrawerThing != null) {
			this.clockDrawer = clockDrawerThing.doAction("create", actionContext);
		}
	}
	
	public void setDate(Date date) {
		this.date = date;
		
		this.canvas.redraw();
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setTime(String timeStr) throws ParseException {
		SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");
		setDate(sf.parse(timeStr));
	}
	
	public void setClockDrawer(ClockDrawer clockDrawer) {
		this.clockDrawer = clockDrawer;
	}

	public ClockDrawer getClockDrawer() {
		return clockDrawer;
	}
	
	@Override
	public void paintControl(final PaintEvent e) {
		if(clockDrawer != null) {
			Rectangle rc = canvas.getClientArea();
			clockDrawer.drawClock(date, rc.x, rc.y, rc.width, rc.height, e.gc);
		}
	}
	
	public Canvas getCanvas() {
		return canvas;
	}
	
	public static Canvas create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Action styleAction = World.getInstance().getAction("xworker.swt.widgets.Composite/@scripts/@getStyles");
		int style = (Integer) styleAction.run(actionContext);
		    
		Composite parent = (Composite) actionContext.get("parent");
		Canvas cavas = new Canvas(parent, style);
		
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", cavas);
		try{
		    self.doAction("init", actionContext);
		}finally{
		    actionContext.pop();
		}
		
		//保存变量和创建子事物
		actionContext.peek().put("parent", cavas);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		Designer.attach(cavas, self.getMetadata().getPath(), actionContext);
		
		Clock clock = new Clock(self, cavas, actionContext);
		actionContext.g().put(self.getMetadata().getName(), clock);
		
		//执行Clock动作，如果存在
		Thing clockActions = self.getThing("ClockActions@0");
		if(clockActions != null) {
			for(Thing clockAction : clockActions.getChilds()) {
				clockAction.getAction().run(actionContext);
			}
		}
		
		return cavas;
	}

	public void setTask(ClockTask task) {
		this.task = task;
		
		if(task != null && canvas.isDisposed() == false) {
			if(clockTaskThread == null || clockTaskThread.isAlive() == false) {
				pushSession.start();
				clockTaskThread = new Thread(this, "Clock Task Thread, path=" + thing.getMetadata().getPath());
				clockTaskThread.start();
			}
		}
	}
	
	public void stopTask() {
		setTask(null);
	}
	
	private void doTask() {
		try {			
			task.run(this);
		}catch(Exception e) {
			logger.error("Run Clock Task Error, path=" + thing.getMetadata().getPath(), e);
		}
	}
	@Override
	public void run() {
		while(true) {
			if(task == null) {
				break;
			}
			
			if(task.isFinished(this)) {
				break;
			}
			
			if(canvas.isDisposed()) {
				break;
			}
			
			try {
				canvas.getDisplay().asyncExec(new Runnable() {
					public void run() {
						Clock.this.doTask();
						Clock.this.pushSession.stop();
						Clock.this.pushSession.start();
					}
				});		
				canvas.getDisplay().wake();
				//logger.info("wake thread ");
				Thread.sleep(task.getInterval(this));
			}catch(Exception e) {
				logger.error("Run Clock Task Error, path=" + thing.getMetadata().getPath(), e);
			}			
		}
	}
}
