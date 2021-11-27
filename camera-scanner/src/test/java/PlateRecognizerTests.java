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
        String resourcesPath = System.getProperty("user.dir").concat("\\src\\test\\resources\\photos\\");
        File f = new File(resourcesPath);
        String[] filenames = f.list();
        for (String filename : filenames) {
            assertEquals(filename.substring(0, filename.lastIndexOf('.')), recognizer.recognize(resourcesPath + filename));
        }
    }

    @Test
    public void testVideos() throws IOException {
        String resourcesPath = System.getProperty("user.dir").concat("\\src\\test\\resources\\videos\\");
        File f = new File(resourcesPath);
        String[] filenames = f.list();
        for (String filename : filenames) {
            assertEquals(filename.substring(0, filename.lastIndexOf('.')), recognizer.recognize(resourcesPath + filename));
        }
    }
}
