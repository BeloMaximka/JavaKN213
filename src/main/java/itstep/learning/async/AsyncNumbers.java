package itstep.learning.async;

public class AsyncNumbers {
    public void run() {
        StringBuilder sb = new StringBuilder();
        Object lock = new Object();
        for (int i = 0; i <= 9; i++) {
            new Thread(new Rate(lock, sb, i)).start();
        }
    }
}

class Rate implements  Runnable
{
    private final Object lock;
    private final StringBuilder numberString;
    private final int number;
    public Rate(Object lock, StringBuilder numberString, int number) {
        this.lock = lock;
        this.numberString = numberString;
        this.number = number ;
    }
    @Override
    public void run() {
        synchronized (this.lock) {
            numberString.append(this.number);
            System.out.printf("Added %d: %s\n", this.number, this.numberString);
        }
    }
}