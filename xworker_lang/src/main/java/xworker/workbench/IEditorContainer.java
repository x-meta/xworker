package xworker.workbench;

import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;

import java.util.List;
import java.util.Map;

public interface IEditorContainer<Container, Control, Image> {
    String EDITOR_CONTAINER = "editorContainer";

    /**
     * 打开一个编辑器，如果编辑器已经则返回已有的编辑器。
     *
     * @param id 编辑器标识
     * @param editor 编辑器模型
     * @param params 参数
     */
    IEditor<Container, Control, Image> openEditor(String id, Thing editor, Map<String, Object> params);

    /**
     * 返回指定ID的编辑器，如果存在则返回，否则返回null。
     *
     * @param id  编辑器标识
     * @return 编辑器，如果不存在返回null
     */
    IEditor<Container, Control, Image> getEditor(String id);

    /**
     * 编辑器的状态变更了，通知编辑器容器。
     *
     * @param editorActions 编辑器的动作容器
     */
    void editorModified(ActionContainer editorActions);

    /**
     * 返回概要栏的Composite，用于编辑器创建它自己的概要界面。
     *
     * @return 概要栏的父控件
     */
    Container getOutlineParent();

    /**
     * 设置编辑器的概要界面。
     *
     * @param outlineComposite 概要栏控件
     */
    void setEditorOutline(Container outlineComposite);

    /**
     * 是否有概要。
     */
    boolean hasOutline();

    /**
     * 是否有菜单。
     */
    boolean hasMenu();

    /**
     * 是否有CoolBar。
     */
    boolean hasCoolBar();

    /**
     * 是否有StatusBar。
     */
    boolean hasStatusBar();

    /**
     * 遍历所有的编辑器，如果存在一个编辑器的内容已修改，则返回true，否则返回false。
     */
    boolean isDirty();

    /**
     * 遍历所有的编辑器，如果编辑的内容已改动，则保存。
     */
    void saveAll();

    /**
     * 如果当前编辑器的数据已改动，则保存。
     */
    void save();

    /**
     * 返回所有编辑器。
     */
    List<IEditor<Container, Control, Image>> getEditors();

    /**
     * 返回当前处于激活状态的编辑器。
     */
    IEditor<Container, Control, Image> getActiveEditor();

    /**
     * 返回编辑器容器自己的Composite。
     */
    Container getComposite();

    /**
     * 返回所有编辑器。
     *
     * @param dirty 是否编辑器处于未保存状态
     */
    List<IEditor<Container, Control, Image>> getEditors(boolean dirty);

    /**
     * 关闭一个编辑器。
     */
    void close(IEditor<Container, Control, Image> editor);

    /**
     * 添加一个编辑器监听器。
     */
    void addIEditorListener(IEditorListener<Container, Control, Image> editorListener);

    /**
     * 移除一个编辑器监听器。
     */
    void removeIEditorListener(IEditorListener<Container, Control, Image> editorListener);

    /**
     * 触发一个编辑器状态改变的事件。
     */
    void fireStateChanged(IEditor<Container, Control, Image> editor);

    IMenuContainer getMenuContainer();

    ICoolBarContainer getCoolBarContainer();

    void setWorkbench(IWorkbench<Container, Control, Image> workbench);

    IWorkbench<Container, Control, Image> getWorkbench();
}
