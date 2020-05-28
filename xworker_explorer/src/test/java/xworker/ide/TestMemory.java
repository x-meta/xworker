package xworker.ide;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

import org.xmeta.Thing;
import org.xmeta.World;

public class TestMemory {
	public static void main(String args[]){
		try{
			//读取所有的事物，看看内存占用
			World world = World.getInstance();
			world.init("xworker");
			
			//初始化之外的所有项目
			Thing projectSet = world.getThing("_local.xworker.worldExplorer.ProjectSet");
			if(projectSet != null){
				projectSet.doAction("run");
			}
			
			long start = System.currentTimeMillis();
			Thing thing = world.getThing("xworker.ide.worldexplorer.swt.SimpleExplorer");
			int count = 1000000;
			Thing[] things = new Thing[count];
			for(int i=0; i<count; i++){
				things[i] = new Thing("");//thing.detach();
			}			
			System.out.println("Thing create time: " + (System.currentTimeMillis() - start));
			/*
			//遍历所有的事物
			for(ThingManager thingManager : world.getThingManagers()){
				System.out.println("ThingManager : " + thingManager.getName());
				
			    Iterator<Thing> iter = thingManager.iterator(null, true);
			    while(iter.hasNext()){
			        Thing thing = iter.next();
			        //System.out.println(thing.getMetadata().getPath());        
			        count++;
			    }
			}*/

			System.out.println("Total thing:" + count);
			MemoryMXBean mx = ManagementFactory.getMemoryMXBean();
			System.out.println("Memory heap: " + mx.getHeapMemoryUsage());
			System.out.println("Memory nonheap: " + mx.getNonHeapMemoryUsage());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
