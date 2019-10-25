/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.ui.html;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.Thing;

public class GridLayout {
	/**
	 * Grid布局。
	 * 
	 * @param fields 需要布局的数据对象列表
	 * @param cols 布局总体所分的列
	 * 
	 * @return 布局的结果
	 */
	public static List<List<GridData>> layout(List<Thing> fields, int cols){
		//最多布局300行
        int maxRow = 300;
        List<List<GridData>> rows = new ArrayList<List<GridData>>();

        int[][] grids = new int[maxRow][cols];
        Object[][] gridData = new Object[maxRow][cols];

        for(int i=0; i<fields.size(); i++){        	
            Thing obj = (Thing) fields.get(i);
            String colspanStr = (String) obj.get("colspan");
            String rowspanStr = (String) obj.get("rowspan");

            int colspan = 0;
            int rowspan = 0;

            try{
                colspan = Integer.parseInt(colspanStr);
            }catch(Exception e){
            }
            try{
                rowspan = Integer.parseInt(rowspanStr);
            }catch(Exception e){
            }
            colspan = colspan == 0 ? 1 : colspan;
            rowspan = rowspan == 0 ? 1 : rowspan;

            colspan = colspan > cols ? cols : colspan;
            
            GridData gdata = new GridData();
            gdata.colspan = colspan;
            gdata.rowspan = rowspan;
            gdata.userData = obj;            
            
            //填充单元
            boolean seted = false;
            for(int m=0; m<maxRow; m++){
                for(int n=0; n<cols; n++){
                    if(grids[m][n] == 0){
                        if((cols - n) >= colspan){
                            boolean ok = true;
                            for(int k=m; k<m+rowspan; k++){
                                for(int p=n; p<n+colspan; p++){
                                    if(grids[k][p] == 1){
                                        ok = false;
                                        break;
                                    }
                                }
                                if(!ok) break;
                            }

                            if(ok){
                                gridData[m][n] = gdata;
                                seted = true;

                                for(int k=m; k<m+rowspan; k++){
                                    for(int p=n; p<n+colspan; p++){
                                        grids[k][p] = 1;
                                    }
                                }
                            }
                        }
                    }
                    if(seted) break;
                }
                if(seted) break;
            }
        }

        //处理griddata
        for(int i=0; i<maxRow; i++){
            boolean norow = true;
            for(int k=0; k<cols; k++){
                if(gridData[i][k] != null){
                    norow = false;
                    break;
                }
            }

            if(norow) continue;

            List<GridData> row = new ArrayList<GridData>();
            rows.add(row);
            for(int k=0; k<cols; k++){
                int colspan = 1;
                if(gridData[i][k] != null){
                	GridData gdata = (GridData) gridData[i][k];                    
                    for(int m=k+1; m<cols; m++){
                        if(gridData[i][m] == null){
                        	colspan ++;
                        }else{
                        	break;
                        }
                    }

                    gdata.colspan = colspan;                    
                    row.add(gdata);
                }
            }
        }
        return rows;
    }
}