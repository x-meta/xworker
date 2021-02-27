package xworker.util;

public class UtilMath {
	/**
	 * 获取x1,y2相对于x2，y2旋转了digree角度后的坐标。
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param digree
	 * @return
	 */
	public static Point rotateBy(double x1, double y1, double x2, double y2, double digree){
		double x = (x1 - x2) * Math.cos(digree) - (y1 - y2) * Math.sin(digree) + x2;
		double y = (y1 - y2) * Math.cos(digree) + (x1 - x2) * Math.sinh(digree) + y2;
		
		return new Point(x, y);
	}
	
	/**
	 * 计算两点之间的距离。
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static double getDistance(double x1, double y1, double x2, double y2){
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}
	
	/**
	 * （x1, y1)和(x2, y2)组成一条直线，求以(x1, y1)为起点在直线上指定距离离(x2, y2)最短的一个点。
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param distance
	 * @return
	 */
	public static Point getPointByDistance(double x1, double y1, double x2, double y2 , double distance){
		double degree = UtilMath.getDegree(x1, y1, x2, y2);
		return getPointByDegree(x1, y1, degree, distance);
		/*
		double f = (x2 - x1) / (y2 - y1);
		double s1 = Math.sqrt(Math.pow(distance, 2) / (Math.pow(f, 2) + 1));
		double yr1 = s1 + y1;
		double yr2 = y1 - s1;
		double xr1 = (yr1 - y1) * f + x1;
		double xr2 = (yr2 - y1) * f + x1;
		
		double r1 = Math.pow(xr1 - x2, 2) + Math.pow(yr1 - x2, 2);
		double r2 = Math.pow(xr2 - x2, 2) + Math.pow(yr2 - x2, 2);
		
		if(r1 > r2){
			return new Point(xr2, yr2);
		}else{
			return new Point(xr1, yr1);
		}*/
	}
	
	/**
	 * 返回亮点形成的角度。
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static double getDegree(double x1, double y1, double x2, double y2){
		return Math.atan2(y2 - y1, x2 - x1);
	}
	
	/**
	 * 根据角度和距离，获得从点(x1, y1)出发的目标点。
	 * 
	 * @param x1
	 * @param y1
	 * @param degree
	 * @param distance
	 * @return
	 */
	public static Point getPointByDegree(double x1, double y1, double degree, double distance){
		double x = x1 + distance * Math.cos(degree);
		double y = y1 + distance * Math.sin(degree);
		
		return new Point(x, y);
	}
	
	public static void main(String args[]){
		try{
			System.out.println(Math.atan2(0, 1) * 180 / Math.PI);
			
			System.out.println(getDegree(0, 0, 0, -20) * 180 / Math.PI);
			
			System.out.println(UtilMath.getPointByDistance(0, 0, 0, -20, 4));
			
			System.out.println(UtilMath.getPointByDegree(0,  0, Math.PI / 4, 4));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
