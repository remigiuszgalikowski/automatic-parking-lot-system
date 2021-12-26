import org.opencv.core.Mat;
import java.awt.image.BufferedImage;

public interface Recognizer {
    BufferedImage recognize(Mat image);
}
