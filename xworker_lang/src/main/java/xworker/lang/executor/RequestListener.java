package xworker.lang.executor;

public interface RequestListener {
    public void readed(Request request);

    public void finished(Request request);
}
