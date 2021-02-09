package xworker.swt.nebula;

import java.text.ParseException;

import org.eclipse.nebula.widgets.opal.calculator.CalculatorCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;

import xworker.swt.design.DesignTools;
import xworker.swt.design.Designer;
import xworker.swt.model.Model;
import xworker.swt.model.ModelManager;
import xworker.swt.util.SwtUtils;

public class CalculatorComboActions {	
	static {
		DesignTools.registUnInsertable(CalculatorCombo.class);
		
		ModelManager.regist(CalculatorCombo.class, new Model() {

			@Override
			public void setValue(Object control, Object value, String viewPattern, String editPattern) {
				CalculatorCombo calc = (CalculatorCombo) control;
				if(editPattern != null && !"".equals(editPattern)) {
					calc.setValue(UtilData.format(value, editPattern));
				}else {
					if(value != null) {
						calc.setValue(String.valueOf(value));
					}else {
						calc.setValue("");
					}
				}
			}

			@Override
			public Object getValue(Object control, String type, String pattern) {
				CalculatorCombo calc = (CalculatorCombo) control;
				
				try {
					return UtilData.parse(calc.getValue(), type, pattern);
				} catch (ParseException e) {					
					e.printStackTrace();
					return null;
				}
			}
			
		});
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Object obj = SwtUtils.createNoSupportRAP(self, actionContext);
		if(obj != null) {
			return obj;
		}
		
		Action styleAction = World.getInstance().getAction("xworker.swt.widgets.Composite/@scripts/@getStyles");
		int style = (Integer) styleAction.run(actionContext);
		if(self.getBoolean("FLAT")) {
			style = style | SWT.FLAT;
		}
		Composite parent = actionContext.getObject("parent");
		CalculatorCombo calc = new CalculatorCombo(parent, style);
		
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
