package xworker.swt.app;

import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;

public interface IEditor {
	/**
	 * 设置编辑器要编辑的内容。参数有编辑器自行定义。
	 * 
	 * @param params
	 */
	public void setContent(Map<String, Object> params);
	
	/**
	 * 是否和当前正在编辑的内容一致。如果一致返回true，否则返回false。
	 * 
	 * @param params
	 * @return
	 */
	public boolean isSameContent(Map<String, Object> params);
	
	/**
	 * 执行保存操作。
	 */
	public void doSave();
	
	/**
	 * 数据是否已经被修改了。
	 * @return
	 */
	public boolean isDirty();
	
	/**
	 * 返回编辑器的短标题。
	 * 
	 * @return
	 */
	public String getSimpleTitle();
	
	/**
	 * 返回编辑器的长标题。长标题通常显示在Shell的标题上。
	 * 
	 * @return
	 */
	public String getTitle();
	
	/**
	 * 返回编辑器的概要组建。不存在返回null。
	 * 
	 * @return
	 */
	public Composite getOutline();
	
	public void doDispose();
	
	public Thing getMenuConfig();
	
	public Thing getCoolBarConfig();
	
	public Thing getStatusBarConfig();
	
	public Thing getEditor();
	
	public ActionContainer getActions();
	
	public ActionContext getActionContext();
		
	/**
	 * 返回编辑器的图标。
	 * 
	 * @return
	 */
	public Image getIcon();
}
