package xworker.poi.dataobject;


import org.apache.poi.ss.usermodel.*;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectIterator;
import xworker.dataObject.query.QueryConfig;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class POIDataObjectUtils {
    public static void exportToExcel(String fileName, DataObject dataObject, QueryConfig queryConfig, long maxRows, boolean hasHeader, ActionContext actionContext) throws IOException {
        exportToExcel(new File(fileName), dataObject, queryConfig, maxRows, hasHeader, actionContext);
    }

    public static void exportToExcel(File file, DataObject dataObject, QueryConfig queryConfig, long maxRows, boolean hasHeader, ActionContext actionContext) throws IOException {
        if(!file.exists()){
            file.getParentFile().mkdirs();
        }

        Workbook workbook = WorkbookFactory.create(false);
        Sheet sheet = workbook.createSheet(dataObject.getMetadata().getDescriptor().getMetadata().getLabel());
        exportToSheet(sheet, dataObject, queryConfig, maxRows, hasHeader, actionContext);

        FileOutputStream fout = new FileOutputStream(file);
        try {
            workbook.write(fout);
        }finally {
            fout.close();
        }
    }

    public static void exportToExcel(String fileName, Thing dataObject, List<DataObject> datas, boolean hasHeader) throws IOException {
        exportToExcel(new File(fileName), dataObject,datas, hasHeader);
    }

    public static void exportToExcel(File file, Thing dataObject, List<DataObject> datas, boolean hasHeader) throws IOException {
        if(!file.exists()){
            file.getParentFile().mkdirs();
        }

        Workbook workbook = WorkbookFactory.create(false);
        Sheet sheet = workbook.createSheet(dataObject.getMetadata().getLabel());
        exportToSheet(sheet, dataObject, datas, hasHeader);

        FileOutputStream fout = new FileOutputStream(file);
        try {
            workbook.write(fout);
        }finally {
            fout.close();
        }
    }
    public static void initColumnWidth(Sheet sheet, DataObject dataObject){
        int column = 0;
        for(Thing attr : dataObject.getMetadata().getAttributes()){
            if(!attr.getBoolean("showInTable")){
                continue;
            }
            if(attr.valueExists("gridWidth")){
                sheet.setColumnWidth(column, attr.getInt("gridWdith") * 50);
            }else{
                String type = attr.getString("type");
                if("datetime".equals(type)){
                    sheet.setColumnWidth(column,  256 * 22);
                }
            }
            column++;
        }
    }

    public static void writeHeader(Sheet sheet, DataObject dataObject, int rowIndex){
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        cellStyle.setFont(font);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);

        Row row = sheet.createRow(rowIndex);

        int column = 0;
        for(Thing attr : dataObject.getMetadata().getAttributes()){
            if(!attr.getBoolean("showInTable")){
                continue;
            }
            Cell cell = row.createCell(column);
            cell.setCellValue(attr.getMetadata().getLabel());
            cell.setCellStyle(cellStyle);
            if(attr.valueExists("gridWidth")){
                sheet.setColumnWidth(column, attr.getInt("gridWdith") * 50);
            }else{
                String type = attr.getString("type");
                if("datetime".equals(type)){
                    sheet.setColumnWidth(column, 256 * 22);
                }
            }
            column++;
        }
    }

    private static void writeDataObject(Sheet sheet, int rowIndex, DataObject data, CellStyle dateCellStyle, CellStyle dateTimeCellStyle, CellStyle timeCellStyle){
        Row row = sheet.createRow(rowIndex);

        int column = 0;
        for(Thing attr : data.getMetadata().getAttributes()) {
            if (!attr.getBoolean("showInTable")) {
                continue;
            }

            Cell cell = row.createCell(column);
            String type = attr.getString("type");
            String name = attr.getMetadata().getName();
            if("date".equals(type)){
                cell.setCellValue(data.getDate(name));
                cell.setCellStyle(dateCellStyle);
            }else if("datetime".equals(type)){
                cell.setCellValue(data.getDate(name));
                cell.setCellStyle(dateTimeCellStyle);
            }else if("time".equals(type)){
                cell.setCellValue(data.getDate(name));
                cell.setCellStyle(timeCellStyle);
            }else if("byte".equals(type) || "short".equals(type) || "int".equals(type) || "float".equals(type) ||
                    "long".equals(type) || "double".equals(type)){
                cell.setCellValue(data.getDouble(name));
            }else{
                cell.setCellValue(data.getString(name));
                cell.getCellStyle().setDataFormat((short) 0);
            }

            column++;
        }
    }

    public static void exportToSheet(Sheet sheet, DataObject dataObject, QueryConfig queryConfig, long maxRows, boolean hasHeader, ActionContext actionContext){
        int rowIndex = 0;

        //表头和宽度
        if(hasHeader){
            writeHeader(sheet, dataObject, rowIndex);
            rowIndex++;
        }else{
            initColumnWidth(sheet, dataObject);
        }

        DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
        short dateFormat = dataFormat.getFormat("yyyy-MM-dd");
        short dateTimeFormat = dataFormat.getFormat("yyyy-MM-dd HH:mm:ss");
        short timeFormat = dataFormat.getFormat("HH:mm:ss");
        CellStyle dateCellStyle = sheet.getWorkbook().createCellStyle();
        dateCellStyle.setDataFormat(dateFormat);
        CellStyle dateTimeCellStyle = sheet.getWorkbook().createCellStyle();
        dateTimeCellStyle.setDataFormat(dateTimeFormat);
        CellStyle timeCellStyle = sheet.getWorkbook().createCellStyle();
        timeCellStyle.setDataFormat(timeFormat);

        //数据
        DataObject.beginThreadCache();
        try {
            DataObjectIterator iterator = dataObject.iterator(actionContext, queryConfig);
            while(iterator.hasNext()){
                writeDataObject(sheet, rowIndex, iterator.next(), dateCellStyle, dateTimeCellStyle, timeCellStyle);
                rowIndex++;

                if(rowIndex >= maxRows){
                    break;
                }
            }
            try {
                iterator.close();
            }catch (Exception ignore){
            }
        }finally {
            DataObject.finishThreadCache();
        }
    }

    public static void exportToSheet(Sheet sheet, Thing dataObject, List<DataObject> datas, boolean hasHeader){
        int rowIndex = 0;

        //表头和宽度
        if(hasHeader){
            writeHeader(sheet, new DataObject(dataObject), rowIndex);
            rowIndex++;
        }else{
            initColumnWidth(sheet, new DataObject(dataObject));
        }

        DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
        short dateFormat = dataFormat.getFormat("yyyy-MM-dd");
        short dateTimeFormat = dataFormat.getFormat("yyyy-MM-dd HH:mm:ss");
        short timeFormat = dataFormat.getFormat("HH:mm:ss");
        CellStyle dateCellStyle = sheet.getWorkbook().createCellStyle();
        dateCellStyle.setDataFormat(dateFormat);
        CellStyle dateTimeCellStyle = sheet.getWorkbook().createCellStyle();
        dateTimeCellStyle.setDataFormat(dateTimeFormat);
        CellStyle timeCellStyle = sheet.getWorkbook().createCellStyle();
        timeCellStyle.setDataFormat(timeFormat);

        //数据
        DataObject.beginThreadCache();
        try {
            for(DataObject data : datas){
                writeDataObject(sheet, rowIndex, data,  dateCellStyle, dateTimeCellStyle, timeCellStyle);
                rowIndex++;
            }
        }finally {
            DataObject.finishThreadCache();
        }
    }
}
