/*path:xworker.swt.xworker.UserTaskTable/@actions/@create*/
package xworker.swt.xworker.UserTaskTable.p_556836535;

import org.xmeta.Thing;
import org.xmeta.ActionContext;

//原型
def thing = world.getThing("xworker.swt.xworker.prototype.UserTaskTableShell/@taskTable");
def ac = new ActionContext();
ac.put("parent", parent);
ac.put("thing", self);
ac.put("parentContext", actionContext);

//创建SWT对象
def table = thing.doAction("create", ac);
table.setData("tableThing", self);
table.setData("actionContext", actionContext);

ac.peek().put("parent", table);
for(child in self.getChilds()){
    child.doAction("create", ac);
}

actionContext.g().put(self.getMetadata().getName(), ac);
return table;