package xworker.system.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import xworker.system.ISystem;
import xworker.system.SystemManager;

public class LogicSystem implements Runnable, ISystem{
	private static Logger logger = LoggerFactory.getLogger(LogicSystem.class);
	
	Thing thing;
	ActionContext actionContext;
	boolean busy = false;
	boolean stoped = false;
	
	public LogicSystem(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
		
		SystemManager.regist(thing.getMetadata().getPath(), this);
	}

	public Thing getThing() {
		return thing;
	}

	public void setBusy(boolean busy){
		this.busy = busy;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}


	@Override
	public void run() {
		try{
			//初始化
			doAction("init");
			
			while(true){
				try{
					//执行系统的方法
					doSystem();
					
					if(stoped){
						break;
					}
					
					boolean isShutdown = (Boolean) doAction("isShutdown");
					if(UtilData.isTrue(doAction("isLoop")) && !isShutdown){
						long time = (Long) doAction("getSleep");
						Thread.sleep(time);
					}else{
						break;
					}
				}catch(Exception e){
					logger.warn("System: " + thing.getMetadata().getPath(), e);
					
					if(!UtilData.isTrue(doAction("onException", e))){
						break;
					}
				}
			}
			
			doAction("shutdown");
		}finally{
			SystemManager.unregist(thing.getMetadata().getPath(), this);
		}
	}
	
	public Object doAction(String name){
		return doAction(name, null);
	}
	
	public Object doAction(String name, Exception e){
		if(e == null){
			return thing.doAction(name, actionContext, "system", this);
		}else{
			return thing.doAction(name, actionContext, "system", this, "exception", e);
		}
	}
	
	private boolean initThings(){
		if(busy){
			return false;
		}
		
		Thing env = thing.getThing("Environment@0");
		if(env == null){
			return true;
		}else{
			for(Thing thing : env.getChilds()){
				//获取变量
				String name = thing.getMetadata().getName();
				Object obj = actionContext.g().get(name);
				if(obj != null){
					//是否已经初始化
					if(!UtilData.isTrue(thing.doAction("isExpired", actionContext, "system", this))){
						continue;
					}
				}else if(!UtilData.isTrue(thing.doAction("isNotNull", actionContext, "system", this))){
					continue;
				}
				
				//重新初始化变量
				thing.doAction("initObject", actionContext, "system", this);
				
				if(busy){
					return false;
				}
			}
		}
		
		return true;
	}
	
	private void doLogic(){
		if(busy){
			return;
		}
		
		Thing logics = thing.getThing("Logics@0");
		for(Thing child : logics.getChilds()){
			child.doAction("doLogic", actionContext);
		}
	}
	
	public static void doSystem(ActionContext actionContext){
		LogicSystem system = actionContext.getObject("system");				
		system.doSystem();
	}
	
	public static LogicSystem run(ActionContext actionContext){		
		Thing self = actionContext.getObject("self");
		if(self.getBoolean("singleInstance")){
			List<ISystem> list = SystemManager.getSystems(self.getMetadata().getPath());
			if(list != null && list.size() > 0){
				return null;
			}
		}
		
		//创建新的变量上下文
		ActionContext ac = new ActionContext();
		ac.put("parentContext", actionContext);
		
		LogicSystem system = new LogicSystem(self, actionContext);
		if(UtilData.isTrue(self.doAction("isThread", ac))){
			new Thread(system, self.getMetadata().getPath()).start();
		}else{
			system.run();
		}
		
		return system;
	}

	@Override
	public void doSystem() {
		//初始化系统环境中的事物
		if(!initThings()){
			return;
		}
		
		//执行逻辑
		doLogic();
	}

	@Override
	public void stop() {
		stoped = true;
	}
	
	@Override
	public boolean isStoped(){
		return stoped;
	}
	
	public static void doLogicSequence(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		for(Thing child : self.getChilds()){
			child.doAction("doLogic", actionContext);
		}
	}
	
	public static void doLogicRandom(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		if(self.getChilds().size() > 0){
			List<Thing> childs = new ArrayList<Thing>();
			childs.addAll(self.getChilds());
			
			int count = self.getInt("count", 1);
			Random random = new Random();
			while(count > 0 && childs.size() > 0){
				count--;
				int index = random.nextInt(childs.size());
				Thing th = childs.remove(index);
				th.doAction("doLogic", actionContext);
			}			
		}
	}
	
	public static void doLogicAction(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		self.doAction("doAction", actionContext);
	}
	
	public static void stopAll(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		List<ISystem> systemList = SystemManager.getSystems(self.getMetadata().getPath());
		if(systemList != null){
			for(ISystem system : systemList){
				system.stop();
			}
		}
	}
}
