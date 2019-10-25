package xworker.swt.xwidgets.clock;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Transform;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class AnalogClockDrawer extends AbstractDrawer implements ClockDrawer {
	public AnalogClockDrawer(Thing thing, ActionContext actionContext) {
		super(thing, actionContext);
	}
	
	@Override
	public void drawClock(Date date, float x, float y, float width, float height,  GC gc) {
		// Hard code the padding at 10 pixels for now
		float padding = 10;

		// float radius
		double r = Math.min(width, height)/2-padding;

		// Center point.
		float cX = x + width / 2;
		float cY = y + height / 2;

		//Tick Styles
		int tickLen = 7;  // short tick
		int medTickLen = 14;  // at 5-minute intervals
		int longTickLen = 21; // at the quarters
		gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
		gc.fillRectangle((int) x, (int) y, (int) width, (int) height);
		gc.setAntialias(SWT.ON);
		Color tickColor = new Color(gc.getDevice(), 0x00, 0x00, 0x00);	  
		Color hourAreaClolor = new Color(gc.getDevice(), 0xff, 0x51, 0x51);
		Color minuteAreaClolor = new Color(gc.getDevice(), 0x84, 0xc1, 0xff); 
		Color secondAreaClolor = new Color(gc.getDevice(), 0x93, 0xff, 0x93); 
		Font textFont = new Font(gc.getDevice(), new FontData("", 14, SWT.BOLD));
		Path secondHand = new Path(gc.getDevice());
		Path minuteHand = new Path(gc.getDevice());
		Path hourHand = new Path(gc.getDevice());
		Path ticksPath = new Path(gc.getDevice());
		Transform tr = new Transform(gc.getDevice());
		
		boolean showArea = this.isShowArea();
		boolean showSecond = this.isShowSecond();
		boolean showMinute = this.isShowMinute();
		boolean showHour = this.isShowHour();
		boolean showAmPm = this.isShowAmPm();
		
		try {
			//------------------------------显示表盘-----------------------------
			// Draw a tick for each "second" (1 through 60)
			for ( int i=1; i<= 60; i++){
			    // default tick length is short
			    int len = tickLen;
			    if ( i % 15 == 0 ){
			        // Longest tick on quarters (every 15 ticks)
			        len = longTickLen;
			    } else if ( i % 5 == 0 ){
			        // Medium ticks on the '5's (every 5 ticks)
			        len = medTickLen;
			    }
	
			    double di = (double)i; // tick num as double for easier math
	
			    // Get the angle from 12 O'Clock to this tick (radians)
			    double angleFrom12 = di/60.0*2.0*Math.PI;
	
			    // Get the angle from 3 O'Clock to this tick
			        // Note: 3 O'Clock corresponds with zero angle in unit circle
			        // Makes it easier to do the math.
			    double angleFrom3 = Math.PI/2.0-angleFrom12;
	
			    // Move to the outer edge of the circle at correct position
			    // for this tick.
			    ticksPath.moveTo(
			            (float)(cX+Math.cos(angleFrom3)*r),
			            (float)(cY-Math.sin(angleFrom3)*r)
			    );
	
			    // Draw line inward along radius for length of tick mark
			    ticksPath.lineTo(
			            (float)(cX+Math.cos(angleFrom3)*(r-len)),
			            (float)(cY-Math.sin(angleFrom3)*(r-len))
			    );
			}
	
			// Draw the full shape onto the graphics context.
			gc.setLineJoin(SWT.JOIN_ROUND);
			gc.setLineWidth(2);
			gc.setForeground(tickColor);			
			gc.drawOval((int) ( cX - r), (int) (cY - r), (int) r * 2,(int)  r * 2);
			gc.drawPath(ticksPath);
			//gc.setLineCap(SWT.CAP);
			
			gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_BLACK));			
			gc.setFont(textFont);
						
			//----------------------------显示指针-------------------------
			gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_BLACK));
			gc.fillArc((int) cX-8, (int) cY-8, 16, 16, 0, 360);
			// Calculate the angle of the second hand
			Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
			calendar.setTimeInMillis(date.getTime());
			
			//secondHand.moveTo((float)cX, (float)cY);
			//secondHand.moveTo(0, longTickLen / 2 );
			secondHand.moveTo(0, 0);
	        secondHand.lineTo((float)0, (float)(r-tickLen));
			
			//Shape translatedSecondHand = secondHand.createTransformedShape(Transform.makeTranslation(0f, 5, 0));
	        
	        double second = (double)(calendar.get(Calendar.SECOND));
	        double secondAngle = second/60.0*360 - 180;//*Math.PI;
	        
	        //显示秒针划过的区域
	        if(showArea && showSecond) {
		        gc.setBackground(secondAreaClolor);	        
		        float secondWidth = (float)((r-medTickLen));
		        gc.fillArc((int) (cX - secondWidth), (int) (cY - secondWidth), 
		        		(int) (2 * secondWidth), (int) (2 * secondWidth), (int) (90), (int)-(secondAngle + 180));
	        }
	        
	        
	        double minute = (double)(calendar.get(Calendar.MINUTE)) + 
	                (double)(calendar.get(Calendar.SECOND))/60.0;
	        double minuteAngle = minute/60.0*360 - 180;	        
	        //显示分针划过的区域	        
	        if(showArea && showMinute) {
	        	gc.setBackground(minuteAreaClolor);
		        float minuteWidth = (float)((r-medTickLen) * 0.9);
		        gc.fillArc((int) (cX - minuteWidth), (int) (cY - minuteWidth), 
		        		(int) (2 * minuteWidth), (int) (2 * minuteWidth), (int) (90), (int) -(minuteAngle + 180));
	        }
	        
	        double hour = (double)(calendar.get(Calendar.HOUR_OF_DAY)%12) + 
	                (double)(calendar.get(Calendar.MINUTE))/60.0;
	        double angle = (hour/12.0)*360 - 180 ;
	        
	        //显示时针划过的区域
	        if(showArea && showHour) {
		        gc.setBackground(hourAreaClolor);	        
		        float hourWidth = (float)((r-longTickLen)*0.75);
		        gc.fillArc((int) (cX - hourWidth), (int) (cY - hourWidth), 
		        		(int) (2 * hourWidth), (int) (2 * hourWidth), (int) 90,  (int) -(angle + 180));
		    }
	        
	        gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_BLACK));
	        	        
	        tr.translate(cX, cY);
	        if(showSecond) {
	        	tr.rotate((float) secondAngle);	        
	        	gc.setTransform(tr);	        
	        	gc.drawPath(secondHand);
	        	tr.rotate((float) -secondAngle);	
	        }
	        
	        if(showMinute) {
		        minuteHand.moveTo((float)0, (float)0);
		        minuteHand.lineTo((float)0+4, (float)0);
		        minuteHand.lineTo((float)0+1, (float)((r-medTickLen) * 0.9));
		        minuteHand.lineTo((float)0-1, (float)((r-medTickLen) * 0.9));
		        minuteHand.lineTo((float)0-4, (float)0);
		        minuteHand.close();
		        
		        //Shape translatedMinuteHand = minuteHand.createTransformedShape(Transform.makeTranslation(0f, 5, 0));
	                    
		        tr.rotate((float)minuteAngle);
		        gc.setTransform(null);
		        gc.setTransform(tr);
		        
		        //g.setColor(0x000000);
		        gc.fillPath(minuteHand);
		        tr.rotate((float) -minuteAngle);	
	        }
	        
	        if(showHour) {
		        hourHand.moveTo((float)0, (float)0);
		        hourHand.lineTo((float)0+6, (float)0);
		        hourHand.lineTo((float)0+2, (float)((r-longTickLen)*0.75));
		        hourHand.lineTo((float)0-2, (float)((r-longTickLen)*0.75));
		        hourHand.lineTo((float)0-6, (float)0);
		        hourHand.close();
		        
		        tr.rotate((int) angle);
		        gc.setTransform(null);
		       
		        gc.setTransform(tr);
		        gc.fillPath(hourHand);
	        }
	        
	        gc.setTransform(null);
	        
	        //-----------------------显示表盘上的数字---------------------
	        gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
	        gc.setAlpha(255);
	        int charWidth = gc.getFontMetrics().getAverageCharWidth(); 
		    int charHeight = gc.getFontMetrics().getHeight();
	        for ( int i=1; i<=12; i++){
			    // Calculate the string width and height so we can center it properly
			    String numStr = ""+i;
			    
			    double di = (double)i;  // number as double for easier math

			    // Calculate the position along the edge of the clock where the number should
			    // be drawn
			     // Get the angle from 12 O'Clock to this tick (radians)
			    double angleFrom12 = di/12.0*2.0*Math.PI;

			    // Get the angle from 3 O'Clock to this tick
			        // Note: 3 O'Clock corresponds with zero angle in unit circle
			        // Makes it easier to do the math.
			    double angleFrom3 = Math.PI/2.0-angleFrom12;

			    // Get diff between number position and clock center
			    int tx = (int)(Math.cos(angleFrom3)*(r-longTickLen - 10));
			    int ty = (int)(-Math.sin(angleFrom3)*(r-longTickLen -10));

					    // Translate the graphics context by delta between clock center and
			    // number position
		
			    // Draw number at clock center.
			    if(numStr.length() == 1) {
			    	gc.drawString(numStr, (int)(tx + cX -charWidth/2), (int)(cY + ty-charHeight/2), true);
			    }else {
			    	gc.drawString(numStr, (int)(tx + cX -charWidth), (int)(cY + ty-charHeight/2), true);
			    }

			    // Undo translation		
			}
	        
	        
	        //-------------------------------画上午和下午------------------------------
	        if(showAmPm) {
	        	String amPm = null;
	        	if(Calendar.AM == calendar.get(Calendar.AM_PM)){
	        		amPm = "AM";
	        	}else {
	        		amPm = "PM";
	        	}
	        	
	        	gc.drawString(amPm, (int)(cX + r / 2 - charWidth), (int)(cY  -charHeight/2), true);
	        }
			
		}finally {
			tickColor.dispose();
			hourAreaClolor.dispose();
			minuteAreaClolor.dispose();
			secondAreaClolor.dispose();
			ticksPath.dispose();
			textFont.dispose();		
			secondHand.dispose();
			minuteHand.dispose();
			hourHand.dispose();
			tr.dispose();
		}
	}

	public static AnalogClockDrawer create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		return new AnalogClockDrawer(self, actionContext);
	}
}
