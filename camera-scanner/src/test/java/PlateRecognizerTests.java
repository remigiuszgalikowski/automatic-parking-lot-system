import net.sourceforge.tess4j.Tesseract;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
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
    private Streamer streamer;

    public PlateRecognizerTests() {
        System.load("/home/r3m1g1u52/Biblioteki/opencv-custom_build/opencv/build/lib/libopencv_java455.so");
        this.converter = new Converter();
        this.outputManager = new OutputManager(this.converter);
        this.timeSupplier = System::currentTimeMillis;
        this.timer = new Timer();
        this.tesseract = new Tesseract();
        this.framePreparer = new FramePreparer(new Scalar(0,255,0,255), 15);
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
        this.timer.await(30);

        Mat frame = null;
        BufferedImage plate = null;
        String text = null;

        while (text == null) {
            while (plate == null) {
                frame = this.fileAdapter.getFrame();
                this.outputManager.show(frame);
                if (!frame.empty()) {
                    plate = this.testPlateFinder.find(frame);
                }
                this.timer.await(30);
            }
            text = this.testTesseractReader.read(plate);
        }
        System.out.println(text);
        assertEquals(listOfFiles[0].getName().substring(0,listOfFiles[0].getName().indexOf(".")), text);

        try {
            this.outputManager.save(this.framePreparer.getPreparedFrame(frame, this.testPlateFinder.getPlateCoords(),text),"/home/r3m1g1u52/Projekty/automatic-parking-lot-system/camera-scanner/src/test/resources/visualisation","10-prepared.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Test
    public void MultipleFilesTest() {

        String dir = "/home/r3m1g1u52/Projekty/automatic-parking-lot-system/camera-scanner/src/test/resources/input/multiple";
        File folder = new File(dir);
        File[] listOfFiles = folder.listFiles();
        Mat frame;
        BufferedImage plate;
        String text;
        List<String > contours = new ArrayList<>();

        String[][] toTxtFile = new String[listOfFiles.length][5];

        int i;
        int f = 0;
        for (File file : listOfFiles) {
            frame = null;
            plate = null;
            text = null;
            if (file.isFile()) {
                i = 0;
                this.fileAdapter = new VideoFileAdapter(dir + "/" + file.getName(), this.timeSupplier);
                frame = this.fileAdapter.getFrame();
                while (text == null ) {
                    while (plate == null) {
                        frame = this.fileAdapter.getFrame();
                        if (!frame.empty()) {
                            plate = this.testPlateFinder.find(frame);
                            i++;
                            if (i > 30) { break;}
                        }
                        this.timer.await(31);
                    }
                    if (plate != null) {
                        text = this.testTesseractReader.read(plate);
                    }
                    if (i > 30) { break;}
                };
                if (!frame.empty()) {
                    try {
                        this.outputManager.save(this.framePreparer.getPreparedFrame(frame, this.testPlateFinder.getPlateCoords(), text), "/home/r3m1g1u52/Projekty/automatic-parking-lot-system/camera-scanner/src/test/resources/output", file.getName().substring(0, file.getName().indexOf('.')) + ".png");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                toTxtFile[f][0] = file.getName();
                toTxtFile[f][1] = text;
                toTxtFile[f][2] = "finder: "+this.testPlateFinder.getDuration();
                toTxtFile[f][3] = "OCR: "+this.testTesseractReader.getDuration();
                long tmp = this.testPlateFinder.getDuration()+this.testTesseractReader.getDuration();
                toTxtFile[f][4] = "sum: "+tmp;
                f++;
            }
            this.fileAdapter = null;
        }
        for (String[] r : toTxtFile) {
            System.out.println(r[0] + " | " + r[1] + " | " + r[2] + " | " + r[3] + " | " + r[4]);
        }

    }

    @Test
    public void StreamTest() {
        this.streamAdapter = new VideoStreamAdapter(0);
        this.streamer = new Streamer(this.timer,this.streamAdapter,this.plateFinder,this.tesseractReader,this.framePreparer);
        this.streamer.start();
    }

}