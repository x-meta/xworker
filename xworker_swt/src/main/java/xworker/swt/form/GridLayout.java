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
package xworker.swt.form;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.Thing;

/**
 * 通用Grid布局器。
 * 
 * @author zyx
 *
 */
public class GridLayout {
	public static List<List<GridData>> layout(List<Thing> fields, int cols){
		int maxRow = 300;
        ArrayList<List<GridData>> rows = new ArrayList<List<GridData>>();

        int[][] grids = new int[maxRow][cols];
        Object[][] gridData = new Object[maxRow][cols];

        for(int i=0; i<fields.size(); i++){        	
        	Thing field = fields.get(i);
        	
        	GridData fieldGridData = new GridData();
        	fieldGridData.source = field;            
        	fieldGridData.colspan = field.getInt("colspan", 1);
        	fieldGridData.rowspan = field.getInt("rowspan", 1);            

        	fieldGridData.colspan = fieldGridData.colspan > cols ? cols : fieldGridData.colspan;
        	int colspan = fieldGridData.colspan;
        	int rowspan = fieldGridData.rowspan;            
            
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
                                gridData[m][n] = fieldGridData;
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
                	GridData obj = (GridData) gridData[i][k];
                    for(int m=k; m<cols; m++){
                        if(gridData[i][m] == null){
                        	colspan ++;
                        }else if(gridData[i].length > k+1 && gridData[i][k+1] != null){
                        	break;
                        }
                    }

                    obj.colspan = colspan;

                    row.add(obj);
                }
            }
        }
        return rows;
	}
}