import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by tamaz bagdavadze on 7/19/2016.
 */

public class Main {

    private static int count = 0;

    private static ReentrantLock lock = new ReentrantLock();

    private static void increment() {
        lock.lock();
        try{
            count++;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newWorkStealingPool(4);
        List<Callable<String>> callableList = new ArrayList<>();

        final int iterationCount = 10000;

        callableList.add(() -> {
            for (int i = 0; i < iterationCount; i++) {
                increment();
            }
            return "1";
        });

        callableList.add(() -> {
            for (int i = 0; i < iterationCount; i++) {
                increment();
                System.out.println(lock.isLocked());
                System.out.println(lock.isLocked());
                System.out.println(lock.isLocked());
            }
            return "2";
        });

        callableList.add(() -> {
            for (int i = 0; i < iterationCount; i++) {
                increment();
            }
            return "3";
        });

        executorService.invokeAll(callableList).stream().map(f -> {
            try {
                return f.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new IllegalStateException();
            }
        }).forEach(System.out::println);

        executorService.shutdown();

        System.out.println(lock.isLocked());
        System.out.println(count);
    }
}