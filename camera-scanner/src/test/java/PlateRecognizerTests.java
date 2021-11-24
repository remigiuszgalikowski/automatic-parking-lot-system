import net.sf.javaanpr.imageanalysis.CarSnapshot;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlateRecognizerTests {

    PlateRecognizer recognizer = new PlateRecognizer();
    public PlateRecognizerTests() throws ParserConfigurationException, IOException, SAXException {
    }

    @Test
    public void testPhotos() throws IOException {
        String[] pathnamesPhotos;
        String resourcesPath = System.getProperty("user.dir").concat("\\src\\test\\resources\\photos");
        File f = new File(resourcesPath);
        pathnamesPhotos = f.list();
        for (String path : pathnamesPhotos) {
            CarSnapshot carSnap = new CarSnapshot(resourcesPath.concat(path));
            assertEquals(path.substring(0, path.lastIndexOf('.')), recognizer.recognize(carSnap));
        }
    }

    @Test
    public void testVideos() {

    }
}