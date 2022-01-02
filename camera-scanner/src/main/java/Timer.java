import java.util.concurrent.TimeUnit;

public class Timer {

    public Timer() {}

    public void await(long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
