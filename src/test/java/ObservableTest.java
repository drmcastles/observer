import org.junit.jupiter.api.Test;
import scheduller.IOThreadScheduler;
import scheduller.SingleThreadScheduler;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестирование функционала Observable
 * @Author Stepan
 */
class ObservableTest {

    @Test
    void testCreateAndFilter() {
        Observable.<Integer>create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(Observer<? super Integer> emitter) {
                        emitter.onNext(1);
                        emitter.onNext(2);
                        emitter.onNext(3);
                        emitter.onComplete();
                    }
                })
                .filter(x -> x > 1)
                .subscribe(new Observer<Integer>() {
                    private int count = 0;

                    @Override
                    public void onNext(Integer item) {
                        count++;
                        assertTrue(item > 1);
                    }

                    @Override
                    public void onError(Throwable t) {
                        fail("Unexpected error");
                    }

                    @Override
                    public void onComplete() {
                        assertEquals(2, count);
                    }
                });
    }

    @Test
    void testMapOperator() {
        Observable.<Integer>create(emitter -> {
                    emitter.onNext(1);
                    emitter.onNext(2);
                    emitter.onComplete();
                })
                .map(x -> x * 2)
                .subscribe(new Observer<Integer>() {
                    private int sum = 0;

                    @Override
                    public void onNext(Integer item) {
                        sum += item;
                    }

                    @Override
                    public void onError(Throwable t) {
                        fail("Unexpected error");
                    }

                    @Override
                    public void onComplete() {
                        assertEquals(6, sum);
                    }
                });
    }

    @Test
    void testSubscribeOn() throws InterruptedException {
        AtomicReference<String> subscribeThreadName = new AtomicReference<>();
        AtomicReference<String> observeThreadName = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Observable.<String>create(emitter -> {
                    // Запоминаем поток, в котором выполняется подписка
                    subscribeThreadName.set(Thread.currentThread().getName());
                    emitter.onNext("test");
                    emitter.onComplete();
                })
                .subscribeOn(new IOThreadScheduler())
                .observeOn(new SingleThreadScheduler())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onNext(String item) {
                        // Запоминаем поток, в котором обрабатывается элемент
                        observeThreadName.set(Thread.currentThread().getName());
                    }

                    @Override
                    public void onError(Throwable t) {
                        fail("Unexpected error");
                    }

                    @Override
                    public void onComplete() {
                        // Проверяем, что подписка и обработка выполнялись в разных потоках
                        assertNotEquals(subscribeThreadName.get(), observeThreadName.get());
                        latch.countDown();
                    }
                });

        latch.await(1, TimeUnit.SECONDS);
    }

    @Test
    void testFlatMap() {
        List<Integer> results = new ArrayList<>();
        Observable.create(new ObservableOnSubscribe<Integer>() {

                    @Override
                    public void subscribe(Observer<? super Integer> emitter) {
                        emitter.onNext(1);
                        emitter.onNext(2);
                        emitter.onComplete();
                    }
                })
                .flatMap(x -> Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(Observer<? super Integer> emitter) {
                        emitter.onNext(x * 10);
                        emitter.onNext(x * 20);
                        emitter.onComplete();
                    }
                }))
                .subscribe(new Observer<Integer>() {
                    private int sum = 0;

                    @Override
                    public void onNext(Integer item) {
                        results.add(item);
                        sum += item;
                    }

                    @Override
                    public void onError(Throwable t) {
                        fail("Unexpected error");
                    }

                    @Override
                    public void onComplete() {
                        assertEquals(4, results.size());
                        assertTrue(results.contains(10));
                        assertTrue(results.contains(20));
                        assertTrue(results.contains(40));
                    }
                });
    }
}