package xworker.swt.xwidgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;
import xworker.swt.widgets.CompositeCreator;

public class Bootstrap {
	//用于记录当前Row的层级的，层级越高包含到的子节点越深，此时间隔越小
	private static ThreadLocal<Integer> rows = new ThreadLocal<Integer>();
	
	public static Object createBootstrap(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		//使用模板创建ScrolledComposite和具有GridLayout的composite
		ThingCompositeCreator cc = SwtUtils.createCompositeCreator(self, actionContext);
		cc.setCompositeThing(World.getInstance().getThing("xworker.swt.xwidgets.prototypes.BootstrapPrototype/@ScrolledComposite"));
		Object parent = cc.create();
		
		//创建字节点
		actionContext.peek().put("parent", cc.getNewActionContext().get("mainComposite"));
		for(Thing child : self.getChilds()) {
			child.doAction("createRow", actionContext);
		}
		
		//保存变量
		actionContext.g().put(self.getMetadata().getName(), parent);
		
		return parent;
	}
	
	public static void createRow(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Integer rowCount = rows.get();
		try {		
			if(rowCount == null) {
				rowCount = 0;				
			}else {
				rowCount = rowCount + 1;				
			}
			rows.set(rowCount);
		
			Composite composite = new Composite((Composite) actionContext.get("parent"), SWT.NONE);
			GridLayout layout = new GridLayout(12, true);
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			layout.marginRight = 0;
			
			layout.verticalSpacing = 20;// - 5 * rowCount;
			if(layout.verticalSpacing < 0) {
				layout.verticalSpacing = 0;
			}
			layout.horizontalSpacing = layout.verticalSpacing;
			composite.setLayout(layout);			
			
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			int height = self.getInt("height");
			if(height > 0) {
				gridData.heightHint = height;
			}
			gridData.horizontalSpan = self.getInt("span");
			int vspan = self.getInt("vspan");
			if(vspan > 0) {
				gridData.verticalSpan = vspan;
			}
			gridData.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;			
			composite.setLayoutData(gridData);
			
			actionContext.peek().put("parent", composite);
			
			for(Thing child : self.getChilds()) {
				child.doAction("createRow", actionContext);
			}
			
			//composite.pack();
		}finally {
			if(rowCount > 0) {
				rowCount = rowCount - 1;
				rows.set(rowCount);
			}else {
				rows.set(null);
			}
		}
	}
	
	public static void createCell(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		//Cell是Composite，先执行
		Composite composite = (Composite) CompositeCreator.create(actionContext);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		int height = self.getInt("height");
		if(height > 0) {
			gridData.heightHint = height;
		}
		gridData.horizontalSpan = self.getInt("span");
		int vspan = self.getInt("vspan");
		if(vspan > 0) {
			gridData.verticalSpan = vspan;
		}
		composite.setLayoutData(gridData);
		
	
	}
}
