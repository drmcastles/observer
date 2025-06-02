import scheduller.Scheduler;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Основной класс для создания реактивных потоков
 * @param <T> тип элементов потока
 * @Author Stepan
 */
public class Observable<T> {
    private final ObservableOnSubscribe<T> source;

    private Observable(ObservableOnSubscribe<T> source) {
        this.source = source;
    }

    /**
     * Создает новый Observable
     * @param source функция инициализации потока
     * @return новый объект Observable
     * @Author Stepan
     */
    public static <T> Observable<T> create(ObservableOnSubscribe<T> source) {
        return new Observable<>(source);
    }

    /**
     * Подписывает Observer на поток
     * @param observer объект для получения уведомлений
     * @return Disposable для управления подпиской
     * @Author Stepan
     */
    public Disposable subscribe(Observer<? super T> observer) {
        DefaultDisposable disposable = new DefaultDisposable();
        Observer<? super T> safeObserver = new SafeObserver<>(observer, disposable);
        source.subscribe(safeObserver);
        return disposable;
    }

    /**
     * Подписывается с обработчиками onNext и onError
     * @param onNext обработчик элементов
     * @param onError обработчик ошибок
     * @return Disposable
     * @Author Stepan
     */
    public Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        return subscribe(new Observer<T>() {
            @Override
            public void onNext(T item) {
                onNext.accept(item);
            }

            @Override
            public void onError(Throwable t) {
                onError.accept(t);
            }

            @Override
            public void onComplete() {}
        });
    }

    /**
     * Преобразует элементы потока
     * @param mapper функция преобразования
     * @return новый Observable
     * @Author Stepan
     */
    public <R> Observable<R> map(Function<? super T, ? extends R> mapper) {
        return new Observable<>(observer ->
                this.subscribe(new Observer<T>() {
                    @Override
                    public void onNext(T item) {
                        try {
                            observer.onNext(mapper.apply(item));
                        } catch (Throwable t) {
                            observer.onError(t);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        observer.onError(t);
                    }

                    @Override
                    public void onComplete() {
                        observer.onComplete();
                    }
                })
        );
    }

    /**
     * Фильтрует элементы потока
     * @param predicate условие фильтрации
     * @return новый Observable
     * @Author Stepan
     */
    public Observable<T> filter(Predicate<? super T> predicate) {
        return new Observable<>(observer ->
                this.subscribe(new Observer<T>() {
                    @Override
                    public void onNext(T item) {
                        try {
                            if (predicate.test(item)) {
                                observer.onNext(item);
                            }
                        } catch (Throwable t) {
                            observer.onError(t);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        observer.onError(t);
                    }

                    @Override
                    public void onComplete() {
                        observer.onComplete();
                    }
                })
        );
    }

    /**
     * Указывает scheduller.Scheduler для выполнения подписки
     * @param scheduler планировщик
     * @return новый Observable
     * @Author Stepan
     */
    public Observable<T> subscribeOn(Scheduler scheduler) {
        return new Observable<>(observer -> 
            scheduler.execute(() -> source.subscribe(observer))
        );
    }

    /**
     * Указывает scheduller.Scheduler для обработки элементов
     * @param scheduler планировщик
     * @return новый Observable
     * @Author Stepan
     */
    public Observable<T> observeOn(Scheduler scheduler) {
        return new Observable<>(observer -> 
            this.subscribe(new Observer<T>() {
                @Override
                public void onNext(T item) {
                    scheduler.execute(() -> observer.onNext(item));
                }

                @Override
                public void onError(Throwable t) {
                    scheduler.execute(() -> observer.onError(t));
                }

                @Override
                public void onComplete() {
                    scheduler.execute(observer::onComplete);
                }
            })
        );
    }

    /**
     * Преобразует элементы в новые Observable и объединяет их
     * @param mapper функция преобразования
     * @return новый Observable
     * @Author Stepan
     */
    public <R> Observable<R> flatMap(Function<? super T, ? extends Observable<? extends R>> mapper) {
        return new Observable<>(downstreamObserver -> {
            CompositeDisposable composite = new CompositeDisposable();
            Disposable disposable = this.subscribe(
                    new Observer<T>() {
                        @Override
                        public void onNext(T item) {
                            try {
                                Observable<? extends R> obs = mapper.apply(item);
                                Disposable d = obs.subscribe(
                                        downstreamObserver::onNext,
                                        downstreamObserver::onError
                                );
                                composite.add(d);
                            } catch (Throwable t) {
                                downstreamObserver.onError(t);
                            }
                        }

                        @Override
                        public void onError(Throwable t) {
                            downstreamObserver.onError(t);
                        }

                        @Override
                        public void onComplete() {
                            downstreamObserver.onComplete();
                        }
                    }
            );
            composite.add(disposable);
        });
    }
}