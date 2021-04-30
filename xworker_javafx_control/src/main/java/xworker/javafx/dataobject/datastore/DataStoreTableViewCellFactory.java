package xworker.javafx.dataobject.datastore;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataStore;
import xworker.javafx.dataobject.DataObjectAttributeStringConverter;
import xworker.javafx.util.DataObjectStringConverter;
import xworker.javafx.util.ThingValueStringConverter;

import java.util.List;

public class DataStoreTableViewCellFactory implements Callback<TableColumn<DataObject, Object>, TableCell<DataObject, Object>> {
    private static DataStoreTableViewCellFactory instance = new DataStoreTableViewCellFactory();
    public static DataStoreTableViewCellFactory getInstance(){
        return instance;
    }

    @Override
    public TableCell<DataObject, Object> call(TableColumn<DataObject, Object> param) {
        Thing attribute = (Thing) param.getUserData();
        String inputtype = attribute.getStringBlankAsNull("inputtype");
        if("text".equals(inputtype)){
            return createDefault(attribute);
        }else if("label".equals(inputtype)){
            TextFieldTableCell<DataObject, Object> cell = new TextFieldTableCell<>();
            cell.setEditable(false);
            return cell;
        }else if("select".equals(inputtype) || "inputSelect".equals(inputtype)){
            ChoiceBoxTableCell cell;
            DataStore dataStore = (DataStore) param.getProperties().get(DataStoreTableView.DATASTORE);
            if(dataStore != null){
                cell = new ChoiceBoxTableCell(){
                    @Override
                    public void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);

                        if(item == null || empty){
                            return;
                        }

                        this.setItem(item);
                    }
                };
                new DataStoreChoiceBoxTableCell(dataStore, cell);
                cell.setConverter(new DataObjectStringConverter(dataStore.getDataObject(), dataStore.getLabelField()));
            }else {

                List<Thing> values = (List<Thing>) param.getProperties().get(DataStoreTableView.VALUES);
                if(values != null){
                    cell = new ChoiceBoxTableCell<>();
                    cell.setConverter(new ThingValueStringConverter(values));
                    cell.getItems().addAll(values);
                }else{
                    return createDefault(attribute);
                }
            }

            if("inputSelect".equals(inputtype)){
                cell.setEditable(true);
            }
            return cell;
        }else if("truefalse".equals(inputtype) || "checkBox".equals(inputtype)){
            CheckBoxTableCell<DataObject, Object> cell = new CheckBoxTableCell<>();
            cell.setConverter(new DataObjectAttributeStringConverter(attribute));
            return cell;
        }else if("truefalseselect".equals(inputtype)){
            ChoiceBoxTableCell<DataObject, Object> cell = new ChoiceBoxTableCell<>();
            cell.setConverter(new DataObjectAttributeStringConverter(attribute));
            cell.getItems().addAll("", "true", "false");
            return cell;
        }

        return createDefault(attribute);
    }

    private TableCell<DataObject, Object> createDefault(Thing attribute){
        TextFieldTableCell<xworker.dataObject.DataObject, java.lang.Object> cell =  new TextFieldTableCell<xworker.dataObject.DataObject, java.lang.Object>();
        cell.setConverter(new DataObjectAttributeStringConverter(attribute));
        return cell;
    }
}
