package xworker.javafx.thing.form;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;
import xworker.javafx.beans.property.MapAdapter;
import xworker.thingeditor.ThingAttributeGroup;

import java.util.*;

public class ThingForm {
    Thing formThing;
    ActionContext actionContext;
    Object changeSource = null;
    int column;
    Thing editThing;
    Thing editThingDescriptor;

    /**
     * 用于保存表单的值。
     */
    //MapAdapter<String, Object> values = new MapAdapter<>();

    /**
     * 属性编辑器列表。
     */
    List<AttributeEditor> attributeEditors = new ArrayList<>();

    /**
     * yongy
     */
    Node formNode;
    VBox rootNode;

    SimpleBooleanProperty modifiedProperty = new SimpleBooleanProperty();

    public ThingForm(){
        this(null, new ActionContext());
    }

    public ThingForm(ActionContext actionContext){
        this(null, actionContext);
    }

    public ThingForm(Thing thing, ActionContext actionContext){
        rootNode = new VBox();

        setFormThing(thing, actionContext);

    }

    public boolean isModified(){
        return modifiedProperty.get();
    }

    public SimpleBooleanProperty modifiedProperty(){
        return modifiedProperty;
    }

    /**
     * 获取表单的值到Map中。
     *
     * @return
     */
    public Map<String, Object> getValues(){
        Map<String, Object> values = new HashMap<>();
        for(AttributeEditor editor : attributeEditors){
            values.put(editor.getName(), editor.getValue());
        }

        return values;
    }

    /**
     * 设置表单的值。
     *
     * @param values
     */
    public void setValues(Map<String, Object> values){
        try {
            changeSource = this;
            if (values == null) {
                values = Collections.emptyMap();
            }

            for (AttributeEditor editor : attributeEditors) {
                String name = editor.getName();
                editor.setValue(values.get(name));
            }
        }finally{
            changeSource = false;
        }
    }

    /**
     * 设置部分值。先从表单中取值，然后再覆盖值，最后设置到表单中。
     *
     * @param values
     */
    public void setPartialValues(Map<String, Object> values){
        Map<String, Object> oldValues = getValues();
        oldValues.putAll(values);

        setValues(oldValues);
    }

    /**
     * 校验表单，如果校验失败返回false，否则返回true。
     *
     * @return
     */
    public boolean validate(){
        return true;
    }

    /**
     * 返回表单的JavaFX节点，如果不存在创建。
     *
     * @return 表单界面节点
     */
    public Node getFormNode(){
        return rootNode;
    }

    /**
     * 设置编辑一个新的模型。
     * @param thing
     */
    public void setThing(Thing thing){
        this.editThing = thing;
        if(thing != null){
            setDescriptor(thing.getDescriptor());

            setValues(thing.getAttributes());
        }else{
            setDescriptor(new Thing());
        }
    }

    /**
     * 设置要编辑器的模型，并指定编辑该模型的描述者。
     *
     * @param thing
     * @param editThingDescriptor
     */
    public void setThing(Thing thing, Thing editThingDescriptor){
        this.editThing = thing;
        if(editThingDescriptor != null) {
            setDescriptor(editThingDescriptor);
        }else{
            setDescriptor(thing.getDescriptor());
        }
        setValues(thing.getAttributes());
    }

