package xworker.chart.jfree;

import java.awt.Font;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;

public class JfreeInit {
	public static boolean inited = false;

	public static void init() {
		inited = false;
		if (!inited) {
			// 创建主题样式
			StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
			// 设置标题字体
			standardChartTheme.setExtraLargeFont(new Font("隶书", Font.BOLD, 20));
			// 设置图例的字体
			standardChartTheme.setRegularFont(new Font("宋书", Font.PLAIN, 12));
			// 设置轴向的字体
			standardChartTheme.setLargeFont(new Font("宋书", Font.PLAIN, 13));
			// 应用主题样式
			ChartFactory.setChartTheme(standardChartTheme);
			
			inited = true;
		}
	}
}
