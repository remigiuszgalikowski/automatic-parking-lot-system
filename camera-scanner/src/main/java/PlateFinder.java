import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.util.*;

public class PlateFinder implements Finder<List<PlateImage>, Mat> {

    private final Converter imageConverter;

    public PlateFinder(Converter converter) {
        this.imageConverter = converter;
    }

    @Override
    public List<PlateImage> find(Mat input) {

//        Mat scaled = new Mat();
//        Imgproc.resize(input, scaled, new Size(1280,720), 0, 0, Imgproc.INTER_AREA);

        Mat grayScale = new Mat();
        Imgproc.cvtColor(input , grayScale, Imgproc.COLOR_BGR2GRAY);

        Mat bilateral = new Mat();
        Imgproc.bilateralFilter(grayScale, bilateral, 13, 50, 50);
        Mat canny = new Mat();
        Imgproc.Canny(bilateral, canny, 60, 200);
        Mat hierarchyMat = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(canny, contours, hierarchyMat, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        hierarchyMat.release();

        OutputManager out = new OutputManager(imageConverter);

        List<MatOfPoint> rectContours = new ArrayList<>();

//        Mat inputCopy000 = input.clone();
//        for (int i = 0; i < contours.size(); i++){
//            Imgproc.drawContours(inputCopy000, contours, i, new Scalar(255, 0, 255, 255));
//        }
//        out.show(inputCopy000);

        for (MatOfPoint mop : contours) {
            MatOfPoint2f mopAprox = new MatOfPoint2f();
            double perimeter = Imgproc.arcLength(new MatOfPoint2f(mop.toArray()), true);
            Imgproc.approxPolyDP(new MatOfPoint2f(mop.toArray()), mopAprox, 0.018 * perimeter, true);
            if (mopAprox.toArray().length < 20) {
                MatOfPoint m = new MatOfPoint();
                mopAprox.convertTo(m, CvType.CV_32S);
                rectContours.add(m);
            }
        }

//        Mat inputCopy001 = input.clone();
//        for (int i = 0; i < rectContours.size(); i++){
//            Imgproc.drawContours(inputCopy001, rectContours, i, new Scalar(255, 255, 0, 255));
//        }
//        out.show(inputCopy001);


        rectContours.sort(new Comparator<MatOfPoint>() {
            public int compare(MatOfPoint mop1, MatOfPoint mop2) {
                return (int) ((Imgproc.contourArea(mop1) - Imgproc.contourArea(mop2)) * -1);
            }
        });
        if (contours.toArray().length > 18) {rectContours = rectContours.subList(0,17);}


        List<PlateImage> plateCandidates = new ArrayList<PlateImage>() {};

        for (MatOfPoint mop : rectContours) {
            if (isPlate(mop)) {
                Rect plateCords = this.createRectangle(mop);
                boolean isNotTooClose = true;
                for (PlateImage p : plateCandidates) {
                    if (this.distance(
                            new Point(p.location().x, p.location().y),
                            new Point(plateCords.x, plateCords.y)
                    ) < p.location().width*2) {
                        isNotTooClose = false;
                    }
                }
                if (isNotTooClose) {
                    plateCandidates.add(
                            new PlateImage(
                                    this.imageConverter.toBufferedImage(bilateral.submat(plateCords)),
                                    plateCords
                            ));
                }
            }
        }
        return plateCandidates;

    }

    private boolean isPlate(MatOfPoint suspectedPlate) {

        org.opencv.core.Point[] sus = suspectedPlate.toArray();

        Rect aproxPlate = this.createRectangle(suspectedPlate);

        double ratio = aproxPlate.width/aproxPlate.height;

        var maxPlateRatio = 6.0;
        var minPlateRatio = 4.0;

        return maxPlateRatio >= ratio && ratio >= minPlateRatio;
    }

    private double distance(org.opencv.core.Point point1, org.opencv.core.Point point2) {
        return Math.sqrt((point1.x - point2.x) * (point1.x - point2.x) + (point1.y - point2.y) * (point1.y - point2.y));
    }

//    private Mat normalize(Mat frame, MatOfPoint2f suspectedPlate) {
//
//        org.opencv.core.Point[] sus = suspectedPlate.toArray();
//
//        if (sus.length != 4) return null;
//
//        List<Double> sides = new ArrayList<>(4);
//        sides.add(distance(sus[0], sus[1]));
//        sides.add(distance(sus[1], sus[2]));
//        sides.add(distance(sus[2], sus[3]));
//        sides.add(distance(sus[3], sus[0]));
//        sides.sort(Collections.reverseOrder());
//
//
//        System.out.println("sus[0]: " + sus[0] + ", sus[1]: " + sus[1] + " | distance: " + sides.get(0));
//
//        if (!this.isPlate(sides)) return null;
//
//
//        double longer = Math.round((sides.get(0) + sides.get(1))/2);
//        double shorter = Math.round((sides.get(2) + sides.get(3))/2);
//
//        //if (longer < 90 || shorter < 25) return null;
//
//        System.out.println("longer: " + longer + " | 0: " + sides.get(0) + " | 0: " + sides.get(1));
//
//        MatOfPoint2f dstPoints = new MatOfPoint2f(
//                new Point(longer,0),
//                new Point(longer, shorter),
//                new Point(0,shorter),
//                new Point(0,0)
//        );
//
//
//        Mat output = new Mat();
//
//        Mat persMat = Imgproc.getPerspectiveTransform(suspectedPlate, dstPoints);
//        Size plateSize = new Size(longer,shorter);
//        Imgproc.warpPerspective(frame, output, persMat, plateSize);
//
//        return output;
//    }


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
}