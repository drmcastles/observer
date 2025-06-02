class SafeObserver<T> implements Observer<T> {
    private final Observer<? super T> actual;
    private final DefaultDisposable disposable;

    SafeObserver(Observer<? super T> actual, DefaultDisposable disposable) {
        this.actual = actual;
        this.disposable = disposable;
    }

    @Override
    public void onNext(T item) {
        if (!disposable.isDisposed()) {
            actual.onNext(item);
        }
    }

    @Override
    public void onError(Throwable t) {
        if (!disposable.isDisposed()) {
            actual.onError(t);
            disposable.dispose();
        }
    }

    @Override
    public void onComplete() {
        if (!disposable.isDisposed()) {
            actual.onComplete();
            disposable.dispose();
        }
    }
}