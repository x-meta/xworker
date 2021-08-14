package xworker.workbench;

public interface IEditorListener<Container, Control, Image> {
    /**
     * 当创建了编辑器时触发。
     */
    void onCreated(IEditorContainer<Container, Control, Image> editorContainer, IEditor<Container, Control, Image> editor);

    /**
     * 当一个编辑器处于激活状态时触发。
     */
    void onActive(IEditorContainer<Container, Control, Image> editorContainer, IEditor<Container, Control, Image> editor);

    /**
     * 当一个编辑器销毁后触发。
     */
    void onDisposed(IEditorContainer<Container, Control, Image> editorContainer, IEditor<Container, Control, Image> editor);

    /**
     * 当扁编辑器的状态修改后触发。可以用来监听编辑器是否有脏数据。
     */
    void stateChanged(IEditorContainer<Container, Control, Image> editorContainer, IEditor<Container, Control, Image> editor);
}
