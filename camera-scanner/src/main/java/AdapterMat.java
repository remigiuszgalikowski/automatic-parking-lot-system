import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class AdapterMat implements Adapter {

    public final Size miniatureSize;
    private Mat previousFrame;
    private Mat previousFrameMiniature;

    public AdapterMat(String source) {
        this.videoCapture.open(source);
        this.miniatureSize = this.generateMiniatureSize();
        this.previousFrame = getFrame();
        this.previousFrameMiniature = getFrameMiniature();
    }
    public AdapterMat(int source) {
        this.videoCapture.open(source);
        this.miniatureSize = this.generateMiniatureSize();
        this.previousFrame = getFrame();
        this.previousFrameMiniature = getFrameMiniature();
    }

    @Override
    public Mat getFrame() {
        Mat mat = new Mat();
        this.videoCapture.retrieve(mat);
        this.previousFrame = mat;
        return mat;
    }

    @Override
    public Mat getFrameMiniature() {
        Mat mat = new Mat();
        this.videoCapture.retrieve(mat);
        Imgproc.resize(mat, mat, this.miniatureSize);
        this.previousFrameMiniature = mat;
        return mat;
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
}
