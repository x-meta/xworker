package xworker.swt.app;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;

public interface IEditorContainer {
	public static final String EDITOR_CONTAINER = "editorContainer";
	
	/**
	 * 打开一个编辑器，如果编辑器已经则返回已有的编辑器。
	 * 
	 * @param id
	 * @param editor
	 * @param params
	 */
	public IEditor openEditor(String id, Thing editor, Map<String, Object> params);
	
	/**
	 * 编辑器的状态变更了，通知编辑器容器。
	 * 
	 * @param editorActions
	 */
	public void editorModified(ActionContainer editorActions);
	
	/**
	 * 返回概要栏的Composite，用于编辑器创建它自己的概要界面。
	 * 
	 * @return
	 */
	public Composite getOutlineParent();
	
	/**
	 * 设置编辑器的概要界面。
	 * 
	 * @param outlineComposite
	 */
	public void setEditorOutline(Composite outlineComposite);
	
	/**
	 * 是否有概要。
	 * 
	 * @return
	 */
	public boolean hasOutline();
	
	/**
	 * 是否有菜单。
	 * 
	 * @return
	 */
	public boolean hasMenu();
	
	/**
	 * 是否有CoolBar。
	 * @return
	 */
	public boolean hasCoolBar();
	
	/**
	 * 是否有StatusBar。
	 * @return
	 */
	public boolean hasStatusBar();
	
	/**
	 * 遍历所有的编辑器，如果存在一个编辑器的内容已修改，则返回true，否则返回false。
	 * @return
	 */
	public boolean isDirty();
	
	/**
	 * 遍历所有的编辑器，如果编辑的内容已改动，则保存。
	 */
	public void saveAll();
	
	/**
	 * 如果当前编辑器的数据已改动，则保存。
	 */
	public void save();
	
	/**
	 * 返回所有编辑器。
	 * @return
	 */
	public List<IEditor> getEditors();
	
	/**
	 * 返回编辑器容器自己的Composite。
	 * 
	 * @return
	 */
	public Composite getComposite();
	
	/**
	 * 返回所有编辑器。
	 * 
	 * @param dirty 是否编辑器处于未保存状态
	 * @return
	 */
	public List<IEditor> getEditors(boolean dirty);
	
	/**
	 * 关闭一个编辑器。
	 * 
	 * @param editor
	 */
	public void close(IEditor editor);
}
