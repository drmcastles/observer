/**
 * Базовая реализация Disposable
 * @Author Stepan
 */
class DefaultDisposable implements Disposable {
    private volatile boolean disposed = false;

    @Override
    public void dispose() {
        disposed = true;
    }

    @Override
    public boolean isDisposed() {
        return disposed;
    }
}

