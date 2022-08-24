import net.sourceforge.tess4j.Tesseract;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Supplier;

public class TestTesseractReader implements Reader<List<PlateImage>, List<PlateReading>> {

    private final Reader<List<PlateImage>, List<PlateReading>> reader;
    private final Supplier<Long> timeSupplier;
    private long duration;

    public TestTesseractReader(Tesseract tesseract, Supplier<Long> timeSupplier) {
        this.reader = new TesseractReader(tesseract);
        this.timeSupplier = timeSupplier;
    }

    @Override
    public List<PlateReading> read(List<PlateImage> images) {
        long startTime = this.timeSupplier.get();
        List<PlateReading> text = this.reader.read(images);
        this.duration = this.timeSupplier.get() - startTime;
        return text;
    }
    
    public long getDuration() {
        return this.duration;
    }

}