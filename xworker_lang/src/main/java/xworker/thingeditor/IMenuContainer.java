package xworker.thingeditor;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public interface IMenuContainer {
    void setEditorMenu(Thing menuConfig, ActionContext actionContext);

    void removeEditorMenu(Thing menuConfig, ActionContext actionContext);
}
