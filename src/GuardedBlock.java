import java.util.LinkedList;
import java.util.Queue;

public class GuardedBlock {
    private static final int CAPACITY = 5; // Max capacity of the buffer
    private final Queue<Integer> buffer = new LinkedList<>();
    private final Object lock = new Object();

    public void produce(int value) throws InterruptedException {
        synchronized (lock) {
            while (buffer.size() == CAPACITY) { // Guarded condition
                System.out.println("Buffer full, waiting...");
                lock.wait(); // Wait until there is space in the buffer
            }
            buffer.add(value);
            System.out.println("Produced: " + value);
            lock.notifyAll(); // Notify consumer threads
        }
    }

    public void consume() throws InterruptedException {
        synchronized (lock) {
            while (buffer.isEmpty()) { // Guarded condition
                System.out.println("Buffer empty, waiting...");
                lock.wait(); // Wait until there is data in the buffer
            }
            int value = buffer.poll();
            System.out.println("Consumed: " + value);
            lock.notifyAll(); // Notify producer threads
        }
    }

    public static void main(String[] args) {
        GuardedBlock example = new GuardedBlock();

        // Producer thread
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    example.produce(i);
                    Thread.sleep(1000); // Simulate production delay
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Consumer thread
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    example.consume();
                    Thread.sleep(150); // Simulate consumption delay
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
    }
}

