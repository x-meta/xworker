package xworker.thingeditor;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;
import xworker.lang.executor.ExecutorService;

import java.util.List;
import java.util.Map;

public interface IWorkbench<CONTAINER, CONTROL, IMAGE> {
    Thing getThing();

    ActionContainer getActions();

    ActionContext getActionContext();

    IEditorContainer<CONTAINER, CONTROL, IMAGE> getEditorContainer();

    ExecutorService getLogService();

    ExecutorService getRequestService();

    IEditor<CONTAINER, CONTROL, IMAGE> openEditor(String id, Thing editor, Map<String, Object> params);

    IView<CONTAINER, CONTROL> openView(String id, final Thing view, final String type);

    IView<CONTAINER, CONTROL> openView(String id, final Thing view, final String type, final boolean closeable, final Map<String, Object> params);

    IView<CONTAINER, CONTROL> getView(String id);

    IEditor<CONTAINER, CONTROL, IMAGE> getEditor(String id);

    boolean exit();

    void save();

    void saveAll();

    boolean isDirty();

    IEditor<CONTAINER, CONTROL, IMAGE> getActiveEditor();

    List<IEditor<CONTAINER, CONTROL, IMAGE>> getEditors();

    List<IView<CONTAINER, CONTROL>> getViews();

}
