import net.sourceforge.tess4j.Tesseract;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


public class PlateRecognizerTests {

    Debugger debugger;

    Converter converter;
    TestAdapterMat adapter;
    TestMotionDetector motionDetector;
    TestPlateRecognizer plateRecognizer;
    Tesseract tesseract;
    TestTesseractReader textReader;

    public PlateRecognizerTests() {
    }

    @Test
    public void a() {
        System.load("C:\\Users\\HP\\IdeaProjects\\automatic-parking-lot-system\\libraries\\opencv\\build\\java\\x64\\opencv_java454.dll");

        debugger = new Debugger();

        converter = new Converter();
        adapter = new TestAdapterMat("C:\\Users\\HP\\IdeaProjects\\automatic-parking-lot-system\\camera-scanner\\src\\test\\resources\\videos\\GD123XK.mp4",
                debugger,
                false);
        motionDetector = new TestMotionDetector();
        plateRecognizer = new TestPlateRecognizer(adapter, converter);
        tesseract = new Tesseract();
        textReader = new TestTesseractReader(tesseract);

        Mat frame = null;
        BufferedImage plate = null;
        Mat miniature1 = null;
        Mat miniature2 = null;
        do {
            miniature1 = adapter.getFrameMiniature();
            this.waitForNextFrame(300);
            miniature2 = adapter.getFrameMiniature();
        } while(!motionDetector.detect(miniature1, miniature2));
        while (plate == null) {
            this.waitForNextFrame(2400);
            frame = adapter.getFrame();
            plate = plateRecognizer.recognize(frame);
        }
        assertNotNull(frame);
        assertNotNull(plate);

        String text;
        text = textReader.read(plate);

        long detectionDuration = this.motionDetector.getAvgDuration();
        long recognitionDuration = plateRecognizer.getAvgDuration();
        long readingDuration = textReader.getAvgDuration();
        System.out.println("Detection time: " + detectionDuration);
        System.out.println("Recognition time: " + recognitionDuration);
        System.out.println("Reading time: " + readingDuration);



        assertEquals("GD123XK", text);

        this.debugger.debug((Mat) this.adapter.getHighlightedFrame(), "getHighlightedFrame()");

    }

    private void waitForNextFrame(long miliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(miliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void loadOpenCV() {
        String currentDir = new File("").getAbsolutePath();
        String arch = System.getProperty("sun.arch.data.model");

        switch (arch) {
            case "64":
                System.load(currentDir + "\\libraries\\opencv\\build\\java\\x64\\opencv_java454.dll");
                break;
            case "32":
                System.load(currentDir + "\\libraries\\opencv\\build\\java\\x86\\opencv_java454.dll");
                break;
        }
    }
}
