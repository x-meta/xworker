package xworker.dataObject;

public interface DataObjectPlayerListener {
    public void play(DataObjectPlayer player, DataObject dataObject);

    public void onNoData(DataObjectPlayer player);
}
