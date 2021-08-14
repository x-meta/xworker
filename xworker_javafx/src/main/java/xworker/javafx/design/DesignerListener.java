package xworker.javafx.design;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class DesignerListener implements EventHandler<MouseEvent> {
    @Override
    public void handle(MouseEvent event) {
        if(event.getButton() == MouseButton.MIDDLE && event.isControlDown()){
            Object source = event.getSource();
            if(source instanceof TableView){
                TableView<?> tableView = (TableView<?>) source;
                for(TableColumn<?,?> column : tableView.getColumns()){
                    System.out.println(column.getText() + ", width=" + column.getWidth());
                }
            }
        }
    }
}
