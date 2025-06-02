/**
 * Интерфейс для управления подпиской
 *
 * @Author Stepan
 */
public interface Disposable {
    /**
     * Отменяет подписку
     */
    void dispose();

    /**
     * Проверяет состояние подписки
     *
     * @return true если подписка отменена
     */
    boolean isDisposed();
}
