package xworker.libdgx;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;

import xworker.libdgx.engine.collision.CollisionManager;
import xworker.libdgx.engine.world2d.GameWorld;

public class XWorkerScreen extends ScreenAdapter {
	private static Logger logger = LoggerFactory.getLogger(XWorkerScreen.class);
	
	/** 保存Screen中的变量。 */
	ActionContext actionContext;
	/** 定义Screen的事物。 */
	Thing thing;
	/** 事物的最后修改日期 */
	long lastModified;
	/** 需要释放的资源 */
	List<Disposable> resources = new ArrayList<Disposable>();
	/** 屏幕定义的Stage列表 */
	List<Stage> stageList = new ArrayList<Stage>();
	/** 当前的舞台 */
	Stage currentStage = null;	
	boolean disposed = false;
	/** 碰撞管理器 */
	CollisionManager collisionManager;
	/** 游戏世界列表，可以有多个 */
	List<GameWorld> worlds = new ArrayList<GameWorld>();
	
	public XWorkerScreen(Thing thing){
		this.thing = thing;
		this.lastModified = thing.getMetadata().getLastModified();
		this.actionContext = new ActionContext();
		
		init();
	}
	
	public void initMenu(Menu menu){
		Bindings bindings = actionContext.push();
		bindings.put("parent", menu);
		try{
			Thing menuThing = thing.getThing("Menu@0");
			if(menuThing != null){
				for(Thing child : menuThing.getChilds()){
					child.doAction("create", actionContext);
				}
			}
		}catch(Exception e){
			logger.error("init screen menu error" ,e );
		}finally{
			actionContext.pop();
		}
	}
	
	public void init(Shell shell){
		int width = thing.getInt("width");
		int height = thing.getInt("height");
		
		if(width > 0 && height > 0){
			shell.setSize(width, height);
		}
	}
	
	public static void run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		run(self);
	}
	
	public static void run(Thing thing){
		ScreenManager.setScreen(thing);
	}
	
	public static void createActors(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
	}
	
	public static void createActions(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
	}
	
	public static void createEventListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
	}
	
	public static void createResources(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		XWorkerScreen screen = (XWorkerScreen) actionContext.get("screen");
		
		for(Thing child : self.getChilds()){
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Disposable){
				screen.resources.add((Disposable) obj);
			}
		}
	}
	
	public static void createStages(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		XWorkerScreen screen = (XWorkerScreen) actionContext.get("screen");
		
		for(Thing child : self.getChilds()){
			Object s = child.doAction("create", actionContext);
			if(s instanceof Stage){
				screen.stageList.add((Stage) s);
			}
		}
	}

	private void init(){
		actionContext.getScope(0).put("screen", this);
		actionContext.getScope(0).put(thing.getMetadata().getName(), this);
		Bindings bindings = actionContext.push();
		bindings.put("screen", this);
		
		try{
			for(Thing child : thing.getChilds()){
				child.doAction("create", actionContext);
			}
		}finally{
			actionContext.pop();
		}
			
		if(stageList.size() > 0){
			currentStage = stageList.get(0);
		}
	}
	
	@Override
	public void render(float delta) {
		if(thing.getStringBlankAsNull("red") != null && thing.getStringBlankAsNull("green") != null && 
				thing.getStringBlankAsNull("blue") != null && thing.getStringBlankAsNull("alpha") != null){
			Gdx.gl.glClearColor(thing.getFloat("red"), thing.getFloat("green"), thing.getFloat("blue"), thing.getFloat("alpha"));
		}else{
			Gdx.gl.glClearColor(1, 1, 1, 1);
		}
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);			
		
		try{
			if(collisionManager != null){
				collisionManager.checkCollide(delta);
			}
						
			if(currentStage != null){
				currentStage.act(delta);
				currentStage.draw();
			}
			
			actionContext.peek().put("delta", delta);
			for(GameWorld world : worlds){
				world.run(delta);
			}
						
			thing.doAction("render", actionContext);
		}catch(Exception e){
			logger.error("render errot,  thing=" + thing.getMetadata().getPath(), e);
		}
	}

	@Override
	public void dispose() {
		disposed = true;
		
		try{
			//销毁所有的舞台
			for(Stage stage : stageList){
				try{
					stage.dispose();
				}catch(Exception ee){			
					logger.error("stage dispose error, thing=" + thing.getMetadata().getPath(), ee);
				}
			}	
			
			//销毁所有资源
			for(Disposable res : resources){
				try{
					res.dispose();
				}catch(Exception ee){
					logger.error("resource dispose error, thing=" + thing.getMetadata().getPath(), ee);
				}
			}
			
			thing.doAction("dispose", actionContext);
		}catch(Exception e){
			logger.warn("dispose error", e);
		}
	}
	
	public boolean isDisposed() {
		return disposed;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public Thing getThing() {
		return thing;
	}

	public long getLastModified() {
		return lastModified;
	}

	public List<Stage> getStageList() {
		return stageList;
	}

	public Stage getCurrentStage() {
		return currentStage;
	}
	
	@Override
	public void resize(int width, int height) {
		if(currentStage != null){
			currentStage.getViewport().update(width, height, true);
		}
	}

	public void setCollisionManager(CollisionManager collisionManager) {
		this.collisionManager = collisionManager;
	}

	public List<GameWorld> getWorlds() {
		return worlds;
	}
	
	public void addWorld(GameWorld world){
		worlds.add(world);
	}
}
