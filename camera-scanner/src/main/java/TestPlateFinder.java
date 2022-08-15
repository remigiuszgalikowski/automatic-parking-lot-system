import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TestPlateFinder implements Finder<BufferedImage, Mat> {

    private final PlateFinder plateRecognizer;
    private final Supplier<Long> timeSupplier;
    private final List<Long> durations;

    public TestPlateFinder(Supplier<Long> timeSupplier, Converter converter) {
        this.plateRecognizer = new PlateFinder(converter);
        this.timeSupplier = timeSupplier;
        this.durations = new ArrayList<>();
    }

    @Override
    public BufferedImage find(Mat input) {
        long startTime = this.timeSupplier.get();
        BufferedImage recognizedImage = this.plateRecognizer.find(input);
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
