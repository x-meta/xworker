package xworker.javafx.thing.form;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import org.xmeta.Thing;

public abstract class AttributeEditor {
    protected Thing attribute;
    protected Property<Object> valueProperty;
    protected ThingForm thingForm;

    public AttributeEditor(ThingForm thingForm, Thing attribute, Property<Object> valueProperty){
        this.thingForm = thingForm;
        this.attribute = attribute;
        this.valueProperty = valueProperty;
        this.valueProperty.addListener(new ChangeListener<Object>() {

            @Override
            public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {

            }
        });
    }

    /**
     * 返回编辑器对应的属性名。
     *
     * @return
     */
    public String getName(){
        return attribute.getMetadata().getName();
    }

    /**
     * 创建编辑节点。
     */
    public abstract Node createEditor();

    public boolean hasLabel(){
        return attribute.getBoolean("showLabel");
    }

    /**
     * 创建标签节点。
     *
     * @return
     */
    public Node createLabel(){
        Label label = new Label();
        //label.setMinWidth(100);
        String text = attribute.getMetadata().getLabel() + ":";
        label.setText(text);
        label.setEllipsisString(text);
        label.setTextAlignment(TextAlignment.RIGHT);
        label.setAlignment(Pos.TOP_RIGHT);
        GridPane.setHalignment(label, HPos.RIGHT);
        GridPane.setHgrow(label, Priority.NEVER);
        return label;
    }

    /**
     * 设置值到编辑控件。
     *
     * @param value
     */
    public abstract void setValue(Object value);

    /**
     * 从编辑控件中获取值。
     *
     * @return
     */
    public abstract  Object getValue();
}