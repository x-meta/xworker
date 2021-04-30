package xworker.javafx.thing.form.attributeeditors;

import javafx.application.Platform;
import javafx.scene.Node;
import org.controlsfx.control.CheckComboBox;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataStore;
import xworker.dataObject.DataStoreListener;
import xworker.javafx.thing.form.AttributeEditor;
import xworker.javafx.thing.form.ThingForm;
import xworker.javafx.util.DataObjectStringConverter;

public class MultiComboDataStoreEditor extends AttributeEditor {
    CheckComboBox<DataObject> comboBox;
    DataStore dataStore;
    Object value;

    public MultiComboDataStoreEditor(ThingForm thingForm, Thing attribute, DataStore dataStore) {
        super(thingForm, attribute);

        this.dataStore = dataStore;
    }

    @Override
    public Node createEditor() {
        comboBox = new CheckComboBox<>();
        comboBox.setConverter(new DataObjectStringConverter(dataStore.getDataObject()));
        dataStore.addListener(new DataStoreListener() {
            @Override
            public void onReconfig(DataStore dataStore, Thing dataObject) {
            }

            @Override
            public void onLoaded(DataStore dataStore) {
                comboBox.getItems().clear();
                comboBox.getItems().addAll(dataStore.getDatas());
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        setValue(value);
                    }
                });
            }

            @Override
            public void onChanged(DataStore dataStore) {
                onLoaded(dataStore);
            }
        });
        if(dataStore.getDatas().size() == 0){
            dataStore.reload();
        }

        return comboBox;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;

        comboBox.getCheckModel().clearChecks();
        if(value instanceof Object[]){
            Object[] values = (Object[]) value;
            for(DataObject dataObject : dataStore.getDatas()){
                for(Object v : values){
                    if(dataObject.equalsByKey(v)){
                        comboBox.getCheckModel().check(dataObject);
                    }
                }
            }
        }else if(value instanceof String){
            String[] values = ((String) value).split("[,]");
            for(DataObject dataObject : dataStore.getDatas()){
                for(Object v : values){
                    if(dataObject.equalsByKey(v)){
                        comboBox.getCheckModel().check(dataObject);
                    }
                }
            }
        }else{
            for(DataObject dataObject : dataStore.getDatas()){
                if(dataObject.equalsByKey(value)){
                    comboBox.getCheckModel().check(dataObject);
                }
            }
        }
    }

    @Override
    public Object getValue() {
        String text = null;
        for(DataObject value : comboBox.getCheckModel().getCheckedItems()){
            String label = String.valueOf(value.getKeyValue());
            if(text == null){
                text = label;
            }else{
                text = text + "," + label;
            }
        }

        return text;
    }
}
