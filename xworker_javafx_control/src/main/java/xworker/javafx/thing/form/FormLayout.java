package xworker.javafx.thing.form;

import java.util.ArrayList;
import java.util.List;

/**
 * 辅助类，用于帮助生成GridPane所欲的columnIndex和rowIndex等。
 */
public class FormLayout<T> {
    boolean[][] rows;

    int maxColumn;
    int maxRow;
    List<FormLayoutData<T>> datas = new ArrayList<>();

    public FormLayout(int maxColumn, int maxRow){
        rows = new boolean[maxRow][maxColumn];
        this.maxColumn = maxColumn;
        this.maxRow = maxRow;
    }

    public void add(int colspan, int rowspan, T object){
        if(colspan < 1){
            colspan = 1;
        }
        if(colspan > maxColumn){
            colspan = maxColumn;
        }
        if(rowspan < 1){
            rowspan = 1;
        }

        int currentRow = 0;
        int currentColumn = 0;

        while(true){
            if(rows[currentRow][currentColumn]){
                currentColumn++;
                if(currentColumn == maxColumn){
                    currentRow++;
                    currentColumn = 0;
                }
            }else{
                if(colspan + currentColumn <= maxColumn){
                    for(int r =0; r < rowspan; r++){
                        for(int c=0; c < colspan; c++){
                            rows[currentRow + r][currentColumn + c] = true;
                        }
                    }
                    datas.add(new FormLayoutData(currentColumn, currentRow, colspan, rowspan, object));
                    break;
                }else{
                    currentRow++;
                    currentColumn = 0;
                }
            }
        }
    }

    public List<FormLayoutData<T>> getLayoutDatas(){
        return datas;
    }

}
