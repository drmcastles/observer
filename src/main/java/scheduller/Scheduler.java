package scheduller;

/**
 * Интерфейс для управления потоками выполнения
 * @Author Stepan
 */
public interface Scheduler {
    /**
     * Запускает задачу в потоке
     * @param task выполняемая задача
     */
    void execute(Runnable task);
}