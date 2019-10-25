/**
 * XWorker Yahoo UI Utilities.
 *
 * 提供一些方便简化调用Yahoo UI的接口。
 */
if(!XYUI){
    var xworker = YAHOO.namespace('xworker');
        
    /**
     * 数据集合的客户端的组件。
     *
     * 目前的作用主要是查询数据，能够使多个控件共享数据。
     */     
    var DataSet = function(dataSetPath, responseSchema, webApp){
        this.dataSetPath = dataSetPath;
        this.responseSchema = responseSchema;
        this.webApp = webApp;
        this.inited = false;
        this.request = "";
                
        //数据源
        this.dataSource = new YAHOO.util.DataSource('do?sc=app:web:control.DataSetControl:/@query&dataSet=' + dataSetPath  + '&_webApp=' + webApp, {});
        this.dataSource.connMethodPost = true;
        this.dataSource.responseType = YAHOO.util.DataSource.TYPE_XML;
        this.dataSource.responseSchema = responseSchema;
        
        //事件监听列表
        this.listeners = [];
        
        //缓存的数据
        this.records = [];
        this.initLoad = false; //数据是否已经初始化
        
        /**
         * 添加事件，事件在刷新数据时或者第一次增加到事件列表时触发，用途是监听数据集合的数据。 
         *
         * 事件的参数records，如function(recrods);
         */
        this.addListener = function(listener){
            this.listeners[this.listeners.length] = listener;
            
            if(this.inited){
                //如果数据已经初始化，触发事件
                listener.refrshed(records);
            }
        }
        
        /**
         * 删除指定的事件监听。
         */
        this.removeListener = function(listener){
            for(i=0; i<this.listeners.length; i++){
                if(listeners[i] == listener){
                    listeners.remove(i);
                    break;
                }
            }
        }
        
        /**
         * 返回数据源。
         */
        this.getDataSource = function(){
            return this.dataSource;
        }
        
        /**
         * 查询数据。参数oRequest为xx=xx&xxx=xxx的格式, oCallBack为YUI的callBack格式。
         * 其中公共参数有：_sort,_sortDir,_startIndex,_pageSize，这些参数用于排序和分页。
         */
        this.query = function(oRequest, oCallBack){
            this.dataSource.sendRequest(oRequest, oCallBack);
        }
        
        /**
         * 插入一条记录。
         */         
        this.insert = function(oRequest, oCallBack){
        }
                                 
        /**
         * 取缓存中的数据。
         */
        this.getRecords = function(){
            return this.records;
        }
        
        /**
         * 刷新缓存的数据。
         */
        this.refresh = function(oRequest){
            if(!oRequest){
                oRequest = this.request;
            }else{
                this.reqeust = oRequest;
            }
            this.query(oRequest, {   
                success: function(oRequest,oResponse) {   
                    this.records = [];
                    for(i=0; i<oResponse.results.length; i++){
                        this.records[this.records.length] = oResponse.results[i];
                    }
                    
                    this.inited = true;
                    
                    //触发监听事件                    
                    for(var i=0; i<this.listeners.length; i++){
                        this.listeners[i].refreshed(this.records);
                    }
                }, 
               
                failure: function(oRequest,oResponse,oPayload) {   
                    for(i=0; i<this.listeners.length; i++){
                        this.listeners[i].refreshFailure(oRequest, oResponse);
                    }
                },   
             
                scope: this
            });
        }       
        
        /**
         * 初始化数据
         */
        this.init = function(oRequest){
            if(this.initLoad){
                return;
            }else{
                this.initLoad = true;
            }
            
            if(!oRequest){
                oRequest = this.request;
            }else{
                this.reqeust = oRequest;
            }
            
            this.refresh();
        }         
    };
    
    /**
     * 下拉组合框。
     * oConfig: {notNull=true/false, labelName=string, valueName=string}
     */
    var DataSetComboBox = function(dataSet, comboBox, oConfig){        
        this.comboBox = comboBox;
        this.dataSet = dataSet;
        this.oConfig = oConfig;
        
        this.listener = {
            refreshed : function(records){
                //先清除原有数据
                 while(comboBox.childNodes.length>0){ 
                     comboBox.removeChild(comboBox.childNodes[0]); 
                 } 
                 
                 //添加新获得的数据
                 if(oConfig.notNull){
                 }else{
                     option=document.createElement("option"); 
                     comboBox.appendChild(option);
                 }
                 
                 for(var i=0; i<records.length; i++){
                     option=document.createElement("option"); 
                     option.value =  records[i][oConfig.valueName];
                     option.appendChild(document.createTextNode(records[i][oConfig.labelName])); 
                     comboBox.appendChild(option);                     
                 }
                 
                 comboBox.selectedIndex = 0;
                 //触发onChange事件
                 if(comboBox.onchange){
                     comboBox.onchange();
                 }
                 //comboBox.selectedIndex = 1;
            }
        };
        
        this.dataSet.addListener(this.listener);
    };
    
    /**
     * 数据集单选组，根据数据集生成一个单选按钮组合，比如通过Sex（性别）生成男/女两个单选按钮。
     * dataSet: 数据集
     * radioDiv: 包含radio的DIV
     * oConfig: {notNull=true/false, labelName=string, valueName=string, inputName=string}
     */
    var DataSetRadio = function(dataSet, radioDiv, oConfig){        
        this.radioDiv = radioDiv;
        this.dataSet = dataSet;
        this.cfg = oConfig;
        
        this.listener = {
            refreshed : function(records){                 
                //生成HTML界面时的值
                var value = radioDiv.getAttribute("value");
                
                var radios = XYUI.getChildsByName(radioDiv, oConfig.inputName);
                if(radios){
                    for(var i=0; i<radios.length; i++){
                        if(radios[i].checked){
                            value = radios[i].value;
                        }
                    }
                }
                
                //根据DataSet的数据生成radio输入
                var html = "";
                for(var i=0; i<records.length; i++){
                    html = html + "<input type='radio' name='" + oConfig.inputName 
                        + "' value='" + records[i][oConfig.valueName] + "'" 
                        + (records[i][oConfig.valueName] == value ? " checked" :"") 
                        + "/>" + records[i][oConfig.labelName];
                }
                radioDiv.innerHTML = html;
            },
            
            refreshFailured : function(){
                radioDiv.innerHTML = "获取数据失败！";
            }
        };
        
        this.dataSet.addListener(this.listener);
    };
    
    /**
     * 数据集单选组，根据数据集生成一个单选按钮组合，比如通过Sex（性别）生成男/女两个单选按钮。
     * dataSet: 数据集
     * radioDiv: 包含radio的DIV
     * oConfig: {notNull=true/false, labelName=string, valueName=string, inputName=string}
     */
    var DataSetCheckbox = function(dataSet, checkDiv, oConfig){        
        this.checkDiv = checkDiv;
        this.dataSet = dataSet;
        this.oConfig = oConfig;
        
        this.listener = {
            refreshed : function(records){                             
                var value = checkDiv.getAttribute("value");
                
                var checks = XYUI.getChildsByName(checkDiv, oConfig.inputName);
                if(checks){
                    if(checks.length > 0){
                        value = "";
                    }
                    
                    for(var i=0; i<checks.length; i++){
                        if(checks[i].checked){
                            value += "," + checks[i].value;
                        }
                    }
                }
                    
                //添加新获得的数据
                var html = "";
                for(i=0; i<records.length; i++){
                    var vs = value.split(",");
                    var checked = "";
                    for(var n=0; n<vs.length; n++){
                        if(vs[n] == records[i][oConfig.valueName]){
                            checked = " checked";
                        }
                    }
                    
                    html = html + "<input type='checkBox' name='" + oConfig.inputName 
                        + "' value='" + records[i][oConfig.valueName] + "'" + checked + "/>" + records[i][oConfig.labelName];
                }
                checkDiv.innerHTML = html;
            },
            
            refreshFailured : function(){
                checkDiv.innerHTML = "获取数据失败！";
            }
        };
        
        this.dataSet.addListener(this.listener);
    };
    
    /**
     * 数据集表单，数据集表单的set、get和validate方法分别用于设置表单的值、取表单的值和校验表单。
     *
     * 参数：
     *    formId           表单ID
     *    dataSetPath      数据集路径
     *    responseSchema   DatasSource取数据用于分析XML的JS Schema
     *    prefixName       表单输入名称前缀
     */
    var DataSetForm = function(formId, dataSetPath, responseSchema, prefixName, defaultValues){
        this.form = document.getElementById(formId);
        this.dataSetPath = dataSetPath;
        this.responseSchema = responseSchema;
        this.prefixName = prefixName;
        this.defaultValues = defaultValues;
        
        /**
         * 设置一个键值对应的数据集合到表单。
         */
        this.set = function(record){
            //获得表单的数据名称列表            
            var fields = responseSchema.fields;
            for(i=0; i<fields.length; i++){
                //取输入控件的名称
                var key = fields[i];
                var value = record[key];
                if(typeof(value) == "undefined"){
                    value = "";
                }
                var inputName = key;
                if(prefixName){
                    inputName = prefixName + inputName;
                }
                 
                //设置输入控件的值
                var inputElements = YAHOO.util.Dom.getElementsBy(function(el){
                    if(el.name == inputName){
                        return true;
                    }else{
                        return false;
                    }
                }, null, this.form, null);
                //alert(inputElements.length);
                //XYUI.getElementByName(this.form, inputName);
                
                //alert(key + ":" + record[key]);
                if(inputElements.length == 1){
                    inputElement = inputElements[0];                  
                    if(typeof(inputElement.value) != "undefined"){
                        inputElement.value = value;
                    }else{
                        inputElement.innerHTML = value;
                        //inputElement.value = record.getData(key);
                    }
                }else if(inputElements.length > 1){
                    //查看是否是radio button或check box的情况
                    inputElement = inputElements[0];
                    if(inputElement.type == "radio"){
                        for(var n=0; n<inputElements.length; n++){
                            if(inputElements[n].value == value){
                                inputElements[n].checked = true;
                                break;
                            }
                        }
                    }else if(inputElement.type == "checkbox"){
                        var v = value;
                        var vs = [];
                        if(v){
                            vs = v.split(",");                            
                        }
                        for(var n=0; n<inputElements.length; n++){
                            for(var k=0; k<vs.length; k++){
                                if(inputElements[n].value == vs[k]){
                                    inputElements[n].checked = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        /**
         * 设置Datatable的Record。
         */
        this.setDatatableRecord = function(record){
            var newRecord = {};
            var fields = responseSchema.fields;
            for(i=0; i<fields.length; i++){
                //取输入控件的名称
                var key = fields[i];     
                newRecord[key] = record.getData(key);                
           }
           
           this.set(newRecord);
        }
        
        /**
         * 设置表单的默认值。
         */
        this.setDefault = function(){
            var dValues = defaultValues;
            if(typeof(dValues) == "undefined"){
                dValues = {};
                var fields = responseSchema.fields;
                for(i=0; i<fields.length; i++){
                    //取输入控件的名称
                    var key = fields[i];
                    dValues[key] = "";
               }
            }
                        
            this.set(dValues);
        }
        
        /**
         * 取表单中的数据，返回一个键值对应的数据集合。
         */
        this.get = function(){
            var record = [];
            
            //获得表单的数据名称列表            
            var fields = responseSchema.fields;
            for(i=0; i<fields.length; i++){
                //取输入控件的名称
                var key = fields[i];
                var inputName = key;
                if(prefixName){
                    inputName = prefixName + inputName;
                }
                
                //设置输入控件的值
                var inputElement = this.form.getElementByName(key);
                if(inputElement){
                    if(inputElement.value){
                        record[key] = inputElement.vlaue;
                    }else{
                        record[key] = inputElement.innerHTML;
                    }
                }
            }
        }
                
        
        /**
         * 校验表单中的数据，使用fvalidate。
         */
        this.validate = function (){
            return validateForm(this.form, false, false, false,false,17);
        }
        
        /**
         * 提交表单。
         */
        this.submit = function(){            
            if(this.validate()){               
                this.form.submit();
            }
        }
        
        /**
         * 异步提交表单。
         * 参数：scope范围，successFn成功后的函数，failureFn失败后执行的函数
         */
        this.submitAsync = function(scope, successFn, failureFn){
            if(!this.validate()){  
                return;
            }
            
            var handleSuccess = function(o){
		        try{
                	if(o.status == 200 && o.responseText !== undefined){    
                	    //alert(     o.responseText);         	    
                        XYUI.evalScriptTags(o.responseText, scope);
                        if(successFn){
                            successFn(scope);
                        }
                	}else{
                        alert("连接服务器失败：" + o.statusText);
                	}
                }catch(exception){
                }
            };

            var handleFailure = function(o){
                if(failureFn){
                    failureFn(o);
                }else{
                    alert("连接服务器失败：" + o.statusText);  alert(o.statusText);
              	}
            };
            
            var callback = {
                success:handleSuccess,
                failure:handleFailure
            };
            
            var con = YAHOO.util.Connect;            
            var postData = con.setForm(this.form);      
            con.asyncRequest("POST", 
                       this.form.action,
                       callback, "");
        }
    };
    
    /**
     * 能够异步提交的表单。
     */
    var AsyncForm = function(formId){
        this.form = document.getElementById(formId);
        
        /**
         * 提交表单。
         */
        this.submit = function(){            
            if(this.validate()){               
                this.form.submit();
            }
        }
        
        /**
         * 异步提交表单。
         * 参数：scope范围，successFn成功后的函数，failureFn失败后执行的函数
         */
        this.submitAsync = function(scope, successFn, failureFn){            
            var handleSuccess = function(o){
		        try{
                	if(o.status == 200 && o.responseText !== undefined){    
                	    //alert(     o.responseText);         	    
                        XYUI.evalScriptTags(o.responseText, scope);
                        if(successFn){
                            successFn(scope);
                        }
                	}else{
                        alert("连接服务器失败：" + o.statusText);
                	}
                }catch(exception){
                }
            };

            var handleFailure = function(o){
                if(failureFn){
                    failureFn(o);
                }else{
                    alert("连接服务器失败：" + o.statusText);  alert(o.statusText);
              	}
            };
            
            var callback = {
                success:handleSuccess,
                failure:handleFailure
            };
            
            var con = YAHOO.util.Connect;            
            var postData = con.setForm(this.form);      
            con.asyncRequest("POST", 
                       this.form.action,
                       callback, "");
        }
        
        /**
         * 异步提交表单并不把返回结果当作脚本处理，有成功函数处理。
         * 参数：scope范围，successFn成功后的函数，failureFn失败后执行的函数
         */
        this.submitAsyncNoEval = function(scope, successFn, failureFn){            
            var handleSuccess = function(o){
		        try{
                	if(o.status == 200 && o.responseText !== undefined){    
                	    //alert(     o.responseText);         	    
                        if(successFn){
                            successFn( o.responseText);
                        }else{
                            alert(o.responseText);
                        }
                	}else{
                        alert("连接服务器失败：" + o.statusText);
                	}
                }catch(exception){
                }
            };

            var handleFailure = function(o){
                if(failureFn){
                    failureFn(o);
                }else{
                    alert("连接服务器失败：" + o.statusText);  alert(o.statusText);
              	}
            };
            
            var callback = {
                success:handleSuccess,
                failure:handleFailure
            };
            
            var con = YAHOO.util.Connect;            
            var postData = con.setForm(this.form);      
            con.asyncRequest("POST", 
                       this.form.action,
                       callback, "");
        }
    };
    
    var xdataset = {
        /**
         * 获取一个DataSet的ResponseSchema，获取后存储到cache中，第二次就从cache中读取。
         */
        getResponseSchema: function(dataSetPath){
            //从缓存中读取schema
            if(xworker.schemas[dataSetPath]){
                return xworker.schemas[dataSetPath];
            }
            
            //如果缓存中没有schema，从服务区端读取
            if(!xworker.schemas){
                xworker.schemas = [];
            }            
            
            var finished = false;
            YAHOO.util.Connect.asyncRequest('POST', "do?sc=app:web:control.DataSetControl:/@getResponseSchema", {
                    success: function(o){
                        xworker.schemas[dataSetPath] = eval(o.responseText);
                        finished = true;
                    },
                    
                    fialure: function(o){
                        finished = false;
                    }
                }, "dataSet=" + dataSetPath);
                
            while(!finished){
                
            }
            return xworker.schemas[dataSetPath];
        }
    };
    
    var XYUI = {
        showLoading1 : function(){    	        
        	var h=1600;//document.documentElement.clientHeight;
    		var w=1400;//document.documentElement.clientWidth;
    
    		var div=document.createElement("<div id='poploadingdivxyui' style='valign:center;align:center;position:absolute;visibility:visible;background:#FFFFFF;filter:alpha(opacity=100);z-index:2;left:0;top:0;width:"+w+"px;height:"+h+"px;'></div>");
    		div.innerHTML = "<div align='left'><img src=\"images/blue-loading.gif\"/></div>";
    		document.body.appendChild(div);
    		//alert("tt");
        },
        
        hideLoading1 : function(){
            var popdiv = document.getElementById("poploadingdivxyui");
            if(popdiv){
	            popdiv.style.display = "none";
	        }
        },
        
        showLoading : function(){            
            if(!YAHOO.xworker.xyui_wait){
                YAHOO.xworker.xyui_wait = new YAHOO.widget.Panel("xyui_wait",  
                                                    { width: "200px", 
                                                      fixedcenter: true, 
                                                      close: false, 
                                                      draggable: false, 
                                                      zIndex:4,
                                                      modal: true,
                                                      visible: false
                                                    } 
                                                );
    
                YAHOO.xworker.xyui_wait.setHeader("正在装载，请稍稍...");
                YAHOO.xworker.xyui_wait.setBody("<div align='center'><img src=\"images/blue-loading.gif\"/></div>");
                YAHOO.xworker.xyui_wait.render(document.body);
            }
            
            YAHOO.xworker.xyui_wait.show();           
        },
        
        hideLoading : function(){
            YAHOO.xworker.xyui_wait.hide();
        },
        
        /**
         * 通过POST提交数据(postData)。
         *
         * 参数：
         *   sUrl URL
         *   postData 格式xx=xx&xx1=xx1的字符串，表示参数
         *   elementContaienr 返回值将放在那个Element元素下，如果为空放在body下
         *   successFn 成功后的执行函数
         *   failureFn 失败后执行的函数
         *   callerNameSpace 调用者所处的命名控件，传入的是字符串
         */
		openHTML : function(sUrl, postData, elementContainer, successFn, failureFn, callerNameSpace){		    
		    XYUI.showLoading();
		    var handleSuccess = function(o){
		        try{
                	if(o.status == 200 && o.responseText !== undefined){       
                	    //firefox下innerHtml不执行脚本，ie下innerHtml和appendChild都不执行脚本
                	    var evalScript = false;    
                	    var container = null;      	    
                	    if(elementContainer){          
               	            elementContainer.innerHTML = o.responseText;        
               	            container = elementContainer;    	           
               	            evalScript = true;
                	    }else{
                	        //在document节点创建一个专门用于获得新节点的div                	        
                	        var openDiv = document.getElementById("_XYUI_openHTML_Div");
                	        if(!openDiv){
                	            openDiv = document.createElement("div"); 
                                openDiv.id =  "_XYUI_openHTML_Div";          
                	            YAHOO.util.Dom.setStyle (openDiv, "width", "0");  
                	            YAHOO.util.Dom.setStyle (openDiv, "height", "0");  
                  	            document.body.appendChild(openDiv);                 	        
                  	        }
                  	        
                  	        //创建一个新的div
                  	        var htmlDiv = document.createElement("div"); 
            	            YAHOO.util.Dom.setStyle (htmlDiv, "width", "0");  
            	            YAHOO.util.Dom.setStyle (htmlDiv, "height", "0");  
              	            openDiv.appendChild(htmlDiv);   
              	            htmlDiv.innerHTML = o.responseText;  
              	            evalScript = true;             	         
              	            container = htmlDiv;               	        
                	    }
                	                    	  
                	    //如果是IE，那么执行返回代码的<script>...</script>下的脚本
                	    if(YAHOO.env.ua.ie > 0 || evalScript){
           	                XYUI.evalScriptTags(o.responseText);
                	    }
                	    
                	    if(successFn){
                	        successFn();
                	    }
                	}else{
                	    if(failureFn){
                	        failureFn();
                	    }else{
                	        alert(o.statusText);
                	    }
                	}
                }catch(exception){
                    //alert(exception);
                }
                
                XYUI.hideLoading();
            };

            var handleFailure = function(o){
                try{
                    if(!failureFn){
                	    alert(o.statusText);
                	}else{
                	    failureFn();
                	}
            	}catch(exception){
                    //alert(exception);
                }
                
                XYUI.hideLoading();
            };
            
            var callback = {
                success:handleSuccess,
                failure:handleFailure
            };
            
            if(postData && callerNameSpace){
                postData += "&_callerNameSpace=" + callerNameSpace;
            }else{
                postData = "&_callerNameSpace=" + callerNameSpace;
            }
            
            var request = YAHOO.util.Connect.asyncRequest('POST', sUrl, callback, postData);       
              
		},
		
		/**
		 * 打开一个<script language="javascript">xxx</script>的脚本页面。
		 *
		 * 参数：
		 *     sUrl : URL地址。
		 *     postData：格式为xx=xx&xx1=xx1的字符串。
		 *     errorMsg：当出错时的提示信息。
		 *     context：执行脚本时的上下文。
		 */
		openScript : function(sUrl, postData, errorMsg, scope, successFn, failureFn){
		    var handleSuccess = function(o){
		        try{
                	if(o.status == 200 && o.responseText !== undefined){                  	    
                        XYUI.evalScriptTags(o.responseText, scope);
                        if(successFn){
                            successFn(scope);
                        }
                	}else{
                	    if(errorMsg){
                	        alert(errorMsg);
                	    }else{
                            alert(o.statusText);
                        }
                	}
                }catch(exception){
                }
            };

            var handleFailure = function(o){
                if(failureFn){
                    failureFn(o);
                }else{
                    if(errorMsg){
                        alert(errorMsg);
                    }else{
               	        alert(o.statusText);
               	    }
              	}
            };
            
            var callback = {
                success:handleSuccess,
                failure:handleFailure
            };

            
            var request = YAHOO.util.Connect.asyncRequest('POST', sUrl, callback, postData);
		},				
		
		openDataSetSelectorWindow: function(dataSet, selectFunction, cancelFunction, width_, height_){ 
         	var width=500;
            var height = 400;
            if(width_) width = width_;
            if(height_) height = height_;
            
            window.open("do?sc=app:web:control.DataSetControl:/@querySelector&dataSet=" 
                    + dataSet + "&selectFn=" + selectFunction + "&cancelFn=" + cancelFunction,"数据选择", 
                    "toolbar=0,scrollbars=0,location=0,statusbar=0,menubar=0,resizable=1,width=" + width 
                    + ",height=" + height + ",left="+(window.screen.width-width)/2+",top="+(window.screen.height-height)/2 + '"');
        },
        
        /**
         * 打开一个数据选择对话框，并设置选择后的值。
         * dataSet：数据集
         * input：输入控件，用来寻找控件所在的form
         * prefix：数据控件的前缀
         * width_：对话框的宽度
         * height_：对话框的高度
         */
        openDataSetSelector: function(dataSet, input, prefix, width_, height_){
            var width=500;
            var height = 400;
            if(width_) width = width_;
            if(height_) height = height_;
            
            //input是否是在一个form中
            var inputForm;
            for(var i=0; i<document.forms.length; i++){
                var aform = document.forms[i];
                var inputElements = YAHOO.util.Dom.getElementsBy(function(el){
                    if(el == input){
                        return true;
                    }else{
                        return false;
                    }
                }, null, this.form, null);
                if(inputElements && inputElements.length > 0){
                    inputForm = aform;
                    break;
                }
            }
            
            //选择数据后的设置数据的函数
            var selectFn = function(ids, selector, selectorWindow, rows){
                
            };
            window.open("do?sc=app:web:control.DataSetControl:/@querySelector&dataSet=" 
                    + dataSet + "&selectFn=" + selectFunction + "&cancelFn=" + cancelFunction,"数据选择", 
                    "toolbar=0,scrollbars=0,location=0,statusbar=0,menubar=0,resizable=0,width=" + width 
                    + ",height=" + height + ",left="+(window.screen.width-width)/2+",top="+(window.screen.height-height)/2 + '"');
        },
        
        setDataFromSelector: function(aform, ids, selector, selectorWindow, rows){
        },
		
	    evalScriptTags: function(html, varScope) {
            var script_data = html.match(/<script.*?>((\n|\r|.)*?)<\/script>/g);
            if(script_data != null) {
                for(var i=0; i < script_data.length; i++) {
                    var script_only = script_data[i].replace(/<script.*?>/g, "");
                    script_only = script_only.replace(/<\/script>/g, "");            
                    if(varScope){
                        with(varScope){
                             eval(script_only);
                        }
                    }else{        
                        eval(script_only);                    
                    }
                }
            }
        },
        
        /**
         * 初始化下拉选择框的内容。
         */
        initComboxData: function(html){
            
        },
        
        getElementByName: function(el, name){
            if(!el) return null;
            
            var cc = YAHOO.util.Dom.getChildren (el);
            for(n=0; n<cc.length; n++){      
                
                if(cc[n] && cc[n].name && cc[n].name == name){                 
                    return cc[n];
                }else if(cc[n]){
                    alert(cc[n].nodeName);
                    var result = XYUI.getElementByName(cc[n], name);
                    if(result){
                        return result;
                    }
                }
            }
        },
        
        alertElement: function(el){
            if(el){
                var s = "";
                for(k in el){
                    s = s + k + "=" + el[k] + ",";
                }
                
                alert(s);
            }
        },
        
        /**
         * N对1的表格格式化。
         */
        n1Format : function(elCell, oRecord, oColumn, oData) { 
        },
        
        /**
         * 根据名称返回指定节点下的元素。
         */
        getChildsByName : function(element, name){
            var elements = YAHOO.util.Dom.getElementsBy(function(el){
                    if(el.name == name){
                        return true;
                    }else{
                        return false;
                    }
                }, null, element, null);
                
            return elements;
        }
	}
}