package xworker.ide.worldexplorer.actions;

import org.xmeta.Thing;

public interface ProjectTreeService {
	/**
	 * 在项目树中定位一个模型，定位到它所在的包。
	 * 
	 * @param thing
	 */
	public void locate(Thing thing);
}
