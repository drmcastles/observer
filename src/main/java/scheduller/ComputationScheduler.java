package scheduller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * scheduller.Scheduler с фиксированным пулом потоков для вычислений
 * @Author Stepan
 */
public class ComputationScheduler implements Scheduler {
    private final ExecutorService executor;

    public ComputationScheduler() {
        int cores = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(cores);
    }

    @Override
    public void execute(Runnable task) {
        executor.execute(task);
    }
}
