package xworker.thingeditor;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;

import java.util.Map;

public interface IView <CONTAINER, CONTROL>{
    void create(CONTAINER parent, ActionContext parentContext);

    void showView();

    String getId();

    Thing getThing();

    ActionContext getActionContext();

    Map<String, Object> getParams();

    CONTAINER getParent();

    ActionContext getParentContext();

    CONTROL getControl();

    ActionContainer getActionContainer();

    Object doAction(String name);

    Object doAction(String name, Object ... params);
}
