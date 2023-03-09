package runnables;

public class Stopper {
  private static volatile boolean stop = false;

  public static void main(String[] args) throws Throwable {
    new Thread(() -> {
      System.out.println("Stopper starting...");
      while (!stop)
//        System.out.println(".");
        ;
      System.out.println("Stopper stopping...");
    }).start();
    System.out.println("main has started the stopper...");
    Thread.sleep(1_000);
    System.out.println("main setting stop flag");
    stop = true;
    System.out.println("main has set stop flag to " + stop);
  }
}
