package xworker.db.ddlutils;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.model.Database;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import javax.sql.DataSource;

public class DatabaseDdlUtil {
    public static DataSource getDataSource(Thing self, ActionContext actionContext){
        DataSource ds = self.doAction("getDataSource", actionContext);
        if(ds == null){
            Thing dataSourceThing = self.doAction("getDataSourceThing", actionContext);
            if(dataSourceThing != null){
                ds = dataSourceThing.doAction("getDataSource", actionContext);
            }
        }
        if(ds == null){
            throw new ActionException("jdbc dataSource is null, path=" + self.getMetadata().getPath());
        }

        return ds;
    }

    public static void create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Boolean dropTablesFirst = self.doAction("isDropTablesFirst", actionContext);
        Boolean continueOnError = self.doAction("isContinueOnError", actionContext);
        Thing database = self.doAction("getDatabase", actionContext);
        DataSource ds = getDataSource(self, actionContext);

        Database db = (Database) database.doAction("getDatabase", actionContext);
        if(db == null){
            throw new ActionException("database is null, path=" + self.getMetadata().getPath());
        }

        Platform platform = DbUtilsActions.getPlatform(self, ds);
        platform.createTables(db, dropTablesFirst, continueOnError);
    }

    public static void alter(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Boolean continueOnError = self.doAction("isContinueOnError", actionContext);
        Thing database = self.doAction("getDatabase", actionContext);
        DataSource ds = getDataSource(self, actionContext);

        if(ds == null){
            throw new ActionException("jdbc dataSource is null, path=" + self.getMetadata().getPath());
        }
        Database db = (Database) database.doAction("getDatabase", actionContext);
        if(db == null){
            throw new ActionException("database is null, path=" + self.getMetadata().getPath());
        }

        Platform platform = DbUtilsActions.getPlatform(self, ds);
        platform.alterTables(db, continueOnError);
    }

    public static void drop(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Boolean continueOnError = self.doAction("isContinueOnError", actionContext);
        Thing database = self.doAction("getDatabase", actionContext);
        DataSource ds = getDataSource(self, actionContext);

        if(ds == null){
            throw new ActionException("jdbc dataSource is null, path=" + self.getMetadata().getPath());
        }
        Database db = (Database) database.doAction("getDatabase", actionContext);
        if(db == null){
            throw new ActionException("database is null, path=" + self.getMetadata().getPath());
        }

        Platform platform = DbUtilsActions.getPlatform(self, ds);
        platform.dropTables(db, continueOnError);
    }
}
