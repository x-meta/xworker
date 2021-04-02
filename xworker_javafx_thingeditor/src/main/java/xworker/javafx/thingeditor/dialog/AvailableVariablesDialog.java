package xworker.javafx.thingeditor.dialog;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;
import xworker.lang.VariableDesc;
import xworker.lang.actions.ActionContainer;
import xworker.util.codeassist.CodeAssistor;
import xworker.util.codeassist.CodeHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ActionClass
public class AvailableVariablesDialog {
    @ActionField
    public javafx.scene.control.TextArea codeText;
    @ActionField
    public javafx.scene.control.ListView<VariableDesc> variableListView;
    @ActionField
    public Thing currentThing;
    @ActionField
    public ActionContext actionContext;
    @ActionField
    public ActionContainer actions;

    List<VariableDesc> checkedVariables = new ArrayList<>();

    public void init(){
        variableListView.setCellFactory(new Callback<ListView<VariableDesc>, ListCell<VariableDesc>>() {
            @Override
            public ListCell<VariableDesc> call(ListView<VariableDesc> param) {
                CheckBoxListCell<VariableDesc> cell = new CheckBoxListCell<VariableDesc>(){
                    @Override
                    public void updateItem(VariableDesc item, boolean empty) {
                        super.updateItem(item, empty);

                        if(item == null || empty){
                            return;
                        }

                        setText(item.getName());
                    }
                };
                cell.setSelectedStateCallback(new Callback<VariableDesc, ObservableValue<Boolean>>() {
                    @Override
                    public ObservableValue<Boolean> call(VariableDesc param) {
                        SimpleBooleanProperty booleanProperty = new SimpleBooleanProperty();
                        booleanProperty.addListener(new CheckListener(AvailableVariablesDialog.this, param));
                        return booleanProperty;
                    }
                });

                return cell;
            }
        });

        List<VariableDesc> vars = CodeHelper.getVariableDescs(currentThing, actionContext);
        Collections.sort(vars);
        variableListView.getItems().addAll(vars);

    }

    public void checked(VariableDesc variableDesc){
        if(!checkedVariables.contains(variableDesc)){
            checkedVariables.add(variableDesc);

            Collections.sort(checkedVariables);
        }

        generateCode();
    }

    public void unchecked(VariableDesc variableDesc){
        checkedVariables.remove(variableDesc);

        generateCode();
    }

    public void generateCode(){
        String code = actions.doAction("toCode", actionContext, "vars", checkedVariables);
        codeText.setText(code);
    }

    static class CheckListener implements  ChangeListener<Boolean> {
        AvailableVariablesDialog dialog;
        VariableDesc variableDesc;

        public CheckListener(AvailableVariablesDialog dialog, VariableDesc variableDesc){
            this.dialog = dialog;
            this.variableDesc = variableDesc;
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if(newValue){
                dialog.checked(variableDesc);
            }else{
                dialog.unchecked(variableDesc);
            }
        }
    }
}
