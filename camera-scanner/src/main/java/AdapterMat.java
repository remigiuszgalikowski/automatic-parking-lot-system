import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class AdapterMat implements Adaptation {

    private VideoCapture videoCapture;

    public AdapterMat(String source) {
        videoCapture = new VideoCapture();
        videoCapture.open(source);
    }
    public AdapterMat(int source) {
        videoCapture = new VideoCapture();
        videoCapture.open(source);
    }

    @Override
    public Mat getFrame() {
        Mat mat = new Mat();
        videoCapture.read(mat);
        return mat;
    }
}
