public interface Adapter<T,S> {
    T getFrame();
    int getFramesPerSecond();
    long getMillisecondsPerFrame();
    S getFrameSize();
}