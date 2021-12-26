import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class PlateRecognizer implements Recognition, MotionDetection {

    private final Adaptation adapter;
    private final Tesseract tesseract;
    private final Supplier<Long> timeSupplier;

    static double MINIMAL_MOVEMENT_RATIO = 3;

    static long PLATE_RECOGNITION_TIME = 3 * 60 * 1000;
    static long MOVEMENT_DETECTION_INTERVAL = 10 * 1000;
    static long PLATE_RECOGNITION_INTERVAL = 1 * 1000;

    public PlateRecognizer(Adaptation adapter) {
        this.adapter = adapter;
        this.tesseract = new Tesseract();
        this.tesseract.setDatapath("C:\\Users\\HP\\IdeaProjects\\automatic-parking-lot-system\\libraries\\Tesseract-OCR\\tessdata");
        this.timeSupplier = System::currentTimeMillis;
    }

    @Override
    public String recognize() {
        Mat currentFrameMiniature;
        Mat previousFrameMiniature;
        Mat currentFrame;
        do {
            previousFrameMiniature = this.adapter.getFrameMiniature();
            this.waitForNextFrame(MOVEMENT_DETECTION_INTERVAL);
            currentFrameMiniature = this.adapter.getFrameMiniature();
        } while (!detectMotion(previousFrameMiniature, currentFrameMiniature));
        String plateText;
        long startRecognitionTime = this.timeSupplier.get();
        do {
            currentFrame = this.adapter.getFrame();
            List<Mat> plateCandidates = this.detectPlates(currentFrame);
            plateText = this.readPlates(plateCandidates);
            System.out.println(plateText);
            if (plateText != null) return plateText;
            this.waitForNextFrame(PLATE_RECOGNITION_INTERVAL);
        } while (PLATE_RECOGNITION_TIME >= this.timeSupplier.get() - startRecognitionTime);
        return null;
    }

    @Override
    public boolean detectMotion(Mat currentFrame, Mat previousFrame) {

        long startTime = this.timeSupplier.get();

        Mat frame1 = new Mat();
        Mat frame2 = new Mat();
        Imgproc.cvtColor(currentFrame, frame1, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(previousFrame, frame2, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(frame1, frame1, new Size(21, 21), 0);
        Imgproc.GaussianBlur(frame2, frame2, new Size(21, 21), 0);
        Mat subtraction = new Mat();
        Core.absdiff(frame1, frame2, subtraction);

        double valueFromMatrix = 0;

        for (int h = 0; h < subtraction.height(); h++) {
            for (int w = 0; w < subtraction.width(); w++) {
                if (subtraction.get(w, h) != null) {
                    valueFromMatrix += subtraction.get(w, h)[0];
                }
            }
        }

        valueFromMatrix /= subtraction.width() * subtraction.height();

        //Temporary
        //System.out.println(valueFromMatrix);
        //this.debug(subtraction, "subtraction");
        //-----------

        long timeStamp = this.timeSupplier.get() - startTime;
        System.out.println("motionDetect: " + timeStamp);

        return valueFromMatrix > MINIMAL_MOVEMENT_RATIO;
    }

    private List<Mat> detectPlates(Mat frameForAnalysis) {
        Mat grayScale = new Mat();
        Imgproc.cvtColor(frameForAnalysis, grayScale, Imgproc.COLOR_BGR2GRAY);
        Mat bilateral = new Mat();
        Imgproc.bilateralFilter(grayScale, bilateral, 13, 15, 15);
        Mat canny = new Mat();
        Imgproc.Canny(bilateral, canny, 30, 200);

        Mat hierarchyMat = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(canny, contours, hierarchyMat, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        hierarchyMat.release();
        contours.sort(new Comparator<MatOfPoint>() {
            public int compare(MatOfPoint mop1, MatOfPoint mop2) {
                return (int) ((Imgproc.contourArea(mop1) - Imgproc.contourArea(mop2)) * -1);
            }
        });
        contours = contours.subList(0,10);

        List<Mat> plateCandidates = new ArrayList<>();
        for (MatOfPoint mop : contours) {
            MatOfPoint2f mopAprox = new MatOfPoint2f();
            double perimeter = Imgproc.arcLength(new MatOfPoint2f(mop.toArray()), true);
            Imgproc.approxPolyDP(new MatOfPoint2f(mop.toArray()), mopAprox, 0.018 * perimeter, true);
            if (isPlate(mopAprox)) {
                //this.debug(mopAprox, frameForAnalysis, "plate highlighted");
                plateCandidates.add(bilateral.submat(this.createRectangle(mopAprox)));
                //this.debug(plateCandidates.get(0), "plate only");
            }
        }
        return plateCandidates;

//        contours.stream()
//                .map(mop -> {
//                    MatOfPoint2f mopAprox = new MatOfPoint2f();
//                    double perimeter = Imgproc.arcLength(new MatOfPoint2f(mop.toArray()), true);
//                    Imgproc.approxPolyDP(new MatOfPoint2f(mop.toArray()), mopAprox, 0.018 * perimeter, true);
//                    return mopAprox;
//                })
//                .filter(this::isPlate)
//                .forEach((mopApprox) -> this.debug(mopApprox, frameForAnalysis));
    }

    private String readPlates(List<Mat> plates) {
        String plateText;
        for(Mat plate : plates) {
            try {
                plateText = this.tesseract.doOCR(this.mat2BufferedImage(plate));
                plateText = plateText.replaceAll("\\s+","");
                System.out.println(plateText);
            } catch (TesseractException e) {
                plateText = null;
                System.out.println("No plate text");
            }
            if (plateText != null) return plateText;
        }
        return null;
    }

    private void waitForNextFrame(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isPlate(MatOfPoint2f suspectedPlate) {

        org.opencv.core.Point[] sus = suspectedPlate.toArray();

        if (sus.length != 4) return false;

        List<Double> sides = new ArrayList(4);
        sides.add(distance(sus[0], sus[1]));
        sides.add(distance(sus[1], sus[2]));
        sides.add(distance(sus[2], sus[3]));
        sides.add(distance(sus[3], sus[0]));
        sides.sort(Comparator.reverseOrder());

        double ratioAB = sides.get(0) / sides.get(2);
        double ratioCD = sides.get(1) / sides.get(3);

        var maxPlateRatio = 5.0;
        var minPlateRatio = 4.0;

        return maxPlateRatio > ratioAB && ratioAB > minPlateRatio
                && maxPlateRatio > ratioCD && ratioCD > minPlateRatio;
    }

    private double distance(org.opencv.core.Point point1, org.opencv.core.Point point2) {
        return Math.sqrt((point1.x - point2.x) * (point1.x - point2.x) + (point1.y - point2.y) * (point1.y - point2.y));
    }

    private Rect createRectangle(MatOfPoint2f matOfPoints) {
        List<Point> points = matOfPoints.toList();
        var rightBorder = points.stream()
                .map(point -> point.x)
                .max(Double::compare).orElse(0.0);
        var leftBorder = points.stream()
                .map(point -> point.x)
                .min(Double::compare).orElse(0.0);
        var topBorder = points.stream()
                .map(point -> point.y)
                .max(Double::compare).orElse(0.0);
        var bottomBorder = points.stream()
                .map(point -> point.y)
                .min(Double::compare).orElse(0.0);
        return new Rect(new Point(leftBorder, bottomBorder), new Point(rightBorder, topBorder));
    }

    public void debug(MatOfPoint2f mop, Mat inputMat, String name) {
        Mat outputMat = inputMat;
        Imgproc.line(outputMat, mop.toList().get(0), mop.toList().get(1), new Scalar(255, 255, 255, 255), 3);
        Imgproc.line(outputMat, mop.toList().get(1), mop.toList().get(2), new Scalar(255, 255, 255, 255), 3);
        Imgproc.line(outputMat, mop.toList().get(2), mop.toList().get(3), new Scalar(255, 255, 255, 255), 3);
        Imgproc.line(outputMat, mop.toList().get(3), mop.toList().get(0), new Scalar(255, 255, 255, 255), 3);
        HighGui.imshow("debug:"+ name, outputMat);
        HighGui.waitKey();
    }
    public void debug(Mat inputMat, String name) {
        Mat outputMat = inputMat;
        HighGui.imshow("debug:"+ name, outputMat);
        HighGui.waitKey();
    }

    private BufferedImage mat2BufferedImage(Mat mat) {
        try {
        //Encoding the image
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, matOfByte);
        //Storing the encoded Mat in a byte array
        byte[] byteArray = matOfByte.toArray();
        //Preparing the Buffered Image
        InputStream in = new ByteArrayInputStream(byteArray);
        BufferedImage buffImage = null;
        buffImage = ImageIO.read(in);
        return buffImage;
        }
        catch (IOException e) {
            return null;
        }
    }

}