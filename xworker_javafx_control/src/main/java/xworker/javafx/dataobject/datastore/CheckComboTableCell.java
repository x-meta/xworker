package xworker.javafx.dataobject.datastore;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import org.controlsfx.control.CheckComboBox;

public class CheckComboTableCell<S, T> extends TableCell<S, T> {
    CheckComboBox<T> comboBox = new CheckComboBox<>();

    public CheckComboTableCell(){
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.setGraphic(comboBox);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if(item == null || empty){
            return;
        }


    }
}
