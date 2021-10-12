package xworker.javafx.control;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class WeekHoursView extends Canvas {
    byte[] hours = new byte[7 * 24];
    boolean[] selectedItems = new boolean[7 * 24];
    double cellHeight;
    double cellWidth;
    double cellX;
    double cellY;
    boolean dragging = false;
    double dragX;
    double dragY;
    Color selectedColor = Color.BLUE;
    Color checkedColor = Color.LIGHTGREEN;

    public WeekHoursView(){
        init();
    }

    public WeekHoursView(byte[] hours){
        this.hours = hours;

        init();
    }

    private void init(){
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int index = getCellIndex(event.getX(), event.getY());
                if(index >= 0) {
                    if (event.getClickCount() >= 2) {
                        //双击反选
                        if (hours[index] == 0) {
                            hours[index] = 1;
                        } else {
                            hours[index] = 0;
                        }
                    }

                    //设置选中
                    if(!dragging){
                        for (int i = 0; i < selectedItems.length; i++) {
                            if (i == index) {
                                selectedItems[i] = true;
                            } else {
                                selectedItems[i] = false;
                            }
                        }
                    }else{
                        dragging = false;
                    }

                    redraw();
                }
            }
        });

        this.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY){
                    int index = getCellIndex(event.getX(), event.getY());
                    if(index >= 0) {
                        //设置选中
                        for (int i = 0; i < selectedItems.length; i++) {
                            if (i == index) {
                                selectedItems[i] = true;
                            } else {
                                selectedItems[i] = false;
                            }
                        }

                        dragging = true;
                        dragX = event.getX();
                        dragY = event.getY();
                        redraw();
                    }
                }
            }
        });

        this.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                dragging = true;

                int startIndex = getCellIndex(dragX, dragY);
                int endIndex = getCellIndex(event.getX(), event.getY());

                if(startIndex >=0 && endIndex >= 0){
                    int startRow = startIndex / 24;
                    int startCol = startIndex % 24;
                    int endRow = endIndex / 24;
                    int endCol = endIndex % 24;
                    if(startRow > endRow){
                        int row = startRow;
                        startRow = endRow;
                        endRow = row;
                    }
                    if(startCol > endCol){
                        int col = startCol;
                        startCol = endCol;
                        endCol = col;
                    }

                    for(int i=0; i<7 * 24; i++){
                        int row = i / 24;
                        int col = i % 24;
                        if(row >= startRow && row <= endRow && col >= startCol && col <= endCol){
                            selectedItems[i] = true;
                        }else{
                            selectedItems[i] = false;
                        }
                    }

                    redraw();
                }

            }
        });
        this.setOnMouseDragOver(new EventHandler<MouseDragEvent>() {
            @Override
            public void handle(MouseDragEvent event) {
                //System.out.println("dragged over");
            }
        });
        this.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            }
        });

        this.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });

        this.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

            }
        });
        redraw();
    }

    public void checkSelected(){
        for(int i=0; i<selectedItems.length; i++){
            if(selectedItems[i]){
                hours[i] = 1;
            }
        }

        redraw();
    }

    public void uncheckSelected(){
        for(int i=0; i<selectedItems.length; i++){
            if(selectedItems[i]){
                hours[i] = 0;
            }
        }

        redraw();
    }

    public void reverseSelected(){
        for(int i=0; i<selectedItems.length; i++){
            if(selectedItems[i]){
                if(hours[i] == 0) {
                    hours[i] = 1;
                }else{
                    hours[i] = 0;
                }
            }
        }

        redraw();
    }

    private int getCellIndex(double x, double y){
        for(int i=0; i<7 * 24; i++){
            int row = i / 24;
            int col = i % 24;
            double startX = cellX + col * cellWidth;
            double startY = cellY + row * cellHeight;
            if(x > startX && x < startX + cellWidth -1 && y > startY && y < startY + cellHeight - 1){
                return i;
            }
        }
        return -1;
    }

    public void redraw(){
        Text text = new Text("一");
        double weekHeight = text.getBoundsInLocal().getHeight();
        double weekWidth = text.getBoundsInLocal().getWidth();
        cellHeight = weekHeight + 2;
        cellX = weekWidth + 10;
        cellY = cellHeight;
        text = new Text ("00");
        cellWidth = text.getBoundsInLocal().getWidth() + 2;
        this.setWidth(cellX + cellWidth * 24);
        this.setHeight(cellHeight * 8);
        text = new Text("0");
        double singleHourWidth = text.getBoundsInLocal().getWidth();

        GraphicsContext gc = this.getGraphicsContext2D();
        //gc.setTextAlign(TextAlignment.CENTER);

        //星期和时间的标签的行和列
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, this.getWidth(), cellY);
        gc.fillRect(0, 0, cellX, this.getHeight());

        double weekX = (cellX - weekWidth) / 2;
        double weekY = (cellHeight - weekHeight) / 2;
        gc.strokeText("一", weekX, cellY + cellHeight - weekY - 2.5);
        gc.strokeText("二", weekX, cellY + cellHeight * 2 - weekY - 2.5);
        gc.strokeText("三", weekX, cellY + cellHeight * 3 - weekY - 2.5);
        gc.strokeText("四", weekX, cellY + cellHeight * 4 - weekY - 2.5);
        gc.strokeText("五", weekX, cellY + cellHeight * 5 - weekY - 2.5);
        gc.strokeText("六", weekX, cellY + cellHeight * 6 - weekY - 2.5);
        gc.strokeText("日", weekX, cellY + cellHeight * 7 - weekY - 2.5);

        for(int i=0; i<24; i++){
            String hour = String.valueOf(i);
            if(hour.length() == 1) {
                gc.strokeText(hour, cellX + cellWidth * i + (cellWidth - singleHourWidth) / 2, cellY - 2.5);
            }else{
                gc.strokeText(hour, cellX + cellWidth * i + 1, cellY - 2.5);
            }
        }


        gc.setLineWidth(1);

        //画边框
        gc.strokeLine(0, 0, this.getWidth(), 0);
        gc.strokeLine(this.getWidth(), 0, this.getWidth(), this.getHeight());
        gc.strokeLine(0, 0, 0, this.getHeight());
        gc.strokeLine(0, this.getHeight(), this.getWidth(), this.getHeight());

        //画单元格的线
        for(int i=0; i<7; i++) {
            gc.strokeLine(0, cellHeight * (i + 1), this.getWidth(), cellHeight * (i + 1));
        }
        for(int i=0; i<24; i++){
            gc.strokeLine(cellX + cellWidth * i, 0, cellX + cellWidth * i, this.getHeight());
        }

        //每个格子先铺一层底色
        gc.setFill(Color.rgb(250,250,250));
        for(int i=0; i<7 * 24; i++){
            int row = i / 24;
            int col = i % 24;
            double startX = cellX + col * cellWidth;
            double startY = cellY + row * cellHeight;
            gc.fillRect(startX + 1, startY + 1, cellWidth-2, cellHeight-2);
        }

        //画单元格的颜色
        gc.setFill(checkedColor);
        for(int i=0; i<7 * 24; i++){
            if(hours[i] == 1){
                int row = i / 24;
                int col = i % 24;
                double startX = cellX + col * cellWidth;
                double startY = cellY + row * cellHeight;
                gc.fillRect(startX + 1, startY + 1, cellWidth-2, cellHeight-2);
            }
        }

        //画选中的颜色
        gc.setFill(selectedColor);
        for(int i =0; i<7 * 24; i++){
            if(selectedItems[i]){
                int row = i / 24;
                int col = i % 24;
                double startX = cellX + col * cellWidth;
                double startY = cellY + row * cellHeight;
                gc.fillRect(startX + 1, startY + 1, cellWidth-2, cellHeight-2);
            }
        }

        //选中的画一个钩
        for(int i=0; i<7 * 24; i++){
            if(hours[i] == 1){
                int row = i / 24;
                int col = i % 24;
                double startX = cellX + col * cellWidth;
                double startY = cellY + row * cellHeight;

                //画一个钩
                gc.strokeLine(startX + cellWidth / 4, startY + cellHeight / 2, startX + cellWidth / 2, startY + cellHeight * 3 / 4);
                gc.strokeLine(startX + cellWidth / 2, startY + cellHeight * 3 / 4, startX + cellWidth * 7 / 8, startY + cellHeight / 4);
            }
        }
    }

    public void setHours(byte[] hours){
        this.hours = hours;

        redraw();
    }

    public byte[] getHours(){
        return hours;
    }

    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        WeekHoursView view = new WeekHoursView();
        actionContext.g().put(self.getMetadata().getName(), view);
        return view;
    }
}
