import net.sourceforge.tess4j.Tesseract;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;


public class PlateRecognizerTests {

    Debugger debugger;

    Supplier<Long> timeSupplier;
    Timer timer;
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

        timeSupplier = System::currentTimeMillis;
        timer = new Timer();
        converter = new Converter();
        adapter = new TestAdapterMat("C:\\Users\\HP\\IdeaProjects\\automatic-parking-lot-system\\camera-scanner\\src\\test\\resources\\videos\\GD123XK.mp4",
                timeSupplier,
                debugger,
                false);
        motionDetector = new TestMotionDetector(adapter, timeSupplier, timer,50);
        plateRecognizer = new TestPlateRecognizer(adapter, timeSupplier, converter);
        tesseract = new Tesseract();
        textReader = new TestTesseractReader(tesseract, timeSupplier);

        BufferedImage plate = null;
        while(!motionDetector.detect()) {}
        while (plate == null) {
            plate = plateRecognizer.recognize();
            timer.await(300);
        }
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
