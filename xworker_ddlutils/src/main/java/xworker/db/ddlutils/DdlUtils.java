package xworker.db.ddlutils;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.lang.executor.Executor;

import javax.sql.DataSource;

public class DdlUtils {
    private static final String TAG = DdlUtils.class.getName();

    public static void create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Boolean continueOnError = self.doAction("isContinueOnError", actionContext);
        DataSource ds = DatabaseDdlUtil.getDataSource(self, actionContext);

        actionContext.peek().put("dataSource", ds);

        for(Thing child : self.getChilds("DdlUtil")){
            try{
                child.doAction("create", actionContext);
            }catch(Exception e){
                Executor.error(TAG, "Create error, utils=" + self.getMetadata().getName(), e);
                if(!continueOnError){
                    break;
                }
            }
        }
    }

    public static void alter(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Boolean continueOnError = self.doAction("isContinueOnError", actionContext);
        DataSource ds = DatabaseDdlUtil.getDataSource(self, actionContext);

        actionContext.peek().put("dataSource", ds);

        for(Thing child : self.getChilds("DdlUtil")){
            try{
                child.doAction("alter", actionContext);
            }catch(Exception e){
                Executor.error(TAG, "Alter error, utils=" + self.getMetadata().getName(), e);
                if(!continueOnError){
                    break;
                }
            }
        }
    }

    public static void drop(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Boolean continueOnError = self.doAction("isContinueOnError", actionContext);
        DataSource ds = DatabaseDdlUtil.getDataSource(self, actionContext);

        actionContext.peek().put("dataSource", ds);

        for(Thing child : self.getChilds("DdlUtil")){
            try{
                child.doAction("drop", actionContext);
            }catch(Exception e){
                Executor.error(TAG, "Drop error, utils=" + self.getMetadata().getName(), e);
                if(!continueOnError){
                    break;
                }
            }
        }
    }
}
