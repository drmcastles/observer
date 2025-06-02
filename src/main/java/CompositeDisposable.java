import java.util.*;

/**
 * Составной Disposable для управления группой подписок
 *
 * @Author Stepan
 */
class CompositeDisposable implements Disposable {
    private final List<Disposable> disposables = new ArrayList<>();
    private boolean disposed = false;

    public void add(Disposable disposable) {
        if (disposed) {
            disposable.dispose();
        } else {
            disposables.add(disposable);
        }
    }

    @Override
    public void dispose() {
        disposed = true;
        disposables.forEach(Disposable::dispose);
    }

    @Override
    public boolean isDisposed() {
        return disposed;
    }
}
