package xworker.libdgx;

import org.xmeta.ActionException;
import org.xmeta.Thing;

public class ConstructException extends ActionException{
	private static final long serialVersionUID = 1L;

	public ConstructException(Thing thing){
		super("No matched constructor, thing=" + thing.getMetadata().getPath());
	}
}
