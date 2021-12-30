public interface Adapter<T> {
    T getFrame();
    T getFrameMiniature();
    T getHighlightedFrame();
    void setHighlightedFrame(T highlightedFrame);
}