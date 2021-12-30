public interface Detector<T> {
    boolean detect(T previousFrame, T currentFrame);
}
