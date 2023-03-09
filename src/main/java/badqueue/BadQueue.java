package badqueue;

import java.util.Arrays;

public class BadQueue<E> {
  private static final int CAPACITY = 10;
  private E[] data = (E[])new Object[CAPACITY];
  private int count = 0;
  private Object rendezvous = new Object();

  public void put(E e) throws InterruptedException {
    synchronized (rendezvous) {
      while (count >= CAPACITY) { // MUST BE a loop
        // returns the key, and delays
        // waits for notification...
        // regains key before continuing
        // might wake up for the wrong reasons!!!
        // Simon's analogy:
        // wait makes thread go to sleep with it's "head"
        // on the rendezvous object.
        // notify shakes the pillow
        rendezvous.wait();
      }
      data[count++] = e;
//      rendezvous.notify();
      rendezvous.notifyAll();
    }
  }

  public E take() throws InterruptedException {
    synchronized (rendezvous) {
      while (count <= 0)
        rendezvous.wait();
        ;
      E rv = data[0];
      System.arraycopy(data, 1, data, 0, --count);
      // shakes the pillow upon which maybe a thread waiting
      // if multiple threads are waiting, this will move ONE
      // CHOSEN AT RANDOM to waiting to regain key state.
      // notify might cause "starvation" with multiple prods & cons
      // notifyAll is inefficient--massive scalability impediment!!!
      // INSTEAD, don't use synchronized/wait/notify, use ReentrantLock
      // or other library level tools
//      rendezvous.notify();
      rendezvous.notifyAll();
      return rv;
    }
  }
}

class TryTheBadQueue {
  public static void main(String[] args) throws InterruptedException {
    BadQueue<int[]> q = new BadQueue<>();

    Thread producer = new Thread(() -> {
      System.out.println("Producer starting...");
      try {
        for (int i = 0; i < 10_000; i++) {
          int[] data = {-1, i}; // transactionally invalid!!!
          if (i < 500) {
            Thread.sleep(1);
          }
          if (i == 5_000) {
            data[0] = -100;
          }
          data[0] = i; // transactionally valid

          q.put(data);
          data = null; // shared now, lose my reference
        }
      } catch (InterruptedException ie) {
        System.out.println("Odd, shutdown requested!");
      }
      System.out.println("Producer ending...");
    });

    Thread consumer = new Thread(() -> {
      System.out.println("Consumer starting...");
      try {
        for (int i = 0; i < 10_000; i++) {
          int [] data = q.take();
          if (i > 9_500) {
            Thread.sleep(1);
          }
          if (data[0] != data[1] || data[0] != i) {
            System.out.println("****error at index " + i
                + " data " + Arrays.toString(data));
          }
        }
      } catch (InterruptedException ie) {
        System.out.println("Odd, shutdown of consumer??");
      }
      System.out.println("Consumer ending...");
    });

    producer.start();
    consumer.start();
    producer.join();
    consumer.join();
    System.out.println("Everything finished");
  }
}
