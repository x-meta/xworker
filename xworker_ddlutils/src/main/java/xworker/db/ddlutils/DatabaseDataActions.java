package xworker.db.ddlutils;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.io.DatabaseDataIO;
import org.apache.ddlutils.model.Database;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.util.UtilAction;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;

public class DatabaseDataActions {
    public static void writeDataToDatabase(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing database = self.doAction("getDatabase", actionContext);

        String[] inputXMLFiles = self.doAction("getInputXMLFiles", actionContext);
        Boolean useBatchMode = self.doAction("isUseBatchMode", actionContext);
        Integer batchSize = self.doAction("getBatchSize", actionContext);
        Boolean ensureFKOrder = self.doAction("isEnsureFKOrder", actionContext);
        Boolean failOnError = self.doAction("isFailOnError", actionContext);

        DataSource ds = DatabaseDdlUtil.getDataSource(self, actionContext);
        Platform platform = DbUtilsActions.getPlatform(self, ds);
        Database database1 = database.doAction("getDatabase", actionContext);

        String xmlData = self.doAction("getXmlDatas", actionContext);
        if(xmlData != null && !xmlData.isEmpty()){
            String path = UtilAction.getActionCodeFilePath(self, "xml");
            File file = new File(path);
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            try {
                //保存xml文件
                FileOutputStream fout = new FileOutputStream(path);
                fout.write(xmlData.getBytes());
                fout.close();

                if(inputXMLFiles == null){
                    inputXMLFiles = new String[]{path};
                }else{
                    String[] files = new String[inputXMLFiles.length + 1];
                    System.arraycopy(inputXMLFiles, 0, files, 0, inputXMLFiles.length);
                    files[files.length - 1] = path;
                    inputXMLFiles = files;
                }
            }catch(Exception e){
            }
        }

        DatabaseDataIO io = new DatabaseDataIO();
        io.setUseBatchMode(useBatchMode);
        if(useBatchMode){
            io.setBatchSize(batchSize);
        }
        io.setEnsureFKOrder(ensureFKOrder);
        io.setFailOnError(failOnError);

        io.writeDataToDatabase(platform, database1, inputXMLFiles);
    }

    public static void writeDataToXML(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing database = self.doAction("getDatabase", actionContext);
        String outputXMLFile = self.doAction("getOutputXMLFile", actionContext);
        Boolean useBatchMode = self.doAction("isUseBatchMode", actionContext);
        Integer batchSize = self.doAction("getBatchSize", actionContext);
        Boolean ensureFKOrder = self.doAction("isEnsureFKOrder", actionContext);
        Boolean failOnError = self.doAction("isFailOnError", actionContext);
        String outputXMLEncoding = self.doAction("getOutputXMLEncoding", actionContext);

        DataSource ds = DatabaseDdlUtil.getDataSource(self, actionContext);
        Platform platform = DbUtilsActions.getPlatform(self, ds);
        Database database1 = database.doAction("getDatabase", actionContext);

        DatabaseDataIO io = new DatabaseDataIO();
        io.setUseBatchMode(useBatchMode);
        if(useBatchMode){
            io.setBatchSize(batchSize);
        }
        io.setEnsureFKOrder(ensureFKOrder);
        io.setFailOnError(failOnError);

        io.writeDataToXML(platform, database1, outputXMLFile, outputXMLEncoding);
    }
}
