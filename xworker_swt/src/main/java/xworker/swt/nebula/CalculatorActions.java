package xworker.swt.nebula;

import org.eclipse.nebula.widgets.opal.calculator.Calculator;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;

public class CalculatorActions {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Object obj = SwtUtils.createNoSupportRAP(self, actionContext);
		if(obj != null) {
			return obj;
		}
		
		
		Action styleAction = World.getInstance().getAction("xworker.swt.widgets.Composite/@scripts/@getStyles");
		int style = (Integer) styleAction.run(actionContext);
		Composite parent = actionContext.getObject("parent");
		Calculator calc = new Calculator(parent, style);
		
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", calc);
		try{
		    self.doAction("init", actionContext);
		}finally{
		    actionContext.pop();
		}
		
		//创建子节点
		actionContext.peek().put("parent", calc);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		actionContext.g().put(self.getMetadata().getName(), calc);
		Designer.attach(calc, self.getMetadata().getPath(), actionContext);
		return calc;
	}
}
