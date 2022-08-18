import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.awt.image.BufferedImage;
import java.util.function.Supplier;

public class TestPlateFinder implements Finder<BufferedImage, Mat> {

    private final PlateFinder plateFinder;
    private final Supplier<Long> timeSupplier;
    private long duration;

    public TestPlateFinder(Supplier<Long> timeSupplier, Converter converter) {
        this.plateFinder = new PlateFinder(converter);
        this.timeSupplier = timeSupplier;
    }

    @Override
    public BufferedImage find(Mat input) {
        long startTime = this.timeSupplier.get();
        BufferedImage recognizedImage = this.plateFinder.find(input);
        this.duration = this.timeSupplier.get() - startTime;
        return recognizedImage;
    }

    public Rect getPlateCoords() {
        return this.plateFinder.getPlateCoords();
    }

    public long getDuration() {
        return this.duration;
    }

}
