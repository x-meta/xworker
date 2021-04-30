package xworker.javafx.control.cell;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.TableCell;
import javafx.util.StringConverter;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.control.TableCellActions;

public class NodeTableCell <S,T> extends TableCell<S,T> {
    Node labelNode;
    Node editNode;
    Thing thing;
    ActionContext actionContext;
    ActionContext parentContext;

    public NodeTableCell(Thing thing, ActionContext parentContext){
        this.thing = thing;
        this.actionContext = new ActionContext();
        this.actionContext.put("parentContext", parentContext);
        this.parentContext = parentContext;
        this.actionContext.put("cell", this);

        Thing labelNodeThing = thing.doAction("getLabelNode", actionContext);
        if(labelNodeThing != null){
            labelNode = labelNodeThing.doAction("create", actionContext);
        }

        Thing editNodeThing = thing.doAction("getEditNode", actionContext);
        if(editNodeThing != null){
            editNode = editNodeThing.doAction("create", actionContext);
        }
    }

    @Override public void startEdit() {
        if (! isEditable() || ! getTableView().isEditable() || ! getTableColumn().isEditable()) {
            return;
        }

        if(editNode == null){
            return;
        }

        super.startEdit();
        setText(null);
        setGraphic(editNode);

        thing.doAction("startEdit", parentContext, "cell", this);
    }

    @Override public void cancelEdit() {
        super.cancelEdit();

        if(labelNode != null){
            setText(null);
            setGraphic(labelNode);
        }else {
            if(getConverter() == null){
                setText(getItem() == null ? null : getItem().toString());
            }
            setGraphic(null);
        }

        thing.doAction("cancelEdit", parentContext, "cell", this);
    }

    @Override public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (isEmpty()) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                setText(null);
                if (editNode != null) {
                    setGraphic(editNode);
                }
            } else {
                if(labelNode != null){
                    setText(null);
                    setGraphic(labelNode);
                }else{
                    setText(getItemText(this, converter.get()));
                    setGraphic(null);
                }
            }
        }

        thing.doAction("updateItem", parentContext, "item", item, "empty", empty, "cell", this);
    }

    private static <T> String getItemText(Cell<T> cell, StringConverter<T> converter) {
        return converter == null ?
                cell.getItem() == null ? "" : cell.getItem().toString() :
                converter.toString(cell.getItem());
    }

    private ObjectProperty<StringConverter<T>> converter =
            new SimpleObjectProperty<StringConverter<T>>(this, "converter");

    /**
     * The {@link StringConverter} property.
     */
    public final ObjectProperty<StringConverter<T>> converterProperty() {
        return converter;
    }

    /**
     * Sets the {@link StringConverter} to be used in this cell.
     */
    public final void setConverter(StringConverter<T> value) {
        converterProperty().set(value);
    }

    /**
     * Returns the {@link StringConverter} used in this cell.
     */
    public final StringConverter<T> getConverter() {
        return converterProperty().get();
    }

    public ActionContext getActionContext(){
        return actionContext;
    }

    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        NodeTableCell cell = new NodeTableCell(self, actionContext);
        TableCellActions.init(cell, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), cell);

        actionContext.peek().put("parent", cell);
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof  StringConverter){
                cell.setConverter((StringConverter) obj);
            }
        }

        return cell;

    }
}
