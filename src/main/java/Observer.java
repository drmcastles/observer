/**
 * Интерфейс для получения уведомлений от Observable
 * @param <T> тип элементов
 * @Author Stepan
 */
public interface Observer<T> {
    /**
     * Вызывается при получении нового элемента
     * @param item элемент потока
     */
    void onNext(T item);

    /**
     * Вызывается при возникновении ошибки
     * @param t объект ошибки
     */
    void onError(Throwable t);

    /**
     * Вызывается при завершении потока
     */
    void onComplete();
}