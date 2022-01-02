import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.util.function.Supplier;

public class TestAdapterMat implements Adapter<Mat> {

    private final AdapterMat adapter;
    private final Supplier<Long> timeSupplier;
    private final long timeOfFirstFrame;

    private final Debugger debugger;
    private final boolean debugMode;

    private int lastFrameNumber = 0;
    private int currentFrameNumber = 0;
    private Mat lastFrame;
    private Mat lastFrameMiniature;

    public TestAdapterMat(String source, Supplier<Long> timeSupplier, Debugger debugger, boolean debug) {
        this.adapter = new AdapterMat(source);
        this.lastFrame = this.adapter.getFrame();
        this.lastFrameMiniature = this.adapter.getFrameMiniature();
        this.timeSupplier = timeSupplier;
        this.timeOfFirstFrame = timeSupplier.get();
        this.debugger = debugger;
        this.debugMode = debug;
    }

    @Override
    public Mat getFrame() {
        this.skipFrames();
        Mat frame = this.determineFrame();
        if (this.debugMode) {this.debugger.debug(frame, "getFrame()");}
        return frame;
    }



    @Override
    public Mat getFrameMiniature() {
        this.skipFrames();
        Mat frameMiniature = this.determineFrameMiniature();
        if (this.debugMode) {this.debugger.debug(frameMiniature, "getFrameMiniature()");}
        return frameMiniature;
    }

    @Override
    public long getTimeBetweenFrames() {
        return this.adapter.getTimeBetweenFrames();
    }

    private void skipFrames() {
        double timeSinceFirstFrame = timeSupplier.get() - this.timeOfFirstFrame;
        this.currentFrameNumber = (int) Math.round(timeSinceFirstFrame / this.getTimeBetweenFrames());
        int numberOfFramesToSkip = this.currentFrameNumber - this.lastFrameNumber - 1;
        this.lastFrameNumber = this.currentFrameNumber + numberOfFramesToSkip + 1;
        for (int i = 0; i < numberOfFramesToSkip; i++) this.videoCaptureGrab();
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

    public Size getMiniatureSize() {
        return this.adapter.getMiniatureSize();
    }

    public void videoCaptureGrab() {
        this.adapter.videoCaptureGrab();
    }

    public boolean isVideoCaptureOpened() {
        return this.adapter.isVideoCaptureOpened();
    }


}