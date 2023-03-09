package runnables;

class MyTask implements Runnable {
  int i = 0;

  @Override
  public void run() {
    System.out.println(Thread.currentThread().getName()
        + " Starting task");
    for (; i < 10_000; i++) {
      System.out.println(Thread.currentThread().getName()
          + "i = " + i);
    }
    System.out.println(Thread.currentThread().getName()
        + " Ending task");
  }
}

public class SimpleRunnable {
  public static void main(String[] args) {
    Runnable mt = new MyTask();
    Thread t1 = new Thread(mt);
    Thread t2 = new Thread(mt);

    System.out.println(Thread.currentThread().getName()
        + " Main about to call run");
    t1.start();
    t2.start();
    System.out.println(Thread.currentThread().getName()
        + " Main returned from run");
  }
}
