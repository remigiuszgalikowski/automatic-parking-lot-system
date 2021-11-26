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
        String[] pathnamesToPhotos;
        String resourcesPath = System.getProperty("user.dir").concat("\\src\\test\\resources\\photos");
        File f = new File(resourcesPath);
        pathnamesToPhotos = f.list();
        for (String path : pathnamesToPhotos) {
            assertEquals(path.substring(0, path.lastIndexOf('.')), recognizer.recognize(path));
        }
    }

    @Test
    public void testVideos() {

    }
}
