package xworker.ide.assistor.task;

import org.xmeta.Thing;
import org.xmeta.World;


/**
 * 一个事物的有几个注册事物的统计，比如向导有几个等等。
 * 
 * @author Administrator
 *
 */
public class StaticInfo {
	String registType;
	int count;
	
	public StaticInfo(String registType, int count){
		this.registType = registType;
		this.count = count;
	}
	
	public String getLabel(){
		Thing metaThing = World.getInstance().getThing("xworker.lang.MetaThing/@th_registThing");
		for(Thing child : metaThing.getChilds("value")){
			if(child.getMetadata().getName().equals(registType)){
				return child.getMetadata().getLabel();
			}
		}
		
		return registType;
	}
	
	public String getType(){
		return registType;
	}
	
	public int getCount(){
		return count;
	}
}
