import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TestPlateRecognizer implements Recognizer {

    private final PlateRecognizer plateRecognizer;
    private final Supplier<Long> timeSupplier;
    private final List<Long> durations;


    public TestPlateRecognizer(Adapter adapter, Converter converter) {
        this.plateRecognizer = new PlateRecognizer(adapter, converter);
        this.timeSupplier = System::currentTimeMillis;
        this.durations = new ArrayList<>();
    }

    @Override
    public BufferedImage recognize(Mat image) {
        long startTime = this.timeSupplier.get();
        BufferedImage recognizedImage = this.plateRecognizer.recognize(image);
        long duration = this.timeSupplier.get() - startTime;
        this.durations.add(duration);
        return recognizedImage;
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