    /**
     * 设置新的描述者。如果需要有默认初始值，setThing(new Thing(descriptor))。
     *
     * @param descriptor
     */
    public void setDescriptor(Thing descriptor){
        //this.values.clear();
        this.editThingDescriptor = descriptor;
        int cols = column;
        if(cols < 1){
            cols = descriptor.getInt("editCols");
            if(cols < 1){
                cols = 1;
            }
        }
        //获取属性定义，并且去重
        List<Thing> attributes = new ArrayList<>();
        Map<String, String> context = new HashMap<>();
        for(Thing attr : descriptor.getAllChilds("attribute")){
            String name = attr.getMetadata().getName();
            if(context.get(name) == null){
                attributes.add(attr);
                context.put(name, name);
            }
        }

        //分组和创建界面
        List<ThingAttributeGroup> groups = ThingAttributeGroup.parseGroups(attributes);
        if(groups.size() == 1 && groups.get(0).getName().equals("")){
            //只有一个默认组，不需要创建TabPane
            formNode = createSingleFormPane(groups.get(0).getAttributes(), attributeEditors, cols);
        }else{
            TabPane tabPane = new TabPane();
            for(ThingAttributeGroup group : groups){
                Tab tab = new Tab();
                tab.setClosable(false);
                if("".equals(group.getName())){
                    tab.setText(UtilString.getString("lang:d=基础属性&en=Basic", actionContext));
                }else{
                    tab.setText(group.getName());
                }

                tab.setContent(createSingleFormPane(group.getAttributes(), attributeEditors, cols));
                tabPane.getTabs().add(tab);
            }
            formNode = tabPane;
        }

        rootNode.getChildren().clear();
        VBox.setVgrow(formNode, Priority.ALWAYS);
        rootNode.getChildren().add(formNode);
    }

    /**
     * 创建一个表单，并把
     *
     * @param attributes
     * @return
     */
    private Node createSingleFormPane(List<Thing> attributes,  List<AttributeEditor> attributeEditors, int column){
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);;

        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        FormLayout<Thing> layout  = new FormLayout<>(column, attributes.size() * 10);
        for(Thing attribute : attributes) {
            layout.add(attribute.getInt("colspan"), attribute.getInt("rowspan"), attribute);
        }
        for(FormLayoutData<Thing> layoutData : layout.getLayoutDatas()){
            Thing attribute = layoutData.getObject();

            AttributeEditor editor = AttributeEditorFactory.createAttributeEditor(this, attribute);

            if(editor != null){
                int columnIndex = layoutData.getColumnIndex() * 2;
                int rowIndex = layoutData.getRowIndex();
                int colspan = layoutData.getColspan();
                int rowspan = layoutData.getRowspan();
                //System.out.println("" + columnIndex + ":" + rowIndex + ":" + colspan + ":" + rowspan);
                if(attribute.valueExists("showLabel") == false || attribute.getBoolean("showLabel")){
                    gridPane.add(editor.createLabel(), columnIndex, rowIndex, 1, rowspan);
                    Node editorNode = editor.createEditor();
                    gridPane.add(editorNode, columnIndex + 1, rowIndex, colspan * 2 - 1, rowspan);

                }else{
                    Node editorNode = editor.createEditor();
                    gridPane.add(editorNode, columnIndex, rowIndex, colspan * 2, rowspan);
                }
;
                attributeEditors.add(editor);
            }

        }

        scrollPane.setContent(gridPane);
        return scrollPane;
    }

    /**
     * 返回正在编辑的模型，但不把表单数据保存到模型上。
     *
     * @return
     */
    public Thing getThing(){
        return getThing(false);
    }

    /**
     * 返回正在编辑的模型，先把表单的数据保存到模型上。
     *
     * @param saveValues
     * @return
     */
    public Thing getThing(boolean saveValues){
        if(editThing != null && saveValues){
            editThing.putAll(getValues());
        }
        return editThing;
    }

    public Thing getFormThing(){
        return formThing;
    }

    public void setFormThing(Thing formThing, ActionContext actionContext){
        this.formThing = formThing;
        this.actionContext = actionContext;

        if(formThing != null) {
            Integer col = formThing.doAction("getColumn", actionContext);
            column = col != null ? col : 1;

            Thing th = formThing.doAction("getThing", actionContext);
            if (th != null) {
                setThing(th);
            } else {
                Thing descritpor = formThing.doAction("getDescriptor", actionContext);
                if (descritpor != null) {
                    setDescriptor(descritpor);
                }
            }
        }
    }

    public ActionContext getActionContext(){
        return actionContext;
    }

    /**
     * 编辑控件发送的变化时调用此方法。
     * @param editor
     */
    public void modified(AttributeEditor editor){
        if(changeSource != this) {
            modifiedProperty.set(true);
            if(formThing != null) {
                formThing.doAction("modified", actionContext, "editor", editor, "form", this);
            }
        }
    }

    public void setModified(boolean modified){
        modifiedProperty.set(modified);
    }
}
