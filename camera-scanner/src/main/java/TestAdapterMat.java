import org.opencv.core.Mat;

import java.util.function.Supplier;

import static org.opencv.videoio.Videoio.CAP_PROP_FPS;

public class TestAdapterMat implements Adaptation {

    static double MILLISECONDS_PER_FRAME;

    private final Adaptation adapter;
    private final Supplier<Long> timeSupplier;

    private int lastFrameNumber = 0;
    private int currentFrameNumber = 0;
    private Mat lastFrame;
    private Mat lastFrameMiniature;
    private final long timeOfFirstFrame;

    public TestAdapterMat(String source) {
        this.adapter = new AdapterMat(source);
        this.lastFrame = this.adapter.getFrame();
        this.lastFrameMiniature = this.adapter.getFrameMiniature();
        this.timeSupplier = System::currentTimeMillis;
        this.timeOfFirstFrame = timeSupplier.get();
        MILLISECONDS_PER_FRAME = 1000 / this.adapter.videoCapture.get(CAP_PROP_FPS);
    }

    @Override
    public Mat getFrame() {
        this.skipFrames();
        return this.determineFrame();
    }

    @Override
    public Mat getFrameMiniature() {
        this.skipFrames();
        return this.determineFrameMiniature();
    }

    private void skipFrames() {
        double timeSinceFirstFrame = timeSupplier.get() - this.timeOfFirstFrame;
        this.currentFrameNumber = (int) Math.round(timeSinceFirstFrame / MILLISECONDS_PER_FRAME);
        int numberOfFramesToSkip = this.currentFrameNumber - this.lastFrameNumber - 1;
        this.lastFrameNumber = this.currentFrameNumber + numberOfFramesToSkip + 1;
        for (int i = 0; i < numberOfFramesToSkip; i++) this.adapter.videoCapture.grab();
    }

    private Mat determineFrame() {
        if (this.currentFrameNumber != this.lastFrameNumber) {
            this.lastFrame = this.adapter.getFrame();
        }
        return this.lastFrame;
    }

    private Mat determineFrameMiniature() {
        if (this.currentFrameNumber != this.lastFrameNumber) {
            this.lastFrameMiniature = this.adapter.getFrameMiniature();
        }
        return this.lastFrameMiniature;
    }
}