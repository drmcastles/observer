/**
 * Функциональный интерфейс для создания Observable
 * @param <T> тип элементов
 * @Author Stepan
 */
@FunctionalInterface
public interface ObservableOnSubscribe<T> {
    /**
     * Вызывается при подписке на Observable
     * @param observer подписчик
     */
    void subscribe(Observer<? super T> observer);
}