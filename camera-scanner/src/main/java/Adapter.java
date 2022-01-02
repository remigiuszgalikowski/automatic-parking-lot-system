public interface Adapter<T> {
    T getFrame();
    T getFrameMiniature();
    long getTimeBetweenFrames();
}