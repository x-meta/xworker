/**
 * 单元格编辑的扩展。
 */ 
$.extend($.fn.datagrid.methods, {
    editCell: function(jq,param){
        return jq.each(function(){
            var opts = $(this).datagrid('options');
            var fields = $(this).datagrid('getColumnFields',true).concat($(this).datagrid('getColumnFields'));
            for(var i=0; i<fields.length; i++){
                var col = $(this).datagrid('getColumnOption', fields[i]);
                col.editor1 = col.editor;
                if (fields[i] != param.field){
                    col.editor = null;
                }
            }
            $(this).datagrid('beginEdit', param.index);
            for(var i=0; i<fields.length; i++){
                var col = $(this).datagrid('getColumnOption', fields[i]);
                col.editor = col.editor1;
            }
        });
    }
});
        
/**
 * 单元格编辑绑定。
 * 
 * @param gridId
 */
function xw_jqeasyui_grid_cell_editor(gridId){
    var datagrid = $(gridId).datagrid;
       
    datagrid.editRowIndex = undefined;
    datagrid.editField = undefined;
    
    datagrid.endEditing = function(theId){
        var datagrid = $(theId).datagrid;
        if (datagrid.editRowIndex == undefined){return true}       
        if ($(theId).datagrid('validateRow', datagrid.editRowIndex)){
            $(theId).datagrid('endEdit', datagrid.editRowIndex);
            datagrid.editRowIndex = undefined;
            datagrid.editField = undefined;
            return true;
        } else {
            return false;
        }
    }
}

/**
 * 开始编辑单元格。
 * 
 * @param gridId
 * @param index
 * @param field
 */
function xw_jqeasyui_grid_cell_start_edit(gridId, index, field){
    var datagrid = $(gridId).datagrid;
    
    if (datagrid.endEditing(gridId)){
        $(gridId).datagrid('selectRow', index)
                 .datagrid('editCell', {index:index,field:field});
        datagrid.editRowIndex = index;
        datagrid.editField = field;
    }
}

/**
 * 行编辑绑定。
 * 
 * @param gridId
 */
function xw_jqeasyui_grid_row_editor(gridId){
    var datagrid = $(gridId).datagrid;
       
    datagrid.editIndex = undefined;
    
    datagrid.endEditing = function(theId){
        var datagrid = $(theId).datagrid;
        if (datagrid.editIndex == undefined){return true}
        if ($(theId).datagrid('validateRow', datagrid.editIndex)){
            var ed = $(theId).datagrid('getEditor', {index:datagrid.editIndex});           
            $(theId).datagrid('endEdit', datagrid.editIndex);
            datagrid.editIndex = undefined;
            return true;
        } else {
            return false;
        }
    }
}

/**
 * 开始编辑行。
 * 
 * @param gridId
 * @param index
 * @param field
 */
function xw_jqeasyui_grid_row_start_edit(gridId, index, field){
    var datagrid = $(gridId).datagrid;
    if (datagrid.editIndex != index){
        if (datagrid.endEditing(gridId)){
            $(gridId).datagrid('selectRow', index)
                    .datagrid('beginEdit', index);
            datagrid.editIndex = index;
        } else {
            $(gridId).datagrid('selectRow', datagrid.editIndex);
        }
    }    
}

/**
 * 加载并缓存JSON数组。
 * 
 * @param name
 * @param url
 * @param params
 */
function xw_jqeasyui_load_store(name, url, params){
	$.getJSON(url, params, function(json){
		alert(name);
	});
}