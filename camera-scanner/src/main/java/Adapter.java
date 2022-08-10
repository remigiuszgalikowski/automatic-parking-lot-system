public interface Adapter<T> {
    T getFrame();
    int getFramesPerSecond();
    long getMillisecondsPerFrame();
}