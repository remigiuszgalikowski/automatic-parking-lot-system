import net.sf.javaanpr.imageanalysis.CarSnapshot;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class FileAdapter {

    public FileAdapter() {}

    public CarSnapshot adapt(String path) throws IOException {
        return new CarSnapshot(path);
    }
    public CarSnapshot adapt(BufferedImage img) {
        return new CarSnapshot(img);
    }
    public CarSnapshot adapt(InputStream is) throws IOException {
        return new CarSnapshot(is);
    }
}
