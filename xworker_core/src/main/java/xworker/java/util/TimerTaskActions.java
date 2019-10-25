/*
 * Copyright 2007-2016 The xworker.org.
 *
 * Licensed to the X-Meta under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The X-Meta licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xworker.java.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.cache.ThingEntry;

public class TimerTaskActions {
	private static Logger logger = LoggerFactory.getLogger(TimerTaskActions.class);
	
    public static Object run(ActionContext actionContext) throws ParseException{
        Thing self = actionContext.getObject("self");
        World world = World.getInstance();        
        
        Thing timerTaskThing = self;
        //Timer的实例
        Thing timerThing = world.getThing(self.getString("timer"));
        if(timerThing == null){
            logger.warn("TimerTask: timer not exists, path=" + self.getString("timer"));
            return null;
        }
        Timer timer = (Timer) timerThing.doAction("getTimer", actionContext, "self", timerThing);
        //log.info("timer=" + timer);
        
        //创建新的动作上下文
        final ActionContext ac = new ActionContext();
        if(self.getString("attributes") != null){
            for(String attr : self.getString("attributes").split("[,]")){
                attr = attr.trim();
                ac.put(attr, actionContext.get(attr));
            }
        }
        
        //创建TimerTask
        final ThingEntry taskThing = new ThingEntry(timerTaskThing);
        TimerTask timerTask = new TimerTask(){
        	public void run(){
                try{
                    //log.info("taskThing=" + taskThing.getThing().metadata.path);
                    //log.info("actionThing=" + taskThing.getThing().getActionThing("doTimerTask"));
                    taskThing.getThing().doAction("doTimerTask", ac);
                }catch(Exception e){ 
                    logger.error("TimerTask: execute error",  e);
                }
            }
        };
        
        //定时任务
        Date firstTime = null;
        long delay = 0;
        int period = self.getInt("period");
        boolean fixedRate = self.getBoolean("fixedRate");        
        if(self.getStringBlankAsNull("firstTime") != null){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            firstTime = sf.parse(self.getString("firstTime"));
        }
        if(firstTime == null){
            delay = self.getInt("delay");
        }
        
        if(firstTime != null){
            if(period == 0){
                timer.schedule(timerTask, firstTime);
            }else if(fixedRate){
                timer.scheduleAtFixedRate(timerTask, firstTime, period);
            }else{
                timer.schedule(timerTask, firstTime, period);
            }
        }else{
            if(period == 0){
                timer.schedule(timerTask, delay);
            }else if(fixedRate){
                timer.scheduleAtFixedRate(timerTask, delay, period);
            }else{        
                timer.schedule(timerTask, delay, period);
            }
        }
        
        //保存变量
        String varName = self.getStringBlankAsNull("varName");
        if(varName != null ){
            Bindings bindings = (Bindings) self.doAction("getVarScope", actionContext);
            if(bindings != null){
                bindings.put(varName, timerTask);
            }
        }
        
        return timerTask;
    }

}