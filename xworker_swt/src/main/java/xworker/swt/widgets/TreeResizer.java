package xworker.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

public class TreeResizer  implements Listener{
	Tree tree;
	Thing thing;
	float rations[];
	
	public TreeResizer(Thing thing, Tree tree) {
		this.thing = thing;
		this.tree = tree;
		
		init();
	}
	
	public void init() {
		String rs = thing.getStringBlankAsNull("rations");
		if(rs != null) {
			int[] r = UtilString.toIntArray(rs);
			int total = 0;
			for(int i=0; i<r.length; i++) {
				total += r[i];
			}
			
			rations = new float[r.length];
			for(int i=0; i<r.length; i++) {
				rations[i] = 1f * r[i] / total;
			}
		}else {
			rations = null;
		}
	}

	@Override
	public void handleEvent(Event event) {
		if(rations != null) {
			int width = tree.getClientArea().width;
			int index = 0;
			for(TreeColumn column : tree.getColumns()) {
				if(index < rations.length) {
					column.setWidth((int) (width * rations[index]));
				}else {
					column.setWidth(0);
				}
				
				index++;
			}
		}
	}
	
	public static void create(ActionContext actionContext) {
		Tree tree = actionContext.getObject("parent");
		Thing self = actionContext.getObject("self");
		
		TreeResizer resizer = new TreeResizer(self, tree);
		tree.addListener(SWT.Resize, resizer);
		
		actionContext.g().put(self.getMetadata().getName(), resizer);
	}

}
