package xworker.swt.xwidgets.clock;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class LineClockDrawer extends AbstractDrawer implements ClockDrawer {
	public LineClockDrawer(Thing thing, ActionContext actionContext) {
		super(thing, actionContext);
	}
	
	@Override
	public void drawClock(Date date, float x, float y, float width, float height,  GC gc) {
		// Hard code the padding at 10 pixels for now
		float padding = 15;

		// 起始点
		float cX = x + padding;
		float cY = y + height  / 3;

		//Tick Styles
		int tickLen = 5;  // short tick
		int medTickLen = 10;  // at 5-minute intervals
		int longTickLen = 15; // at the quarters
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
		
		boolean showArea = this.isShowArea();
		boolean showSecond = this.isShowSecond();
		boolean showMinute = this.isShowMinute();
		boolean showHour = this.isShowHour();
		boolean showAmPm = this.isShowAmPm();
		
		try {
			// Draw a tick for each "second" (1 through 60)
			float interval = (width - padding * 2) / 60;
			
			//----------------------------显示指针-------------------------
			gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_BLACK));
			// Calculate the angle of the second hand
			Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
			calendar.setTimeInMillis(date.getTime());
			
			//secondHand.moveTo((float)cX, (float)cY);
			//secondHand.moveTo(0, longTickLen / 2 );
			secondHand.moveTo(0, 0);
	        secondHand.lineTo((float)0, (float)(tickLen));
			
			//Shape translatedSecondHand = secondHand.createTransformedShape(Transform.makeTranslation(0f, 5, 0));
	        
	        double second = (double)(calendar.get(Calendar.SECOND));
	   
	        double secondWidth = interval * second;
	        
	        //显示秒针划过的区域
	        if(showArea && showSecond) {
		        gc.setBackground(secondAreaClolor);	        
		        gc.fillRectangle((int) cX, (int) (cY - longTickLen), (int) secondWidth, (int)(longTickLen * 2));
	        }
	        
	        double minute = (double)(calendar.get(Calendar.MINUTE)) + 
	                (double)(calendar.get(Calendar.SECOND))/60.0;
	      
	        float minuteWidth = (float)(interval * minute);
	        //显示分针划过的区域	        
	        if(showArea && showMinute) {
	        	gc.setBackground(minuteAreaClolor);
	        	gc.fillRectangle((int) cX, (int) (cY - medTickLen), (int) minuteWidth, (int)(medTickLen * 2));		       
	        }
	        	        
	        double hour = (double)(calendar.get(Calendar.HOUR_OF_DAY)%12) + 
	                (double)(calendar.get(Calendar.MINUTE))/60.0;
	   
	     
	        double hourWidth = (float)(interval * hour * 5);
	        
	        //显示时针划过的区域
	        if(showArea && showHour) {
	        	gc.setBackground(hourAreaClolor);
	        	gc.fillRectangle((int) cX, (int) (cY - tickLen), (int) hourWidth, (int)(tickLen * 2));		       
	        }
	        
	        if(showSecond) {
	        	gc.drawLine((int) (secondWidth + padding), (int) (cY), (int) (secondWidth + padding), (int)(cY + longTickLen));
	        }	        
	        if(showMinute) {
	        	gc.drawLine((int) (minuteWidth + padding), (int) (cY), (int) (minuteWidth + padding), (int)(cY + medTickLen));    
	        }
	        if(showHour) {
	        	gc.drawLine((int) (hourWidth + padding), (int) (cY), (int) (hourWidth + padding), (int)(cY + tickLen));    
	        }
	        
	        gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_BLACK));
	        
			//------------------------------显示表盘-----------------------------
			gc.drawLine((int) cX, (int) cY, (int) (width - padding), (int) cY);

			
			for ( int i=0; i<= 60; i++){
			    // default tick length is short
			    int len = tickLen;
			    if ( i % 15 == 0 ){
			        // Longest tick on quarters (every 15 ticks)
			        len = longTickLen;
			    } else if ( i % 5 == 0 ){
			        // Medium ticks on the '5's (every 5 ticks)
			        len = medTickLen;
			    }
	
			    float it = i * interval  + padding;
			    gc.drawLine((int) it, (int) cY, (int) it, (int)(cY - len)); 
			}
				
			gc.setFont(textFont);
			
	        //-----------------------显示表盘上的数字---------------------
	        gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
	        gc.setAlpha(255);
	        int charWidth = gc.getFontMetrics().getAverageCharWidth(); 
		    int charHeight = gc.getFontMetrics().getHeight();
	        for ( int i=0; i<=12; i++){
           	    // default tick length is short
			    int len = longTickLen;
			  			    
			    String numStr = ""+i;
			    float it = i * interval * 5  + padding;
			    if(numStr.length() == 1) {
			    	gc.drawString(numStr, (int)(it -charWidth/2), (int)(cY -len - charHeight), true);
			    }else {
			    	gc.drawString(numStr, (int)(it -charWidth), (int)(cY -len - charHeight), true);
			    }			   
			}
	        for ( int i=0; i<=12; i++){
           	    // default tick length is short
			    String numStr = ""+ (i * 10);
			    float it = i * interval * 10  + padding;
			    if(numStr.length() == 1) {
			    	gc.drawString(numStr, (int)(it -charWidth/2), (int)(cY  + charHeight), true);
			    }else {
			    	gc.drawString(numStr, (int)(it -charWidth), (int)(cY  + charHeight), true);
			    }			   
			}
	        
	        
	        //-------------------------------画上午和下午------------------------------
	        if(showAmPm) {
	        	SimpleDateFormat sf = new SimpleDateFormat("hh:mm:ss");
	        	String amPm = sf.format(date);
	        	if(Calendar.AM == calendar.get(Calendar.AM_PM)){
	        		amPm = amPm + " AM";
	        	}else {
	        		amPm = amPm + " PM";
	        	}
	        		        	
	        	gc.drawString(amPm, (int)(cX + width / 2 - charWidth * 6), (int)(cY  + height / 2 -charHeight/2), true);
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
		}
	}

	public static LineClockDrawer create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		return new LineClockDrawer(self, actionContext);
	}

}
