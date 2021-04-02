package xworker.javafx.util;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Window;
import javafx.util.Callback;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.util.codeassist.CodeAssistor;
import xworker.util.codeassist.CodeAssitContent;

import java.util.ArrayList;
import java.util.List;

public class FXCodeAssistor implements EventHandler<KeyEvent>{
    CodeAssistor codeAssistor;
    Thing thing;
    TextField textField;
    TextArea textArea;
    ActionContext actionContext;

    Popup popup;
    TextField searchText = new TextField();
    ListView<CodeAssitContent> listView = new ListView<>();
    List<CodeAssitContent> contents;

    private FXCodeAssistor(){
        popup = new Popup();

        // Make the popup appear to the right of the caret
        popup.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_BOTTOM_LEFT);
        // Make sure its position gets corrected to stay on screen if we go out of screen
        popup.setAutoFix(true);

        VBox vBox = new VBox();
        vBox.setFillWidth(true);
        vBox.getChildren().add(searchText);
        vBox.getChildren().add(listView);
        VBox.setVgrow(listView, Priority.ALWAYS);
        listView.setPrefWidth(400);
        popup.setWidth(400);
        popup.getContent().add(vBox);
        listView.setCellFactory(new Callback<ListView<CodeAssitContent>, ListCell<CodeAssitContent>>() {
            @Override
            public ListCell<CodeAssitContent> call(ListView<CodeAssitContent> param) {
                return new TextFieldListCell<CodeAssitContent>(){
                    @Override
                    public void updateItem(CodeAssitContent item, boolean empty) {
                        super.updateItem(item, empty);

                        if(item == null || empty){
                            return;
                        }

                        String text = item.getLabel();
                        if(text == null || text.isEmpty()){
                            text = item.getValue();
                        }
                        setText(text);
                    }
                };
            }
        });
        searchText.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.DOWN){
                    listView.getSelectionModel().selectNext();
                    listView.scrollTo(listView.getSelectionModel().getSelectedItem());
                }else if(event.getCode() == KeyCode.UP){
                    listView.getSelectionModel().selectPrevious();
                    listView.scrollTo(listView.getSelectionModel().getSelectedItem());
                }else if(event.getCode() == KeyCode.ESCAPE){
                    popup.hide();
                }
            }
        });
        searchText.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CodeAssitContent content = listView.getSelectionModel().getSelectedItem();
                if(content != null){
                    if(textField != null){
                        textField.insertText(textField.getCaretPosition(), content.getValue());
                    }else{
                        textArea.insertText(textArea.getCaretPosition(), content.getValue());
                    }
                }

                popup.hide();
            }
        });
        searchText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                searchContents(newValue);
            }
        });
    }

    public String getText(){
        if(textField != null){
            return textField.getText();
        }else{
            return textArea.getText();
        }
    }

    public int getCaretPosition(){
        if(textField != null){
            return textField.getCaretPosition();
        }else{
            return textArea.getCaretPosition();
        }
    }

    public Thing getThing(){
        Thing th = null;
        Callback<FXCodeAssistor, Thing> thingFactory = getThingFactory();
        if(thingFactory != null){
            return thingFactory.call(this);
        }else{
            return thing;
        }
    }

    @Override
    public void handle(KeyEvent event) {
        if(event.getCode() == KeyCode.PERIOD){
            contents = codeAssistor.getClassContents(getThing(), getText(), getCaretPosition(), actionContext);
            listView.getItems().clear();
            listView.getItems().addAll(contents);
            searchText.setText("");
            if(contents != null && contents.size() > 0) {
                popup.show(getOwner());
            }
        }else if(event.isAltDown() && event.getCode() == KeyCode.H){
            String text = getText();
            int index = getCaretPosition();
            if(index > 0){
                if(text.charAt(index - 1) == '.'){
                    contents = codeAssistor.getClassContents(getThing(), text, index, actionContext);
                }else{
                    contents = codeAssistor.getAssistContents(getThing(), text, index, actionContext);
                }
            }else {
                contents = codeAssistor.getAssistContents(getThing(), text, index, actionContext);
            }

            if(contents != null && contents.size() > 0) {
                searchText.setText(codeAssistor.getCurrentWord(text, index));

                searchContents(searchText.getText());
                popup.show(getOwner());
            }
        }
    }

    public void searchContents(String key){
        if(contents == null){
            return;
        }

        List<CodeAssitContent> list = new ArrayList<>();
        for(CodeAssitContent content : contents){
            if(key == null || key.isEmpty()){
                list.add(content);
            }else{
                boolean ok = true;
                String value = content.getValue().toLowerCase();
                key = key.toLowerCase();
                for(String k : key.split("[ ]")){
                    if(!k.isEmpty() && !value.contains(k)){
                        ok = false;
                        break;
                    }
                }
                if(ok){
                    list.add(content);
                }
            }
        }

        listView.getItems().clear();
        listView.getItems().addAll(list);

    }

    public Window getOwner(){
        if(textField != null){
            return textField.getScene().getWindow();
        }else{
            return textArea.getScene().getWindow();
        }
    }

    private final ObjectProperty<Callback<FXCodeAssistor, Thing>> thingFactory = new SimpleObjectProperty<>();
    public final void setThingFactory(Callback<FXCodeAssistor, Thing> factory){
        thingFactory.set(factory);
    }
    public final Callback<FXCodeAssistor, Thing> getThingFactory(){
        return thingFactory.get();
    }
    public final  ObjectProperty<Callback<FXCodeAssistor, Thing>> thingFactory(){
        return thingFactory;
    }

    public static FXCodeAssistor bind(Thing thing, TextField textField, ActionContext actionContext){
        FXCodeAssistor assistor = new FXCodeAssistor();
        assistor.codeAssistor = new CodeAssistor(actionContext);
        assistor.thing = thing;
        assistor.textField = textField;
        assistor.actionContext = actionContext;
        textField.addEventHandler(KeyEvent.KEY_PRESSED, assistor);
        return assistor;
    }

    public static FXCodeAssistor bind(Thing thing, TextArea textArea, ActionContext actionContext){
        FXCodeAssistor assistor = new FXCodeAssistor();
        assistor.codeAssistor = new CodeAssistor(actionContext);
        assistor.thing = thing;
        assistor.textArea = textArea;
        assistor.actionContext = actionContext;
        textArea.addEventHandler(KeyEvent.KEY_PRESSED, assistor);
        return assistor;
    }

}
