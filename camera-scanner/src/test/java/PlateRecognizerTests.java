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
        System.out.println("opencv loaded");

        outputManager = new OutputManager(converter);
        System.out.println("output manager created");
        timeSupplier = System::currentTimeMillis;
        System.out.println("time supplier created");
        timer = new Timer();
        System.out.println("timer created");
        converter = new Converter();
        System.out.println("converter created");
        adapter = new VideoFileAdapter("/home/r3m1g1u52/Projekty/automatic-parking-lot-system/camera-scanner/src/test/resources/input/GX123XD.mp4",
                timeSupplier);
        System.out.println("videofile adapter created");
        plateFinder = new TestPlateFinder(timeSupplier, converter);
        System.out.println("plate finder created");
        tesseract = new Tesseract();
        System.out.println("tesseract created");
        textReader = new TestTesseractReader(tesseract, timeSupplier);
        System.out.println("tesseract reader created");
        System.out.println("======================================");

        Mat tempFrame = null;
        BufferedImage plate = null;
        String text = "";

        while (text.equals("")) {
            while (plate == null) {
                System.out.print("plate loop => ");
                tempFrame = this.adapter.getFrame();
                if (!tempFrame.empty()) {
                    System.out.println("full");
                    //debugger.debug(tempFrame);
                    plate = plateFinder.find(tempFrame);
                }
                else
                {System.out.println("empty");}
                //timer.await(300);
            }
            text = textReader.read(plate);
            System.out.println("text loop -> " + text);
        }

        assertNotNull(plate);
        assertEquals("GX123XD", text);

    }
}
