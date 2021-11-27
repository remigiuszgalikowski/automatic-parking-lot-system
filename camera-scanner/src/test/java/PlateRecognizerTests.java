import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
        VideoCapture video = new VideoCapture();
        Mat frame = new Mat();
        String[] filenames = f.list();
        for (String filename : filenames) {
            video.open(resourcesPath + filename);
            while (video.grab()) {
                video.retrieve(frame);
                if (recognizer.recognize(Mat2BufferedImage(frame)) == filename.substring(0, filename.lastIndexOf('.')))
                {
                    break;
                }
            }
            video.release();
            assertEquals(filename.substring(0, filename.lastIndexOf('.')),
                    recognizer.recognize(Mat2BufferedImage(frame)));
        }
    }

    public static BufferedImage Mat2BufferedImage(Mat mat) throws IOException{
        //Encoding the image
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, matOfByte);
        //Storing the encoded Mat in a byte array
        byte[] byteArray = matOfByte.toArray();
        //Preparing the Buffered Image
        InputStream in = new ByteArrayInputStream(byteArray);
        BufferedImage bufImage = ImageIO.read(in);
        return bufImage;
    }
}
