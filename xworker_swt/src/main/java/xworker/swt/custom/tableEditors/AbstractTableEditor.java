package xworker.swt.custom.tableEditors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.annotation.ActionField;
import org.xmeta.annotation.ActionParams;
	
/**
 * 最初是基于TableCursor而创建的表格单元格编辑器，由于可以有一定通用性，也被用到了Tree和其它控件上。
 * 
 * 使用TableCursor上编辑器时，一次只能编辑一个单元格，在编辑的开始时会创建编辑控件，在编辑结束后会销毁控件
 * 这个时候会新建的一个独立的变量上下文，编辑的环境是相对封闭的，不需要担心变量的范围问题。
 * 
 * 使用了Action注解，会实例化生成一个编辑器对象，编辑器对象以类的名称作为key保存到变量上下文中，由于是
 * 封闭的环境，且编辑是在双击TableCursor才开始编辑的，因为不需要考虑变量问题。
 * 
 * TableEditor是通过相应的TableEditor事物创建的，create、setValue、getValue等方法是通过事物调用的，
 * 事物本身不存储变量等，变量都在独立的变量上下文中。
 * 
 * @author zyx
 *
 */
public abstract class AbstractTableEditor {
	@ActionField()
	protected Object cursor;
	@ActionField()
	protected Item item;
	@ActionField()
	protected Thing cursorThing;
	@ActionField()
	protected ActionContext parentContext;
	@ActionField()
	protected Composite table;
	@ActionField()
	protected Thing self;
	@ActionField()
	protected ActionContext actionContext;
	
	public abstract Object create(ActionContext actionContext);
	
	@ActionParams(names="value")
	public abstract void setValue(Object value, ActionContext actionContext);
	
	public abstract Object getValue(ActionContext actionContext);
	
	public abstract void doDispose();
	
	public int getColumn() {
		return ItemEditorUtils.getCursorColumn(cursor);
	}
	/**
	 * 保存值到Cursor。
	 */
	public void saveValue() {
		ItemEditorUtils.saveValue(item, getColumn(),  getValue(actionContext), actionContext);
		
		//cursorThing.doAction("setValue", parentContext, 
 	    //        UtilMap.toParams(new Object[]{"item", item, 
 	    //        		"column", getColumn(), "value", getValue(actionContext)}));
	}
}
