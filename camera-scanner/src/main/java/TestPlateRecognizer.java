import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TestPlateRecognizer implements Detector, Recognizer {

    private final Recognizer plateRecognizer;
    private final Supplier<Long> timeSupplier;

    private List<Long> motionDetectionTimes;
    private List<Long> plateRecognitionTimes;


    public TestPlateRecognizer(Adapter adapter) {
        this.plateRecognizer = new PlateRecognizer(adapter);
        this.timeSupplier = System::currentTimeMillis;
        this.motionDetectionTimes = new ArrayList<>();
        this.plateRecognitionTimes = new ArrayList<>();
    }

    @Override
    public boolean detect(Mat frame1, Mat frame2) {
        long startTime = this.timeSupplier.get();
        boolean ifMoved = this.detect(frame1, frame2);
        long timeStamp = this.timeSupplier.get() - startTime;
        this.motionDetectionTimes.add(timeStamp);
        System.out.println("motionDetect: " + timeStamp);
        return ifMoved;
    }

    @Override
    public BufferedImage recognize(Mat image) {

        long startTime = this.timeSupplier.get();
        BufferedImage plateText = this.plateRecognizer.recognize(image);
        long timeStamp = this.timeSupplier.get() - startTime;
        this.plateRecognitionTimes.add(timeStamp);
        System.out.println("plateRecog: " + timeStamp);
        return null;
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
