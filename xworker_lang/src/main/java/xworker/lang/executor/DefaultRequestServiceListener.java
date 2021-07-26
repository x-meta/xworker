package xworker.lang.executor;

public interface DefaultRequestServiceListener {
    public void requestAdded(DefaultRequestService defaultRequestService, Request request);

    public void requestRemoved(DefaultRequestService defaultRequestService, Request request);

    public void requestUpdated(DefaultRequestService defaultRequestService, Request request);
}
