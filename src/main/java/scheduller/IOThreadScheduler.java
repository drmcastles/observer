package scheduller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * scheduller.Scheduler с кэшированным пулом потоков для I/O операций
 * @Author Stepan
 */
public class IOThreadScheduler implements Scheduler {
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public void execute(Runnable task) {
        executor.execute(task);
    }
}
