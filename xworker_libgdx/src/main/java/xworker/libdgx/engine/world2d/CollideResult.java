package xworker.libdgx.engine.world2d;

public class CollideResult {
	public Object source;
	public Object target;
	public boolean overlaps;
	
	public CollideResult(Object source, Object target, boolean overlaps){
		this.source = source;
		this.target = target;
		this.overlaps = overlaps;
	}	
}
