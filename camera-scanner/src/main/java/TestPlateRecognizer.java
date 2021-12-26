import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TestPlateRecognizer implements MotionDetection, Recognition {

    private final Recognition plateRecognizer;
    private final Supplier<Long> timeSupplier;

    private List<Long> motionDetectionTimes;
    private List<Long> plateRecognitionTimes;


    public TestPlateRecognizer(Adaptation adapter) {
        this.plateRecognizer = new PlateRecognizer(adapter);
        this.timeSupplier = System::currentTimeMillis;
        this.motionDetectionTimes = new ArrayList<>();
        this.plateRecognitionTimes = new ArrayList<>();
    }

    @Override
    public boolean detectMotion(Mat frame1, Mat frame2) {
        long startTime = this.timeSupplier.get();
        boolean ifMoved = this.detectMotion(frame1, frame2);
        long timeStamp = this.timeSupplier.get() - startTime;
        this.motionDetectionTimes.add(timeStamp);
        System.out.println("motionDetect: " + timeStamp);
        return ifMoved;
    }

    @Override
    public String recognize() {
        long startTime = this.timeSupplier.get();
        String plateText = this.plateRecognizer.recognize();
        long timeStamp = this.timeSupplier.get() - startTime;
        this.plateRecognitionTimes.add(timeStamp);
        System.out.println("plateRecog: " + timeStamp);
        return plateText;
    }

    public long getAvgPlateRecognitionTime() {
        double sum = 0;
        for (long time : this.plateRecognitionTimes)
        {
            sum += time ;
        }
        double avg = sum / this.plateRecognitionTimes.size();
        return (long) avg;
    }

    public long getAvgMotionDetectionTime() {
        double sum = 0;
        for (long time : this.motionDetectionTimes)
        {
            sum += time ;
        }
        double avg = sum / this.motionDetectionTimes.size();
        return (long) avg;
    }

}
