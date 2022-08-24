import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Supplier;

public class TestPlateFinder implements Finder<List<PlateImage>, Mat> {

    private final PlateFinder plateFinder;
    private final Supplier<Long> timeSupplier;
    private long duration;

    public TestPlateFinder(Supplier<Long> timeSupplier, Converter converter) {
        this.plateFinder = new PlateFinder(converter);
        this.timeSupplier = timeSupplier;
    }

    @Override
    public List<PlateImage> find(Mat input) {
        long startTime = this.timeSupplier.get();
        List<PlateImage> recognizedImage = this.plateFinder.find(input);
        this.duration = this.timeSupplier.get() - startTime;
        return recognizedImage;
    }

    public long getDuration() {
        return this.duration;
    }

}
