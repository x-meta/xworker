package xworker.dataObject.annotation;

import org.xmeta.ActionContext;
import xworker.dataObject.DataObject;

public class BeanDataObjectActions {


    public static DataObject doCreate(ActionContext actionContext){
        DataObject theData = actionContext.getObject("theData");

        BeanDataObjectHelper helper = BeanDataObjectHelper.getHelper(theData.getMetadata().getDescriptor());

        return helper.create(theData);
    }
}
