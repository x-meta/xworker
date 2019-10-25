package xworker.statistics.flow;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import xworker.dataObject.DataObject;
import xworker.task.Task;
import xworker.task.TaskManager;

public class FlowStatistics {
	public Thing thing;
	public ActionContext actionContext;
	public MapStore mapStore;
	public List<KeyManager> keyManagers;
	public DataInputer dataInputer;	
	public DataSaver dataSaver;
	private boolean saveData = false;
	private Task task;
	boolean running = false;
	
	public FlowStatistics(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
		
		Thing inputThing = thing.doAction("getDataInputer", actionContext);
		if(inputThing == null){
			throw new ActionException("DataInputer is null, thing=" + thing.getMetadata().getPath());
		}
		
		Thing mapStoreThing = thing.doAction("getMapStore", actionContext);
		if(mapStoreThing == null){
			throw new ActionException("MapStore is null, thing=" + thing.getMetadata().getPath());
		}
		
		Thing dataSaverThing = thing.doAction("getMapStore", actionContext);
		if(dataSaverThing == null){
			dataSaver = new DataSaver(this, thing, actionContext);
		}
		
		List<Thing> keyManagerThings = thing.doAction("getKeyManagers", actionContext);
		if(keyManagerThings == null || keyManagerThings.size() == 0){
			throw new ActionException("No keyManagers, thing=" + thing.getMetadata().getPath());
		}
		
		mapStore = new MapStore(this, mapStoreThing, actionContext);
		dataInputer = new DataInputer(this, inputThing, actionContext);
		keyManagers = new ArrayList<KeyManager>();
		for(Thing keyManagerThing : keyManagerThings){
			keyManagers.add(new KeyManager(this, keyManagerThing, actionContext));
		}
		
		running = true;
	}
	
	public void start(){
		//是否保存数据
		saveData = thing.doAction("isSaveData", actionContext);
		if(saveData && thing.getBoolean("schedule")){
			//启动定时保存数据的任务
			task = TaskManager.startTask(thing, actionContext, UtilMap.toMap("flowStatistics", this));
		}
		
		if(dataSaver != null){
			dataSaver.init();
		}
		
		dataInputer.start();
	}
	
	public void stop(){
		running = false;
		
		//停止数据输入
		dataInputer.stop();
		
		//停止任务，如果存在
		if(task != null){
			task.cancel(true);
		}
		
		if(saveData){
			//转存数据
			saveDatas();
		}
		
		//如果debug，打印数据
		if(UtilData.isTrue(thing.doAction("isDebug", actionContext))){
			printDatas();
		}
		
		//移除所有的key
		if(UtilData.isTrue(thing.doAction("isRemoveDataOnStop", actionContext))){
			for(KeyManager km : keyManagers){				
				String keyManager = km.thing.getMetadata().getPath();
				List<String> keys = mapStore.getKeyList(keyManager);
				
				if(keys != null){
					for(String key : keys){
						mapStore.removeKey(key, keyManager);
					}
				}
			}
		}
		
		//关闭MapStore
		mapStore.close();
		
		if(dataSaver != null){
			dataSaver.close();
		}
	}
	
	public void input(Object data, DataInputer dataInputer){
		for(KeyManager km : keyManagers){
			List<KeyData> keyDatas = km.generateKey(data);
			if(keyDatas != null){
				for(KeyData keyData : keyDatas){
					//查看是否存在key
					if(!mapStore.exists(keyData.key)){
						mapStore.addToKeyList(keyData.key, km.thing.getMetadata().getPath());
					}
					
					//增加统计数据
					mapStore.inc(keyData.key, keyData.count);
				}
			}
		}
	}
	
	public void inputFinished(DataInputer dataInputer){
		stop();
	}
	
	/**
	 * 通过数据对象存储数据。
	 */
	public synchronized void saveDatas(){
		if(dataSaver == null){
			throw new ActionException("DataSaver is null, thing=" + thing.getMetadata().getPath());
		}
		
		List<KeyManagerData> keyManagerDatas = getStatistics();
		for(KeyManagerData kmd : keyManagerDatas){
			//System.out.println(kmd.keyManaer.thing.getMetadata().getLabel() + " - " + kmd.keyManaer.thing.getMetadata().getPath());
			
			for(KeyData keyData : kmd.keyDatas){
				//System.out.println("    " + keyData.key + " ：" + keyData.count);
				DataObject dataObject = kmd.keyManaer.getDataObject(keyData.key, keyData.count);
				if(dataObject != null){
					dataSaver.save(dataObject);
				}
			}
		}
	}
	
	/**
	 * 获取当前的统计信息。
	 * 
	 * @return
	 */
	public List<KeyManagerData> getStatistics(){
		List<KeyManagerData> keyManagerDatas = new ArrayList<KeyManagerData>();
		
		for(KeyManager km : keyManagers){
			KeyManagerData keyManagerData = new KeyManagerData();
			keyManagerData.keyManaer = km;
			keyManagerDatas.add(keyManagerData);
			
			List<String> keys = mapStore.getKeyList(km.thing.getMetadata().getPath());
			
			if(keys != null){
				for(String key : keys){
					long count = mapStore.getKeyCount(key);
					
					KeyData keyData = new KeyData(key, count);
					keyManagerData.keyDatas.add(keyData);
				}
			}
		}
		
		return keyManagerDatas;
	}
	
	public void printDatas(){
		List<KeyManagerData> keyManagerDatas = getStatistics();
		for(KeyManagerData kmd : keyManagerDatas){
			System.out.println(kmd.keyManaer.thing.getMetadata().getLabel() + " - " + kmd.keyManaer.thing.getMetadata().getPath());
			
			for(KeyData keyData : kmd.keyDatas){
				System.out.println("    " + keyData.key + " ：" + keyData.count);
			}
		}
	}
	
	public static FlowStatistics run(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
				
		World world = World.getInstance();		
		FlowStatistics flowStatistics = (FlowStatistics) world.getData(self.getMetadata().getPath());
		if(flowStatistics == null || flowStatistics.running == false){
			flowStatistics = new FlowStatistics(self, actionContext);
			world.setData(self.getMetadata().getPath(), flowStatistics);
			flowStatistics.start();
		}
		
		
		return flowStatistics;
	}
	
	public static void doTask(ActionContext actionContext){
		FlowStatistics flowStatistics = actionContext.getObject("flowStatistics");
		flowStatistics.saveDatas();
	}
	
	public static void stop(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		World world = World.getInstance();		
		FlowStatistics flowStatistics = (FlowStatistics) world.getData(self.getMetadata().getPath());
		if(flowStatistics != null && flowStatistics.running){
			flowStatistics.stop();
		}
	}
	
	public static void printDatas(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		World world = World.getInstance();		
		FlowStatistics flowStatistics = (FlowStatistics) world.getData(self.getMetadata().getPath());
		if(flowStatistics != null){
			flowStatistics.printDatas();
		}	
	}
	
	public static FlowStatistics getFlowStatistics(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		World world = World.getInstance();		
		return (FlowStatistics) world.getData(self.getMetadata().getPath());
	}
}
