package xworker.html.charts;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.html.HtmlUtil;

public class ECharts {
	public static String toHtml(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		//引入js
		HtmlUtil.addHeader("ECharts", "<script src=\"" + self.getString("jsUrl") + "\"></script>", actionContext);
		
		//div部分
		String name = self.getMetadata().getName();
		StringBuilder sb = new StringBuilder();
		sb.append("<div id=\"");
		sb.append(name);
		sb.append("\" style=\"width: ");
		sb.append(self.getString("width"));
		sb.append(";height:");
		sb.append(self.getString("height"));
		sb.append(";\"></div>");
		
		//生成JavaScript
		Thing js = new Thing("xworker.html.base.scripts.JavaScript/@Code");		
		StringBuilder code = new StringBuilder();
		code.append("var ");
		code.append(name);
		code.append("Chart = echarts.init(document.getElementById('");
		code.append(name);
		code.append("'));\n");

		code.append("var ");
		code.append(name);
		code.append("Option = ");
		code.append(self.getString("option"));
		code.append(";\n");
		
		code.append(name);
		code.append("Chart.setOption(");
		code.append(name);
		code.append("Option);\n");
		
		//动态加载
		if(self.getBoolean("dynamic")) {			
			code.append(name + "Chart.showLoading();\n");
			code.append("$.get('");
			code.append(self.getString("dynamicLoadUrl"));
			code.append("').done(function (data) {\n");
			code.append("    var option = eval(data);\n");
			code.append("    " + name + "Chart.hideLoading();\n");
			code.append("    " + name + "Chart.setOption(option);\n");
			code.append("});\n");
			
			if(self.getLong("timer") > 0) {
				code.append("setInterval(function(){\n");
				code.append("    " + name + "Chart.showLoading();\n");
				code.append("    $.get('");
				code.append(self.getString("dynamicLoadUrl"));
				code.append("').done(function (data) {\n");
				code.append("        var option = eval(data);\n");
				code.append("        " + name + "Chart.hideLoading();\n");
				code.append("        " + name + "Chart.setOption(option);\n");
				code.append("    });\n");
				code.append("}, " + self.getString("timer") + ");");
			}
		}
		js.set("code", code.toString());
		HtmlUtil.addBottomJavaScriptThing(js, actionContext);
		
		return sb.toString();
	}
}
