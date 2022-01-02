import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import static org.opencv.videoio.Videoio.CAP_PROP_FPS;

public class AdapterMat implements Adapter<Mat> {

    private final Size miniatureSize;
    private final VideoCapture videoCapture;
    private final long millisecondsPerFrame;

    public AdapterMat(String source) {
        this.videoCapture = new VideoCapture(source);
        this.miniatureSize = this.generateMiniatureSize();
        this.millisecondsPerFrame = (long) (1000 / this.videoCapture.get(CAP_PROP_FPS));
    }
    public AdapterMat(int source) {
        this.videoCapture = new VideoCapture(source);
        this.miniatureSize = this.generateMiniatureSize();
        this.millisecondsPerFrame = (long) (1000 / this.videoCapture.get(CAP_PROP_FPS));
    }

    @Override
    public Mat getFrame() {
        Mat mat = new Mat();
        this.videoCapture.retrieve(mat);
        return mat;
    }

    @Override
    public Mat getFrameMiniature() {
        Mat mat = new Mat();
        this.videoCapture.retrieve(mat);
        Imgproc.resize(mat, mat, this.miniatureSize);
        return mat;
    }

    @Override
    public long getTimeBetweenFrames() {
        return this.millisecondsPerFrame;
    }

    private Size generateMiniatureSize() {
        Mat frame = new Mat();
        this.videoCapture.read(frame);
        Size originalSize = new Size(frame.width(),frame.height());
        double ratio = originalSize.width/originalSize.height;
        int miniatureHeight = 80;
        long miniatureWidth = Math.round(ratio * miniatureHeight);
        return new Size(miniatureWidth, miniatureHeight);
    }

    public Size getMiniatureSize() { return this.miniatureSize; }

    public void videoCaptureGrab() {
        this.videoCapture.grab();
    }

    public boolean isVideoCaptureOpened() {
        return this.videoCapture.isOpened();
    }

}