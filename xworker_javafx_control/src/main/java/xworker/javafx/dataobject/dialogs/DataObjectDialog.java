package xworker.javafx.dataobject.dialogs;

import javafx.scene.control.Dialog;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.javafx.dataobject.DataObjectForm;
import xworker.javafx.dataobject.DataObjectFormType;


public class DataObjectDialog<T> extends Dialog<T> {
    DataObject dataObject;
    DataObjectForm dataObjectForm;

    public DataObjectDialog(){
        super();
    }

    public DataObjectDialog(DataObject dataObject, DataObjectFormType type){
        super();

        setDataObject(dataObject, type);
    }

    public DataObjectDialog(Thing dataObject, DataObjectFormType type){
        super();

        setDataObject(new DataObject(dataObject), type);
    }

    public void setDataObject(DataObject dataObject, DataObjectFormType type){
        this.dataObject = dataObject;
        dataObjectForm= new DataObjectForm();
        dataObjectForm.setType(type);
        dataObjectForm.setDataObject(dataObject);
        this.getDialogPane().setHeader(dataObjectForm.getFormNode());
    }

    public DataObject getDataObject(){
        return dataObject;
    }

    public DataObjectForm getDataObjectForm(){
        return dataObjectForm;
    }

}
