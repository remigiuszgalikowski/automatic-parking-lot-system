import org.opencv.core.Mat;
import java.util.function.Supplier;

public class VideoFileAdapter implements Adapter<Mat> {

    private final VideoStreamAdapter adapter;
    private final Supplier<Long> timeSupplier;
    private final long timeOfLastFrame;

    private int lastFrameNumber = 0;
    private int currentFrameNumber = 0;
    private Mat lastFrame;

    public VideoFileAdapter(String source, Supplier<Long> timeSupplier) {
        this.adapter = new VideoStreamAdapter(source);
        this.lastFrame = this.adapter.getFrame();
        this.timeSupplier = timeSupplier;
        this.timeOfLastFrame = timeSupplier.get();
    }

    @Override
    public Mat getFrame() {
        this.skipFrames();
        return this.determineFrame();
    }
    @Override
    public int getFramesPerSecond() {
        return this.adapter.getFramesPerSecond();
    }
    @Override
    public long getMillisecondsPerFrame() {
        return this.adapter.getMillisecondsPerFrame();
    }
    public void skipFrame() {
        this.adapter.skipFrame();
    }
    public boolean isVideoCaptureOpened() {
        return this.adapter.isVideoCaptureOpened();
    }

    private Mat determineFrame() {
        if (this.currentFrameNumber != this.lastFrameNumber) {
            this.lastFrame = this.adapter.getFrame();
        }
        return this.lastFrame;
    }
    private void skipFrames() {
        double timeSinceFirstFrame = timeSupplier.get() - this.timeOfLastFrame;
        this.currentFrameNumber = (int) Math.round(timeSinceFirstFrame / this.getMillisecondsPerFrame());
        int numberOfFramesToSkip = this.currentFrameNumber - this.lastFrameNumber - 1;
        this.lastFrameNumber = this.currentFrameNumber + numberOfFramesToSkip + 1;
        for (int i = 0; i < numberOfFramesToSkip; i++) this.skipFrame();
    }
}