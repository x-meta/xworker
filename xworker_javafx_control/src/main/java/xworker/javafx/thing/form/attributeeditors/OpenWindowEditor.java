package xworker.javafx.thing.form.attributeeditors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.UtilString;
import xworker.javafx.thing.form.AttributeEditor;
import xworker.javafx.thing.form.ThingForm;
import xworker.javafx.util.FXCodeAssistor;

import java.util.function.Consumer;

public class OpenWindowEditor extends AttributeEditor {
    HBox hbox;
    TextField textField;
    Object value;
    Button openButton;

    public OpenWindowEditor(ThingForm thingForm, Thing attribute) {
        super(thingForm, attribute);
    }

    @Override
    public Node createEditor() {
        hbox = new HBox();
        double size = attribute.getDouble("size");
        textField = new TextField();
        if(size > 0 ){
            textField.setMaxWidth(size * 7);
        }else{
            textField.setMaxWidth(200);
        }

        HBox.setHgrow(textField, Priority.ALWAYS);
        openButton = new Button();
        openButton.setText("*");
        openButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String inputAttrs = attribute.getString("inputattrs");
                if(inputAttrs == null || "".equals(inputAttrs)){
                    inputAttrs = "xworker.swt.xworker.attributeEditor.OpenWindows";
                    //showError(shell, "属性的输入扩展属性没有设置弹出窗口的路径！");
                    //return;
                }

                String[] ws = inputAttrs.split("[|]");
                Thing winThing = World.getInstance().getThing(ws[0]);
                if(winThing == null){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.getButtonTypes().add(ButtonType.OK);
                    String headText = UtilString.getString("lang:d=窗口未设置，属性：&en=Window not set, attribute:", thingForm.getActionContext());
                    alert.setHeaderText(headText + attribute.getMetadata().getPath());
                    alert.show();
                    return;
                }

                //参数
                String param = "";
                if(ws.length >= 2){
                    param = ws[1];
                }

                ActionContext actionContext = thingForm.getActionContext();
                ActionContext parentContext = (ActionContext) actionContext.get("parentContext");
                ActionContext ac = new ActionContext();
                ac.put("attributeEditor", OpenWindowEditor.this);
                ac.put("param", param);
                ac.put("params", UtilString.getParams(param, ","));
                ac.put("parentContext", parentContext);
                ac.put("thingForm", thingForm);
                ac.put("thing", parentContext.get("thing"));

                Node node = winThing.doAction("create", ac);
                Dialog<ButtonType> dialog  = new Dialog<>();
                dialog.setTitle(attribute.getMetadata().getLabel());
                dialog.getDialogPane().setContent(node);
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                ActionContainer actionContainer = ac.getObject("actions");
                actionContainer.doAction("setValue", ac, "value", getValue());
                dialog.showAndWait().ifPresent(new Consumer<ButtonType>() {
                    @Override
                    public void accept(ButtonType buttonType) {
                        if(buttonType == ButtonType.OK){
                            Object value = actionContainer.doAction("getValue", ac);
                            setValue(value);
                        }
                    }
                });
            }
        });

        if(attribute.getBoolean("readOnly")){
            textField.setEditable(false);
        }else{
            textField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    thingForm.modified(OpenWindowEditor.this);
                }
            });

            FXCodeAssistor assistor = FXCodeAssistor.bind(thingForm.getThing(), textField, thingForm.getActionContext());
            assistor.setThingFactory(new Callback<FXCodeAssistor, Thing>() {
                @Override
                public Thing call(FXCodeAssistor param) {
                    return thingForm.getThing();
                }
            });
        }
        hbox.setSpacing(3);
        hbox.getChildren().addAll(textField, openButton);
        return hbox;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;

        if(value == null){
            textField.setText("");
        }else{
            textField.setText(String.valueOf(value));
        }
    }

    @Override
    public Object getValue() {
        if(textField.isEditable()){
            return textField.getText();
        }else {
            return value;
        }
    }

    public Object getRawValue(){
        return value;
    }

    public void setRawValue(Object value){
        this.value = value;
    }

    public TextField getTextField() {
        return textField;
    }
}
