package xworker.workbench;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;

import java.util.Map;

public interface IEditor<Container, Control, Image> {
    /** 用于Editor.createDataParams()时在Map中存放Editor模型的值 */
    String EDITOR_THING = "__IEidtor_thing__";

    /** 用于Editor.createDataParams()时在Map中存放Editor的id值 */
    String EDITOR_ID = "__IEditor_id__";

    /**
     * 返回编辑器的标识。
     */
    String getId();

    /**
     * 设置编辑器要编辑的内容。参数有编辑器自行定义。
     */
    void setContent(Map<String, Object> params);

    /**
     * 是否和当前正在编辑的内容一致。如果一致返回true，否则返回false。
     */
    boolean isSameContent(Map<String, Object> params);

    /**
     * 执行保存操作。
     */
    void doSave();

    /**
     * 数据是否已经被修改了。
     */
    boolean isDirty();

    /**
     * 返回编辑器的短标题。
     */
    String getSimpleTitle();

    /**
     * 返回编辑器所属的编辑器容器。
     */
    IEditorContainer<Container, Control, Image> getEditorContainer();

    /**
     * 返回编辑器的长标题。长标题通常显示在Shell的标题上。
     */
    String getTitle();

    /**
     * 返回编辑器的概要组建。不存在返回null。
     */
    Container getOutline();

    /**
     * 执行销毁操作。
     */
    void doDispose();

    /**
     * 返回编辑器的菜单配置。
     */
    Thing getMenuConfig();

    /**
     * 返回编辑器的工具栏配置。
     */
    Thing getCoolBarConfig();

    /**
     * 返回编辑器的状态栏配置。
     */
    Thing getStatusBarConfig();

    /**
     * 返回编辑器的Composite组件。
     */
    Container getEditor();

    /** 返回编辑器事物模型 */
    Thing getThing();

    /**
     * 返回编辑器自己的动作容器。
     */
    ActionContainer getActions();

    /**
     * 返回编辑器自己的变量上下文。
     */
    ActionContext getActionContext();

    /**
     * 返回编辑器的图标。
     */
    Image getIcon();

    /**
     * 触发自己的状态改变事件。
     *
     */
    void fireStateChanged();

    Object doAction(String name);

    Object doAction(String name, Object... params);

    ActionContainer getActionContainer();
}
