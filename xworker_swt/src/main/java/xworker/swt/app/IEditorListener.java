package xworker.swt.app;

public interface IEditorListener {
	/**
	 * 当创建了编辑器时触发。
	 * 
	 * @param editorContainer
	 * @param editor
	 */
	public void onCreated(IEditorContainer editorContainer, IEditor editor);

	/**
	 * 当一个编辑器处于激活状态时触发。
	 * 
	 * @param editorContainer
	 * @param editor
	 */
	public void onActive(IEditorContainer editorContainer, IEditor editor);
	
	/**
	 * 当一个编辑器销毁后触发。
	 * 
	 * @param editorContainer
	 * @param editor
	 */
	public void onDisposed(IEditorContainer editorContainer, IEditor editor);
	
	/**
	 * 当扁编辑器的状态修改后触发。可以用来监听编辑器是否有脏数据。
	 * 
	 * @param editorContainer
	 * @param editor
	 */
	public void stateChanged(IEditorContainer editorContainer, IEditor editor);
}
