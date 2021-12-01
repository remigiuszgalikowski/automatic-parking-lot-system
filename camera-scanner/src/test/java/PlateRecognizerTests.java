import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PlateRecognizerTests {

    PlateRecognizer plateRecognizer;
    Adaptation adapter;

    public PlateRecognizerTests() {
    }

//    @Test
//    public void testPhotos() {
//        adapter = new AdapterMat(0);
//        plateRecognizer = new PlateRecognizer(adapter);
//        String resourcesPath = System.getProperty("user.dir").concat("\\src\\test\\resources\\photos\\");
//        File f = new File(resourcesPath);
//        String[] filenames = f.list();
//        Mat photo;
//        for (String filename : filenames) {
//            photo = Imgcodecs.imread(resourcesPath.concat(filename));
//            assertEquals(filename.substring(0, filename.lastIndexOf('.')), plateRecognizer.examine(photo));
//        }
//    }


//    @Test
//    public void testVideos() {
//        String resourcesPath = System.getProperty("user.dir").concat("\\src\\test\\resources\\photos\\");
//        File f = new File(resourcesPath);
//        String[] filenames = f.list();
//        for (String filename : filenames) {
//            adapter = new AdapterMat(resourcesPath.concat(filename));
//            plateRecognizer = new PlateRecognizer(adapter);
//            assertEquals(filename.substring(0, filename.lastIndexOf('.')), plateRecognizer.recognize());
//        }
//    }

    @Test
    public void a() {
        adapter = new AdapterMat("C:\\Users\\HP\\IdeaProjects\\automatic-parking-lot-system\\camera-scanner\\src\\test\\resources\\videos\\GD376SG.mp4");
        plateRecognizer = new PlateRecognizer(adapter);
        plateRecognizer.recognize();
    }

//    @Test
//    public void testVideos() throws IOException {
//        String resourcesPath = System.getProperty("user.dir").concat("\\src\\test\\resources\\videos\\");
//        File f = new File(resourcesPath);
//        VideoCapture video = new VideoCapture();
//        Mat frame = new Mat();
//        String[] filenames = f.list();
//        for (String filename : filenames) {
//            video.open(resourcesPath + filename);
//            while (video.grab()) {
//                video.retrieve(frame);
//                if (recognizer.recognize(Mat2BufferedImage(frame)) == filename.substring(0, filename.lastIndexOf('.')))
//                {
//                    break;
//                }
//            }
//            video.release();
//            assertEquals(filename.substring(0, filename.lastIndexOf('.')),
//                    recognizer.recognize(Mat2BufferedImage(frame)));
//        }
//    }
//
//    public static BufferedImage Mat2BufferedImage(Mat mat) throws IOException{
//        //Encoding the image
//        MatOfByte matOfByte = new MatOfByte();
//        Imgcodecs.imencode(".jpg", mat, matOfByte);
//        //Storing the encoded Mat in a byte array
//        byte[] byteArray = matOfByte.toArray();
//        //Preparing the Buffered Image
//        InputStream in = new ByteArrayInputStream(byteArray);
//        BufferedImage bufImage = ImageIO.read(in);
//        return bufImage;
//    }
}
