package runnables;

class MyTask2 implements Runnable {
  private Object rendezvous = new Object();
  public long getCounter() {
    return counter;
  }

  // write to volatile tends to be slow
  // reads tend to be normal speed
  // volatile does NOT address transactional issues
  private /*volatile*/ long counter = 0;

  @Override
  public void run() {
    System.out.println(Thread.currentThread().getName()
        + " starting to increment");
    for (int i = 0; i < 20_000_000; i++) {
      synchronized (this.rendezvous) {
//      synchronized (this) { // unwise!!!
        counter++;
      }
    }
    System.out.println(Thread.currentThread().getName()
        + " completed increments");
  }
}

public class ReadModifyWriteCycle {
  public static void main(String[] args) throws Throwable {
    MyTask2 mt = new MyTask2();
//    mt.run();
//    mt.run();

    Thread t1 = new Thread(mt);
    Thread t2 = new Thread(mt);
    long start = System.nanoTime();
    t1.start();
    t2.start();

    // !!! // don't synchronize on "this", otherwise this
    // kind of code will destroy your program
//    synchronized (mt){
//      for (;;)
//        ;
//      Thread.sleep(1000);
//    }
//    System.out.println("passed sync ");

//    Thread.sleep(1_000);
    t1.join();
    t2.join();
    long time = System.nanoTime() - start;
    System.out.println(mt.getCounter());
    System.out.printf("Elapsed %7.3f\n", (time / 1_000_000_000.0));
  }
}
