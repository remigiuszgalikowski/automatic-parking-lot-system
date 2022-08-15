import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlateFinder implements Finder<BufferedImage, Mat> {

    private final Converter imageConverter;

    private Rect plateCoords;

    public PlateFinder(Converter converter) {
        this.imageConverter = converter;
        this.plateCoords = null;
    }

    @Override
    public BufferedImage find(Mat input) {

        Mat grayScale = new Mat();
        Imgproc.cvtColor(input , grayScale, Imgproc.COLOR_BGR2GRAY);

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
                this.plateCoords = this.createRectangle(mopAprox);
                plateCandidate = bilateral.submat(this.plateCoords);
            }
            if (plateCandidate != null) return this.imageConverter.toBufferedImage(plateCandidate);
        }
        return null;

    }

    private boolean isPlate(MatOfPoint2f suspectedPlate) {

        org.opencv.core.Point[] sus = suspectedPlate.toArray();

        if (sus.length != 4) return false;

        List<Double> sides = new ArrayList<>(4);
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

    public Rect getPlateCoords() {
        return this.plateCoords;
    }
}