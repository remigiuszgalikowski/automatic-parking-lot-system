import org.junit.jupiter.api.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class PlateRecognizerTests {

    TestPlateRecognizer plateRecognizer;
    Adaptation adapter;

    public PlateRecognizerTests() {
    }

    @Test
    public void a() {
        System.load("C:\\Users\\HP\\IdeaProjects\\automatic-parking-lot-system\\libraries\\opencv\\build\\java\\x64\\opencv_java454.dll");
        adapter = new TestAdapterMat("C:\\Users\\HP\\IdeaProjects\\automatic-parking-lot-system\\camera-scanner\\src\\test\\resources\\videos\\GD123XK.mp4");
        plateRecognizer = new TestPlateRecognizer(adapter);
        assertTrue(adapter.videoCapture.isOpened());
        plateRecognizer.recognize();
        String text = plateRecognizer.recognize();
        long detectMotionAvgTime = plateRecognizer.getAvgMotionDetectionTime();
        long recognizePlateAvgTime = plateRecognizer.getAvgPlateRecognitionTime();
        System.out.println("detectMotionAvgTime: " + detectMotionAvgTime);
        System.out.println("recognizePlateAvgTime: " + recognizePlateAvgTime);
        assertTrue(detectMotionAvgTime < recognizePlateAvgTime);
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
