import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TesseractReader implements Reader<List<PlateImage>, List<PlateReading>> {

    private final Tesseract tesseract;

    public TesseractReader(Tesseract tesseract) {
        this.tesseract = tesseract;
    }

    @Override
    public List<PlateReading> read(List<PlateImage> plates) {
        List<PlateReading> readings = new ArrayList<PlateReading>();
        for (PlateImage plate : plates) {
            String text;
            try {
                text = this.tesseract.doOCR(plate.picture());
                text = text.replaceAll("[^A-Z0-9]+", "");
                if (text.length() >= 5) {
                    boolean isNotAlreadyIn = true;
                    for (PlateReading p : readings) {
                        if (Objects.equals(p.text(), text)) {
                            isNotAlreadyIn = false;
                            break;
                        }
                    }
                    if (isNotAlreadyIn) {readings.add(new PlateReading(text, plate.location()));}
                }
            } catch (TesseractException e) {
                e.printStackTrace();
            }
        }
        return readings;
    }
}
