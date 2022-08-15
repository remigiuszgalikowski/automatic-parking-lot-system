import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import static org.opencv.videoio.Videoio.*;

public class VideoStreamAdapter implements Adapter<Mat> {
    private final VideoCapture videoCapture;

    public VideoStreamAdapter(String source) {
        this.videoCapture = new VideoCapture(source, CAP_FFMPEG);
    }
    public VideoStreamAdapter(int source) {
        this.videoCapture = new VideoCapture(source);
    }

    @Override
    public Mat getFrame() {
        Mat mat = new Mat();
        this.videoCapture.retrieve(mat);
        return mat;
    }
    @Override
    public int getFramesPerSecond() {
        return (int) this.videoCapture.get(CAP_PROP_FPS);
    }
    @Override
    public long getMillisecondsPerFrame() {
        return (long) (1000 / this.videoCapture.get(CAP_PROP_FPS));
    }
    public void skipFrame() {
        this.videoCapture.grab();
    }
    public boolean isVideoCaptureOpened() {
        return this.videoCapture.isOpened();
    }
}


