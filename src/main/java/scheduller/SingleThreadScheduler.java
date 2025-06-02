package scheduller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * scheduller.Scheduler с одним потоком для последовательной обработки
 * @Author Stepan
 */
public class SingleThreadScheduler implements Scheduler {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void execute(Runnable task) {
        executor.execute(task);
    }
}