import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.util.function.Supplier;

import static org.opencv.videoio.Videoio.CAP_PROP_FPS;

public class TestAdapterMat implements Adapter {

    static double MILLISECONDS_PER_FRAME;

    private final AdapterMat adapter;
    private final Supplier<Long> timeSupplier;
    private final long timeOfFirstFrame;

    private final Debugger debugger;
    private final boolean debugMode;

    private int lastFrameNumber = 0;
    private int currentFrameNumber = 0;
    private Mat lastFrame;
    private Mat lastFrameMiniature;

    public TestAdapterMat(String source, Debugger debugger, boolean debug) {
        this.adapter = new AdapterMat(source);
        this.lastFrame = this.adapter.getFrame();
        this.lastFrameMiniature = this.adapter.getFrameMiniature();
        this.timeSupplier = System::currentTimeMillis;
        this.timeOfFirstFrame = timeSupplier.get();
        MILLISECONDS_PER_FRAME = 1000 / this.adapter.getVideoCaptureProperties(CAP_PROP_FPS);
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
    public Mat getHighlightedFrame() {
        Mat highlightedFrame = this.adapter.getHighlightedFrame();
        if (this.debugMode) {this.debugger.debug(highlightedFrame, "getHighlightedFrame()");}
        return highlightedFrame;
    }

    @Override
    public void setHighlightedFrame(Object highlightedFrame) {
        this.adapter.setHighlightedFrame(highlightedFrame);
    }

    private void skipFrames() {
        double timeSinceFirstFrame = timeSupplier.get() - this.timeOfFirstFrame;
        this.currentFrameNumber = (int) Math.round(timeSinceFirstFrame / MILLISECONDS_PER_FRAME);
        int numberOfFramesToSkip = this.currentFrameNumber - this.lastFrameNumber - 1;
        this.lastFrameNumber = this.currentFrameNumber + numberOfFramesToSkip + 1;
        for (int i = 0; i < numberOfFramesToSkip; i++) this.adapter.videoCaptureGrab();
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

    public double getVideoCaptureProperties(int propId) {
        return this.adapter.getVideoCaptureProperties(propId);
    }

    public void videoCaptureGrab() {
        this.adapter.videoCaptureGrab();
    }

    public boolean isVideoCaptureOpened() {
        return this.adapter.isVideoCaptureOpened();
    }


}