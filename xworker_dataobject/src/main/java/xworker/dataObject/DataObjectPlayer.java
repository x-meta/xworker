package xworker.dataObject;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.task.TaskManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DataObjectPlayer implements DataStoreListener {
    Thing thing;
    ActionContext actionContext;

    long currentIndex = 0;
    DataStore dataStore;
    List<DataObjectPlayerListener> listeners = new ArrayList<>();
    boolean autoPlay = false;
    boolean loop = false;
    long interval = 2000;
    boolean keepLast = false;
    boolean autoPlaying = false;

    public DataObjectPlayer(Thing dataObject, Thing condtion, Map<String, Object> params, ActionContext actionContext){
        dataStore = new DataStore(dataObject, condtion, actionContext);
        dataStore.addListener(this);
        dataStore.load(params);
    }

    public DataObjectPlayer(Thing thing, ActionContext actionContext){
        this.thing = thing;
        this.actionContext = actionContext;

        Thing dataObject = thing.doAction("getDataObject", actionContext);
        Thing condition = thing.doAction("getCondition", actionContext);
        Map<String, Object> conditionValues = thing.doAction("getConditionValues", actionContext);

        autoPlay = thing.doAction("isAutoPlay", actionContext);
        loop = thing.doAction("isLoop", actionContext);
        interval = thing.doAction("getInterval", actionContext);

        this.addListener(new DataObjectPlayerListener() {
            @Override
            public void play(DataObjectPlayer player, DataObject dataObject) {
                thing.doAction("play", actionContext, "player", this, "dataObject", dataObject);
            }

            @Override
            public void onNoData(DataObjectPlayer player) {
                thing.doAction("onNoData", actionContext, "player", this);
            }
        });

        dataStore = new DataStore(dataObject, condition, actionContext);
        dataStore.addListener(this);
        dataStore.load(conditionValues);
    }

    public void setParams(Map<String, Object> params){
        dataStore.load(params);
    }

    public void addListener(DataObjectPlayerListener listener){
        if(!listeners.contains(listener)){
            listeners.add(listener);
        }
    }

    public void removeListener(DataObjectPlayerListener listener){
        listeners.remove(listener);
    }

    public boolean isKeepLast() {
        return keepLast;
    }

    public void setKeepLast(boolean keepLast) {
        this.keepLast = keepLast;

        if(keepLast) {
            last();
        }
    }

    public void next(){
        currentIndex++;
        initCurrentIndex();
    }

    public void previous(){
        currentIndex--;
        initCurrentIndex();
    }

    /**
     * 播放当前索引位置的数据对象。
     */
    public void play(){
        playCurrentIndex();
    }

    /**
     * 停止自动播放，设置autoPaly=false。
     */
    public void stop(){
        autoPlay = false;
    }

    public void first(){
        currentIndex = 0;
        initCurrentIndex();
    }

    public void last(){
        long totalCount = dataStore.getPageInfo().getTotalCount();

        currentIndex = totalCount - 1;
        initCurrentIndex();
    }

    private void initCurrentIndex(){
        //System.out.println("initcurrentIndex: " + currentIndex);
        long totalCount = dataStore.getPageInfo().getTotalCount();
        if(currentIndex >= totalCount){
            this.currentIndex = totalCount - 1;
            if(autoPlay && loop){
                currentIndex = 0;
            }else {
                this.currentIndex = totalCount - 1;
            }
        }

        if(currentIndex < 0){
            currentIndex = 0;
        }

        PageInfo pageInfo = dataStore.getPageInfo();
        long start = pageInfo.getStart();
        long limit = pageInfo.getLimit();
        if(currentIndex >= start && currentIndex < (start + limit)){
            //当前数据对象存在，展示
            playCurrentIndex();
        }else{
            //当前数据对象不在当前页，先加载
            pageInfo.setPageByOffset(currentIndex);
            dataStore.reload();
        }
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    private void playCurrentIndex(){
        long totalCount = dataStore.getPageInfo().getTotalCount();
        if(currentIndex >= totalCount){
            //已经到达记录的末尾了
            return;
        }

        PageInfo pageInfo = dataStore.getPageInfo();
        long start = pageInfo.getStart();
        long limit = pageInfo.getLimit();

        if(currentIndex >= start && currentIndex < (start + limit)){
            int index = (int) (currentIndex % limit);
            List<DataObject> datas = dataStore.getDatas();
            if(index < datas.size()) {
                DataObject dataObject = datas.get(index);
                for(DataObjectPlayerListener listener : listeners){
                    listener.play(this, dataObject);
                }
            }
        }

        autoPlayNext();
    }

    @Override
    public void onReconfig(DataStore dataStore, Thing dataObject) {
    }

    @Override
    public void onLoaded(DataStore dataStore) {
        if(dataStore.getPageInfo().getTotalCount() <= 0){
            for(DataObjectPlayerListener listener : listeners){
                listener.onNoData(this);
            }

            autoPlayNext();
        }else {
            this.playCurrentIndex();
        }
    }

    private void autoPlayNext(){
        if(autoPlay){
            synchronized (this) {
                if(autoPlaying){
                    return;
                }

                autoPlaying = true;
                //总是试图刷新
                TaskManager.getScheduledExecutorService().schedule(new Runnable() {
                    @Override
                    public void run() {
                        autoPlaying = false;
                        if (keepLast) {
                            last();
                        } else {
                            next();
                        }

                    }
                }, interval, TimeUnit.MILLISECONDS);
            }
        }
    }

    @Override
    public void onChanged(DataStore dataStore) {
    }

    @Override
    public void beforeLoad(DataStore dataStore, Thing condition, Map<String, Object> params) {

    }

    public static void create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        DataObjectPlayer player = new DataObjectPlayer(self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), player);
    }
}
