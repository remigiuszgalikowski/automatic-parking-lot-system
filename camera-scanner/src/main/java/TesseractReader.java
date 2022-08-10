import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;

public class TesseractReader implements Reader<BufferedImage> {

    private final Tesseract tesseract;

    public TesseractReader(Tesseract tesseract) {
        this.tesseract = tesseract;
    }

    @Override
    public String read(BufferedImage image) {
        String plateText;
        try {
            plateText = this.tesseract.doOCR((BufferedImage) image);
            //plateText = plateText.replaceAll("\\s+","");
            plateText = plateText.replaceAll("\\W+","");
            return plateText;
        } catch (TesseractException e) {
            e.printStackTrace();
            return null;
        }
    }

}