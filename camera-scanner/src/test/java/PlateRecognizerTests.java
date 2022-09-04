import net.sourceforge.tess4j.Tesseract;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PlateRecognizerTests {

    private final Converter converter;
    private final OutputManager outputManager;
    private final Supplier<Long> timeSupplier;
    private final Timer timer;
    private final PlateFinder plateFinder;
    private final TestPlateFinder testPlateFinder;
    private final Tesseract tesseract;
    private final TesseractReader tesseractReader;
    private final TestTesseractReader testTesseractReader;
    private final FramePreparer framePreparer;

    private VideoStreamAdapter streamAdapter;
    private VideoFileAdapter fileAdapter;

    public PlateRecognizerTests() {
        System.load("/home/r3m1g1u52/Biblioteki/opencv-custom_build/opencv/build/lib/libopencv_java455.so");
        this.converter = new Converter();
        this.outputManager = new OutputManager(this.converter);
        this.timeSupplier = System::currentTimeMillis;
        this.timer = new Timer();
        this.tesseract = new Tesseract();
        this.framePreparer = new FramePreparer(new Scalar(200,120,255,255), 15);
        this.testPlateFinder = new TestPlateFinder(this.timeSupplier, this.converter);
        this.testTesseractReader = new TestTesseractReader(this.tesseract, this.timeSupplier);
        this.plateFinder = new PlateFinder(this.converter);
        this.tesseractReader = new TesseractReader(this.tesseract);

    }

    @Test
    public void SingleVideoTest() {
        String dir = "/home/r3m1g1u52/Projekty/automatic-parking-lot-system/camera-scanner/src/test/resources/input/single";
        File folder = new File(dir);
        File[] listOfFiles = folder.listFiles();

        this.fileAdapter = new VideoFileAdapter(dir + "/" + listOfFiles[0].getName(),
                this.timeSupplier);
        this.timer.await(41);

        Mat frame = null;
        List <PlateImage> plates = new ArrayList<>();
        List <PlateReading> readings = new ArrayList<>();

        while (readings.isEmpty()) {
            while (plates.isEmpty()) {
                frame = this.fileAdapter.getFrame();
                if (frame != null) {
                    plates = this.testPlateFinder.find(frame);
                }
            }
            readings = this.testTesseractReader.read(plates);
        }
        long tmp = this.testPlateFinder.getDuration()+this.testTesseractReader.getDuration();
        System.out.println(listOfFiles[0].getName() + " | " + readings.get(0).text() + " | finder: " + this.testPlateFinder.getDuration() + " | OCR: " + this.testTesseractReader.getDuration() + " | sum: " + tmp);


        try {
            this.outputManager.save(this.framePreparer.getPreparedFrame(frame, readings),"/home/r3m1g1u52/Projekty/automatic-parking-lot-system/camera-scanner/src/test/resources/output",listOfFiles[0].getName().substring(0, listOfFiles[0].getName().indexOf('.')) + "_new" + ".png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(listOfFiles[0].getName().substring(0,listOfFiles[0].getName().indexOf(".")), readings.get(0).text());

    }

    @Test
    public void MultipleFilesTest() {

        String dir = "/home/r3m1g1u52/Projekty/automatic-parking-lot-system/camera-scanner/src/test/resources/input/multiple";
        File folder = new File(dir);
        File[] listOfFiles = folder.listFiles();
        Mat frame = null;
        List <PlateImage> plates = new ArrayList<>();
        List <PlateReading> readings = new ArrayList<>();

        int i;
        for (File file : listOfFiles) {
            frame = null;
            plates.clear();
            readings.clear();
            if (file.isFile()) {
                this.fileAdapter = new VideoFileAdapter(dir + "/" + file.getName(), this.timeSupplier);
                frame = this.fileAdapter.getFrame();
                while (readings.isEmpty()) {
                    while (plates.isEmpty()) {
                        frame = this.fileAdapter.getFrame();
                        if (frame != null) {
                            plates = this.testPlateFinder.find(frame);
                            System.out.println("aaaa");
                        }
                    }
                    if (!plates.isEmpty() && plates != null) {
                        readings = this.testTesseractReader.read(plates);
                    }
                };
                if (frame != null) {
                    try {
                        this.outputManager.save(this.framePreparer.getPreparedFrame(frame, readings), "/home/r3m1g1u52/Projekty/automatic-parking-lot-system/camera-scanner/src/test/resources/output", file.getName().substring(0, file.getName().indexOf('.')) + ".png");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                long tmp = this.testPlateFinder.getDuration()+this.testTesseractReader.getDuration();
                for (PlateReading r : readings) {
                    System.out.println(file.getName() + " | " + r.text() + " | finder: " + this.testPlateFinder.getDuration() + " | OCR: " + this.testTesseractReader.getDuration() + " | sum: " + tmp);
                }
            }
            this.fileAdapter = null;
        }
    }

}