package xworker.dataObject.java;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.csvreader.CsvReader;

public class TextDataObject {

	public static List<Map<String, String>> getListData(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		String text = self.doAction("getText", actionContext);
		if(text == null || text.equals("")){
			return Collections.emptyList();
		}
		
		//使用CVS解析字符串
		char delimiter = (Character) self.doAction("getDelimiter", actionContext);
		ByteArrayInputStream strIn = new ByteArrayInputStream(text.getBytes("utf-8"));
		CsvReader csvReader = new CsvReader(strIn, delimiter, Charset.forName("utf-8"));
		csvReader.readRecord();

		//数据
		List<Map<String, String>> datas = new ArrayList<Map<String, String>>();
        int index = 0;
        while(csvReader.readRecord()){
            Map<String, String> data = new HashMap<String, String>();        
            for(int i=0; i<csvReader.getColumnCount(); i++){
                data.put("c" + i, csvReader.get(i));                
            }
            data.put("index", "" + index);
            index++;
            datas.add(data);            
        }
        
        return datas;
	}
}
