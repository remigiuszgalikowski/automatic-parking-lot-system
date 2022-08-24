import org.opencv.core.Rect;
import java.awt.image.BufferedImage;

public record PlateImage(BufferedImage picture, Rect location) {
}
