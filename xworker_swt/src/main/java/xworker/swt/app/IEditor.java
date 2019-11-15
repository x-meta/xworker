package xworker.swt.app;

import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;

public interface IEditor {
	/** 用于Editor.createDataParams()时在Map中存放Editor模型的值 */
	public static final String EDITOR_THING = "__IEidtor_thing__";
	
	/** 用于Editor.createDataParams()时在Map中存放Editor的id值 */
	public static final String EDITOR_ID = "__IEditor_id__";
	
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
	
	/**
	 * 执行销毁操作。
	 */
	public void doDispose();
	
	/**
	 * 返回编辑器的菜单配置。
	 * 
	 * @return
	 */
	public Thing getMenuConfig();
	
	/**
	 * 返回编辑器的工具栏配置。
	 * 
	 * @return
	 */
	public Thing getCoolBarConfig();
	
	/**
	 * 返回编辑器的状态栏配置。
	 * 
	 * @return
	 */
	public Thing getStatusBarConfig();
	
	/**
	 * 返回编辑器事物本身。
	 * 
	 * @return
	 */
	public Thing getEditor();
	
	/**
	 * 返回编辑器自己的动作容器。
	 * 
	 * @return
	 */
	public ActionContainer getActions();
	
	/**
	 * 返回编辑器自己的变量上下文。
	 * 
	 * @return
	 */
	public ActionContext getActionContext();
		
	/**
	 * 返回编辑器的图标。
	 * 
	 * @return
	 */
	public Image getIcon();
}
