import net.sourceforge.tess4j.Tesseract;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;


public class PlateRecognizerTests {

    OutputManager outputManager;

    Supplier<Long> timeSupplier;
    Timer timer;
    Converter converter;
    VideoFileAdapter adapter;
    TestPlateFinder plateFinder;
    Tesseract tesseract;
    TestTesseractReader textReader;

    public PlateRecognizerTests() {
    }

    @Test
    public void SingleVideoTest() {
        System.load("/home/r3m1g1u52/Biblioteki/opencv-custom_build/opencv/build/lib/libopencv_java455.so");

        outputManager = new OutputManager(converter);

        timeSupplier = System::currentTimeMillis;
        timer = new Timer();
        converter = new Converter();
        adapter = new VideoFileAdapter("/home/r3m1g1u52/Projekty/automatic-parking-lot-system/camera-scanner/src/test/resources/input/GD123XK.mp4",
                timeSupplier);
        plateFinder = new TestPlateFinder(timeSupplier, converter);
        tesseract = new Tesseract();
        textReader = new TestTesseractReader(tesseract, timeSupplier);

        Mat tempFrame = null;
        BufferedImage plate = null;
        String text = "";

        while (text.equals("")) {
            while (plate == null) {
                tempFrame = this.adapter.getFrame();
                if (!tempFrame.empty()) {
                    //debugger.debug(tempFrame);
                    plate = plateFinder.find(tempFrame);
                }
                //timer.await(300);
            }
            text = textReader.read(plate);
        }

        assertNotNull(plate);
        assertEquals("GD123XK", text);

    }
}
