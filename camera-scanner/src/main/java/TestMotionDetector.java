import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TestMotionDetector implements Detector {

    private final MotionDetector motionDetector;
    private final Supplier<Long> timeSupplier;
    private final List<Long> durations;

    public TestMotionDetector() {
        this.motionDetector = new MotionDetector();
        this.timeSupplier = System::currentTimeMillis;
        this.durations = new ArrayList<>();
    }

    @Override
    public boolean detect(Mat previousFrame, Mat currentFrame) {
        long startTime = this.timeSupplier.get();
        boolean isDetected = this.motionDetector.detect(previousFrame, currentFrame);
        long duration = this.timeSupplier.get() - startTime;
        this.durations.add(duration);
        return isDetected;
    }

    public long getAvgDuration() {
        double sum = 0;
        for (long duration : this.durations)
        {
            sum += duration;
        }
        double avg;
        avg = sum / this.durations.size();
        return (long) avg;
    }

}