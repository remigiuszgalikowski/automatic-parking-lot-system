import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlateRecognizer implements Recognizer {

    private final Adapter adapter;
    private final Converter imageConverter;

    public PlateRecognizer(Adapter adapter, Converter converter) {
        this.adapter = adapter;
        this.imageConverter = converter;
    }

//    @Override
//    public String recognize() {
//        Mat currentFrameMiniature;
//        Mat previousFrameMiniature;
//        Mat currentFrame;
//        do {
//            previousFrameMiniature = this.adapter.getFrameMiniature();
//            this.waitForNextFrame(MOVEMENT_DETECTION_INTERVAL);
//            currentFrameMiniature = this.adapter.getFrameMiniature();
//        } while (!detect(previousFrameMiniature, currentFrameMiniature));
//        String plateText;
//        long startRecognitionTime = this.timeSupplier.get();
//        do {
//            currentFrame = this.adapter.getFrame();
//            List<Mat> plateCandidates = this.detectPlates(currentFrame);
//            plateText = this.readPlates(plateCandidates);
//            System.out.println(plateText);
//            if (plateText != null) return plateText;
//            this.waitForNextFrame(PLATE_RECOGNITION_INTERVAL);
//        } while (PLATE_RECOGNITION_TIME >= this.timeSupplier.get() - startRecognitionTime);
//        return null;
//    }

    @Override
    public BufferedImage recognize(Mat image) {

        Mat grayScale = new Mat();
        Imgproc.cvtColor(image, grayScale, Imgproc.COLOR_BGR2GRAY);
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

        Mat plateCandidate = null;
        for (MatOfPoint mop : contours) {
            MatOfPoint2f mopAprox = new MatOfPoint2f();
            double perimeter = Imgproc.arcLength(new MatOfPoint2f(mop.toArray()), true);
            Imgproc.approxPolyDP(new MatOfPoint2f(mop.toArray()), mopAprox, 0.018 * perimeter, true);
            if (isPlate(mopAprox)) {
                Rect rect = this.createRectangle(mopAprox);
                plateCandidate = bilateral.submat(rect);
                this.adapter.setHighlightedFrame(this.highlightPlate(image, rect));
            }
            if (plateCandidate != null) return this.imageConverter.toBufferedImage(plateCandidate);
        }
        return null;

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

    private Mat highlightPlate(Mat mat, Rect rectangle) {
        Mat highlightedMat = mat;
        Imgproc.rectangle(highlightedMat, rectangle, new Scalar(0,255,0,255), 4);
        return highlightedMat;
    }

}