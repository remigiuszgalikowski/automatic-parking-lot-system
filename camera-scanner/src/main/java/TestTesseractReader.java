import net.sourceforge.tess4j.Tesseract;

import java.awt.image.BufferedImage;
import java.util.function.Supplier;

public class TestTesseractReader implements Reader<BufferedImage> {

    private final Reader<BufferedImage> reader;
    private final Supplier<Long> timeSupplier;
    private long duration;

    public TestTesseractReader(Tesseract tesseract, Supplier<Long> timeSupplier) {
        this.reader = new TesseractReader(tesseract);
        this.timeSupplier = timeSupplier;
    }

    @Override
    public String read(BufferedImage bufferedImage) {
        long startTime = this.timeSupplier.get();
        String text = this.reader.read(bufferedImage);
        this.duration = this.timeSupplier.get() - startTime;
        return text;
    }
    
    public long getDuration() {
        return this.duration;
    }

}