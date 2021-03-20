package xworker.javafx.dataobject.datastore;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
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
           return new TextFieldTableCell<DataObject, Object>();
        }else if("label".equals(inputtype)){
            TextFieldTableCell<DataObject, Object> cell = new TextFieldTableCell<>();
            cell.setEditable(false);
            return cell;
        }else if("select".equals(inputtype)){
            ChoiceBoxTableCell<DataObject, Thing> cell = new ChoiceBoxTableCell<>();
            List<Thing> values = attribute.getAllChilds("value");
            cell.setConverter(new ThingValueStringConverter(values));
            cell.getItems().addAll(values);
        }else if("inputSelect".equals(inputtype)){
            ComboBoxTableCell<DataObject, Thing> cell = new ComboBoxTableCell<>();
            cell.setEditable(true);
            List<Thing> values = attribute.getAllChilds("value");
            cell.setConverter(new ThingValueStringConverter(values));
            cell.getItems().addAll(values);
        }else if("truefalse".equals(inputtype) || "checkBox".equals(inputtype)){
            CheckBoxTableCell<DataObject, Object> cell = new CheckBoxTableCell<>();
            return cell;
        }else if("truefalseselect".equals(inputtype)){
            ChoiceBoxTableCell<DataObject, Object> cell = new ChoiceBoxTableCell<>();
            cell.getItems().addAll("", "true", "false");
            return cell;
        }


        return new TextFieldTableCell<DataObject, Object>();
    }
}
