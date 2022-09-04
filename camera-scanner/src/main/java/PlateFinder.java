import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.*;

public class PlateFinder implements Finder<List<PlateImage>, Mat> {

    private final Converter imageConverter;

    public PlateFinder(Converter converter) {
        this.imageConverter = converter;
    }

    @Override
    public List<PlateImage> find(Mat input) {
        Mat grayScale = new Mat();
        Imgproc.cvtColor(input , grayScale, Imgproc.COLOR_BGR2GRAY);
        Mat bilateral = new Mat();
        Imgproc.bilateralFilter(grayScale, bilateral, 13, 33, 33);
        Mat canny = new Mat();
        Imgproc.Canny(bilateral, canny, 30, 200);
        Mat hierarchyMat = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(canny, contours, hierarchyMat, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        hierarchyMat.release();
        List<MatOfPoint> rectContours = new ArrayList<>();
        for (MatOfPoint mop : contours) {
            MatOfPoint2f mopAprox = new MatOfPoint2f();
            double perimeter = Imgproc.arcLength(new MatOfPoint2f(mop.toArray()), true);
            Imgproc.approxPolyDP(new MatOfPoint2f(mop.toArray()), mopAprox, 0.018 * perimeter, true);

            if (mopAprox.toArray().length < 20 && mopAprox.toArray().length > 3) {
                if (Imgproc.contourArea(mopAprox) > 250) {
                    MatOfPoint m = new MatOfPoint();
                    mopAprox.convertTo(m, CvType.CV_32S);
                    rectContours.add(m);
                }
            }
        }
        rectContours.sort(new Comparator<MatOfPoint>() {
            public int compare(MatOfPoint mop1, MatOfPoint mop2) {
                return (int) ((Imgproc.contourArea(mop1) - Imgproc.contourArea(mop2)) * -1);
            }
        });
        List<PlateImage> plateCandidates = new ArrayList<PlateImage>() {};
        for (MatOfPoint mop : rectContours) {
            if (isPlate(mop)) {
                Rect plateCords = this.createRectangle(mop);
                boolean isNotTooClose = true;
                for (PlateImage pi : plateCandidates) {
                    if (distance(
                            new Point(pi.location().x, pi.location().y),
                            new Point(plateCords.x, plateCords.y)
                    ) < plateCords.height) {
                        isNotTooClose = false;
                        break;
                    }
                }
                if (isNotTooClose) {
                    plateCandidates.add(
                            new PlateImage(
                                    this.imageConverter.toBufferedImage(bilateral.submat(plateCords)),
                                    plateCords
                            )
                    );
                }
            }
        }
        return plateCandidates;
    }

    private boolean isPlate(MatOfPoint suspectedPlate) {

        org.opencv.core.Point[] sus = suspectedPlate.toArray();

        Rect aproxPlate = this.createRectangle(suspectedPlate);

        double ratio = aproxPlate.width/aproxPlate.height;

        var maxStandardPlateRatio = 5.0;
        var minStanderdPlateRatio = 4.0;

        var maxReducedPlateRatio = 3.0;
        var minReducedPlateRatio = 2.0;

        if (maxStandardPlateRatio >= ratio && ratio >= minStanderdPlateRatio) return true;
        else if (maxReducedPlateRatio >= ratio && ratio >= minReducedPlateRatio) return true;
        else return false;
    }

    private Rect createRectangle(MatOfPoint matOfPoints) {
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

    private double distance(org.opencv.core.Point point1, org.opencv.core.Point point2) {
        return Math.sqrt((point1.x - point2.x) * (point1.x - point2.x) + (point1.y - point2.y) * (point1.y - point2.y));
    }
}