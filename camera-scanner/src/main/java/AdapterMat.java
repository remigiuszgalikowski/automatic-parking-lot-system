import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class AdapterMat implements Adapter {

    private final Size miniatureSize;
    private final VideoCapture videoCapture;

    private Mat highlightedFrame;

    public AdapterMat(String source) {
        this.videoCapture = new VideoCapture(source);
        this.miniatureSize = this.generateMiniatureSize();
        this.highlightedFrame = new Mat();
    }
    public AdapterMat(int source) {
        this.videoCapture = new VideoCapture(source);
        this.miniatureSize = this.generateMiniatureSize();
        this.highlightedFrame = new Mat();
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
    public Mat getHighlightedFrame() {
        return this.highlightedFrame;
    }

    @Override
    public void setHighlightedFrame(Object highlightedFrame) {
        this.highlightedFrame = (Mat) highlightedFrame;
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

    public double getVideoCaptureProperties(int propId) {
        return this.videoCapture.get(propId);
    }

    public void videoCaptureGrab() {
        this.videoCapture.grab();
    }

    public boolean isVideoCaptureOpened() {
        return this.videoCapture.isOpened();
    }

}