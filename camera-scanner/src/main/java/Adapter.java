import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;

public interface Adapter {
    VideoCapture videoCapture = new VideoCapture();
    Mat getFrame();
    Mat getFrameMiniature();
}
