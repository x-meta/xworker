package xworker.lang.executor.services;

import xworker.lang.executor.ExecutorService;

public abstract class AbstractRequestService implements ExecutorService {
    @Override
    public void setLogLevel(byte level) {
    }

    @Override
    public byte getLogLevel() {
        return 0;
    }

    @Override
    public void trace(String TAG, String message) {
    }

    @Override
    public void trace(String TAG, String message, Throwable t) {
    }

    @Override
    public void trace(String TAG, String format, Object arg) {
    }

    @Override
    public void trace(String TAG, String format, Object arg1, Object arg2) {
    }

    @Override
    public void trace(String TAG, String format, Object... arguments) {
    }

    @Override
    public void debug(String TAG, String message) {
    }

    @Override
    public void debug(String TAG, String message, Throwable t) {
    }

    @Override
    public void debug(String TAG, String format, Object arg) {
    }

    @Override
    public void debug(String TAG, String format, Object arg1, Object arg2) {
    }

    @Override
    public void debug(String TAG, String format, Object... arguments) {
    }

    @Override
    public void info(String TAG, String message) {
    }

    @Override
    public void info(String TAG, String message, Throwable t) {
    }

    @Override
    public void info(String TAG, String format, Object arg) {
    }

    @Override
    public void info(String TAG, String format, Object arg1, Object arg2) {
    }

    @Override
    public void info(String TAG, String format, Object... arguments) {
    }

    @Override
    public void warn(String TAG, String message) {
    }

    @Override
    public void warn(String TAG, String message, Throwable t) {
    }

    @Override
    public void warn(String TAG, String format, Object arg) {
    }

    @Override
    public void warn(String TAG, String format, Object arg1, Object arg2) {
    }

    @Override
    public void warn(String TAG, String format, Object... arguments) {
    }

    @Override
    public void error(String TAG, String message) {
    }

    @Override
    public void error(String TAG, String message, Throwable t) {
    }

    @Override
    public void error(String TAG, String format, Object arg) {
    }

    @Override
    public void error(String TAG, String format, Object arg1, Object arg2) {
    }

    @Override
    public void error(String TAG, String format, Object... arguments) {
    }

    @Override
    public void print(Object message) {
    }

    @Override
    public void println(Object message) {
    }

    @Override
    public void errPrint(Object message) {
    }

    @Override
    public void errPrintln(Object message) {
    }

    @Override
    public boolean isSupportLog() {
        return false;
    }

    @Override
    public boolean isSupportRequest() {
        return true;
    }
}
