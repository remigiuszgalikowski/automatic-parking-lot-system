import org.opencv.core.Mat;

public interface MotionDetection {
    boolean detectMotion(Mat frame1, Mat frame2);
}
