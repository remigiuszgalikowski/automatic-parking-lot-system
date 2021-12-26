import org.opencv.core.Mat;

public interface Detector {
    boolean detect(Mat previousFrame, Mat currentFrame);
}
