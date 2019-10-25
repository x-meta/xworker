<#import "common.ftl" as lib>
<script language="javascript">
    document.open();
    
    //size
    var ${object.name} = new Diagram();  
    <#if object["size@"]?size != 0>  
      <#assign sizeObj = object["size@"][0]/>
    <#else>
      <#assign sizeObj = {}>
    </#if>    
    ${object.name}.SetFrame(${sizeObj.screenLeft?default("0")}, ${sizeObj.screenTop?default(0)}, ${sizeObj.screenRight?default("100")},${sizeObj.screenBottom?default(100)});
    ${object.name}.SetBorder(${sizeObj.worldLeftX?default("0")}, ${sizeObj.worldRightX?default(0)}, ${sizeObj.worldBottonY?default("100")},${sizeObj.worldTopY?default(100)});
    
    //title
    <#if object["title@"]?size != 0>
      <#assign titleObj = object["title@"][0]/>
    <#else>
      <#assign titleObj = {}>
    </#if>
    ${object.name}.SetText("${titleObj.xTitle?if_exists}","${titleObj.yTitle?if_exists}","${titleObj.title?if_exists}");
    
    //scale
    <#if object["scale@"]?size != 0>
      <#assign scaleObj = object["scale@"][0]/>
    <#else>
      <#assign scaleObj = {}>
    </#if>
    <#if scaleObj.xScale?exists && scaleObj.xScale != "1">
    ${object.name}.XScale = ${scaleObj.xScale};
    </#if>
    <#if scaleObj.yScale?exists && scaleObj.yScale != "1">
    ${object.name}.YScale = ${scaleObj.yScale};
    </#if>
    <#if scaleObj.xScalePosition?exists && scaleObj.xScalePosition != "bottom">
    ${object.name}.XScalePosition = "${scaleObj.xScalePosition}";
    </#if>
    <#if scaleObj.yScalePosition?exists && scaleObj.yScalePosition != "left">
    ${object.name}.YScalePosition = "${scaleObj.yScalePosition}";
    </#if>
    <#if scaleObj.xGridDelta?exists && scaleObj.xGridDelta != "0">
    ${object.name}.XGridDelta = ${scaleObj.xGridDelta};
    </#if>
    <#if scaleObj.yGridDelta?exists && scaleObj.yGridDelta != "0">
    ${object.name}.YGridDelta = ${scaleObj.yGridDelta};
    </#if>
    <#if scaleObj.xSubGrids?exists && scaleObj.xSubGrids != "0">
    ${object.name}.XSubGrids = ${scaleObj.xSubGrids};
    </#if>
    <#if scaleObj.ySubGrids?exists && scaleObj.ySubGrids != "0">
    ${object.name}.YSubGrids = ${scaleObj.ySubGrids};
    </#if>
    <#if scaleObj.gridColor?exists>
    ${object.name}.SetGridColor("${scaleObj.gridColor}", "${scaleObj.subGridColor?if_exists}");
    </#if>
    <#if scaleObj.xGridColor?exists>
    ${object.name}.SetXGridColor("${scaleObj.xGridColor}", "${scaleObj.xSubGridColor?if_exists}");
    </#if>
    <#if scaleObj.yGridColor?exists>
    ${object.name}.SetYGridColor("${scaleObj.yGridColor}", "${scaleObj.ySubGridColor?if_exists}");
    </#if>
    
    //画出图表
    ${object.name}.Draw("${object.drawColor?default("#80FF80")}","${object.textColor?default("#0000FF")}",${object.isScaleText?default("true")},"${object.toolTipText?if_exists}","${object.onClickAction?if_exists}","${object.onMouseoverAction?if_exists}", "${object.onMouseoutAction?if_exists}");
    
    //画具体的数据图形，柱状图、直线、饼.......
    //柱状图
    var datas = [];
    <#assign datas = object["datas@"]>
    <#list datas as data>        
    var ${data.name?if_exists}s = [];
    <#if data.datasName?exists && data.datasName != "">
    <${r"#"}list ${data.datasName} as data>
    ${data.name?if_exists}s[${r"$"}{data_index}] = ${r"$"}{data?default(0)};
    </${r"#"}list>
    <#else>
    <#list data["constantValue@"] as d>
    ${data.name?if_exists}s[${d_index}] = ${d.value?default(0)};
    </#list>
    </#if>
    datas[${data_index}] = new DiagramData("${data.name?if_exists}", "", "${data.color?if_exists}", ${data.name?if_exists}s);
    </#list>
    
    drawDiagramBox(${sizeObj.worldLeftX?default(0)}, ${sizeObj.worldRightX?default(100)}, 0, datas);
    document.close();
    
    function DiagramData(name, label, color, datas){
        this.name = name;
        this.label = label;
        this.color = color;
        this.datas = datas;
    }
    
    /*
      使用JavaScript画柱状图
     */
    function drawDiagramBox(leftX, rightX, xDelta, datas){
        var xWidth = rightX - leftX;
        
        //获取柱状图的数量
        var amount = 0;
        for(i=0; i<datas.length; i++){
            if(amount < datas[i].datas.length){
                amount = datas[i].datas.length;
            }            
        }
        var deltaX = (2 * datas.length + 1) * amount + 1;
        
        //计算一个柱状图的宽度
        deltaX = xWidth / deltaX;
        
        //画出柱状图
        for(i = 0; i<amount; i++){
            for(n = 0; n<datas.length; n++){
                data = datas[n];
                if(i <= data.datas.length){
                    var left = ${object.name}.ScreenX((((2 * datas.length + 1) *i+1) + 2 * n) * deltaX);
                    var top = ${object.name}.ScreenY(data.datas[i]);
                    var right = diagram.ScreenX((((2 * datas.length + 1)*i+1) + 2 * n) * deltaX + (2 * deltaX));
                    var bottom = ${object.name}.ScreenY(0);
                    
                    new Box(left, top, right, bottom, data.color, "", "");                        
                }
            }
        }
    }
</script>