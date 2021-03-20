package xworker.javafx.thing.form;

public class FormLayoutData<T> {
    int columnIndex;
    int rowIndex;
    int rowspan;
    int colspan;
    T object;

    public FormLayoutData(int columnIndex, int rowIndex, int colspan, int rowspan, T object){
        this.columnIndex = columnIndex;
        this.rowIndex = rowIndex;
        this.colspan = colspan;
        this.rowspan = rowspan;
        this.object = object;
    }

    public int getColspan(){
        return colspan;
    }

    public int getRowspan(){
        return rowspan;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public T getObject() {
        return object;
    }
}
