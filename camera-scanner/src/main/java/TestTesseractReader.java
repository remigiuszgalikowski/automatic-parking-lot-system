import net.sourceforge.tess4j.Tesseract;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TestTesseractReader implements Reader<BufferedImage> {

    private final Reader<BufferedImage> reader;
    private final Supplier<Long> timeSupplier;
    private final List<Long> durations;

    public TestTesseractReader(Tesseract tesseract, Supplier<Long> timeSupplier) {
        this.reader = new TesseractReader(tesseract);
        this.timeSupplier = timeSupplier;
        this.durations = new ArrayList<>();
    }

    @Override
    public String read(BufferedImage bufferedImage) {
        long startTime = this.timeSupplier.get();
        String text = this.reader.read(bufferedImage);
        long duration = this.timeSupplier.get() - startTime;
        this.durations.add(duration);
        return text;
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