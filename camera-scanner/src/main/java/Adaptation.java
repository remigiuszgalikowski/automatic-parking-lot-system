import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;

public interface Adaptation {
    VideoCapture videoCapture = new VideoCapture();
    Mat getFrame();
    Mat getFrameMiniature();
}
