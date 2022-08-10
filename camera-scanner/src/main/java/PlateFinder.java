import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class PlateFinder implements Finder<BufferedImage, Mat> {

    private final Converter imageConverter;

    private Rect plateCoords;

    private OutputManager outputManager;

    public PlateFinder(Converter converter) {
        this.imageConverter = converter;
        this.plateCoords = null;
        this.outputManager = new OutputManager(imageConverter);
    }

    @Override
    public BufferedImage find(Mat input) {

        try {
            this.outputManager.save(input,
                    "/home/r3m1g1u52/Projekty/automatic-parking-lot-system/camera-scanner/src/test/resources/visualisation",
                    "1-captured.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Mat grayScale = new Mat();
        Imgproc.cvtColor(input , grayScale, Imgproc.COLOR_BGR2GRAY);

        try {
            this.outputManager.save(grayScale,
                    "/home/r3m1g1u52/Projekty/automatic-parking-lot-system/camera-scanner/src/test/resources/visualisation",
                    "2-grayscale.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Mat bilateral = new Mat();
        Imgproc.bilateralFilter(grayScale, bilateral, 13, 15, 15);

        try {
            this.outputManager.save(bilateral,
                    "/home/r3m1g1u52/Projekty/automatic-parking-lot-system/camera-scanner/src/test/resources/visualisation",
                    "3-bilateral.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Mat gaussian = new Mat();

        Imgproc.GaussianBlur(bilateral, gaussian, new Size(5,5),1);

        try {
            this.outputManager.save(gaussian,
                    "/home/r3m1g1u52/Projekty/automatic-parking-lot-system/camera-scanner/src/test/resources/visualisation",
                    "4-gauss.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Mat sobelx = new Mat();

        Imgproc.Sobel(gaussian, sobelx, CvType.CV_32F,1,0,3 );

        try {
            this.outputManager.save(sobelx,
                    "/home/r3m1g1u52/Projekty/automatic-parking-lot-system/camera-scanner/src/test/resources/visualisation",
                    "5x-sobel.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Mat sobely = new Mat();

        Imgproc.Sobel(gaussian, sobely, CvType.CV_32F,0,1,3 );

        try {
            this.outputManager.save(sobely,
                    "/home/r3m1g1u52/Projekty/automatic-parking-lot-system/camera-scanner/src/test/resources/visualisation",
                    "5y-sobel.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Mat gradientIntensity = new Mat();

        Mat sobelx2 = new Mat();
        Core.pow(sobelx, 2, sobelx2);
        Mat sobely2 = new Mat();
        Core.pow(sobely, 2, sobely2);

        Mat sobelSum = new Mat();
        Core.add(sobelx2,sobely2, sobelSum);

        Core.sqrt(sobelSum, gradientIntensity);

        try {
            this.outputManager.save(gradientIntensity,
                    "/home/r3m1g1u52/Projekty/automatic-parking-lot-system/camera-scanner/src/test/resources/visualisation",
                    "6-gradient_intensity.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        float[][] angles = new float[sobelSum.cols()][sobelSum.rows()];

        for(int c = 0; c<gradientIntensity.cols()-1; c++) {
            for (int r = 0; r<gradientIntensity.rows(); r++) {
                angles[c][r] = Core.fastAtan2((float) sobely.get(r,c)[0], (float) sobelx.get(r,c)[0]);
            }
        }

        Mat sobelDiv = new Mat();
        Core.divide(sobely, sobelx, sobelDiv);

        try {
            this.outputManager.save(sobelDiv,
                    "/home/r3m1g1u52/Projekty/automatic-parking-lot-system/camera-scanner/src/test/resources/visualisation",
                    "DIV.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Mat nonMaxSuppressed = new Mat();
        gradientIntensity.copyTo(nonMaxSuppressed);


        for(int c = 1; c < gradientIntensity.cols() - 2; c++) {
            for (int r = 1; r < gradientIntensity.rows() - 2; r++) {

                double tempq = 255;
                double tempr = 255;

                //angle 0
                if ((0.0 <= angles[c][r] && angles[c][r] < 22.5) || (157.5 <= angles[c][r] && angles[c][r] <= 180)) {
                    tempq = gradientIntensity.get(r + 1, c)[0];
                    tempr = gradientIntensity.get(r - 1, c)[0];
                }
                //angle 45
                else if (22.5 <= angles[c][r] && angles[c][r] < 67.5) {
                    tempq = gradientIntensity.get(r - 1, c + 1)[0];
                    tempr = gradientIntensity.get(r + 1, c - 1)[0];
                }
                //angle 90
                else if (67.5 <= angles[c][r] && angles[c][r] < 112.5) {
                    tempq = gradientIntensity.get(r, c + 1)[0];
                    tempr = gradientIntensity.get(r, c - 1)[0];
                }
                //angle 135
                else if (112.5 <= angles[c][r] && angles[c][r] < 157.5) {
                    tempq = gradientIntensity.get(r + 1, c + 1)[0];
                    tempr = gradientIntensity.get(r - 1, c - 1)[0];
                }

                if (gradientIntensity.get(r, c)[0] >= tempq && gradientIntensity.get(r, c)[0] >= tempr) {
                    nonMaxSuppressed.put(r, c, new double[]{255.0});
                } else {
                    nonMaxSuppressed.put(r, c, new double[]{0.0});
                }

            }
        }

                try {
                    this.outputManager.save(nonMaxSuppressed,
                            "/home/r3m1g1u52/Projekty/automatic-parking-lot-system/camera-scanner/src/test/resources/visualisation",
                            "7-dir_sorted.png");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }



//        for i in range(1,M-1):
//        for j in range(1,N-1):
//        try:
//        q = 255
//        r = 255
//
//               #angle 0
//        if (0 <= angle[i,j] < 22.5) or (157.5 <= angle[i,j] <= 180):
//        q = img[i, j+1]
//        r = img[i, j-1]
//                #angle 45
//        elif (22.5 <= angle[i,j] < 67.5):
//        q = img[i+1, j-1]
//        r = img[i-1, j+1]
//                #angle 90
//        elif (67.5 <= angle[i,j] < 112.5):
//        q = img[i+1, j]
//        r = img[i-1, j]
//                #angle 135
//        elif (112.5 <= angle[i,j] < 157.5):
//        q = img[i-1, j-1]
//        r = img[i+1, j+1]
//
//        if (img[i,j] >= q) and (img[i,j] >= r):
//        Z[i,j] = img[i,j]
//                else:
//        Z[i,j] = 0
//
//        except IndexError as e:
//        pass
//
//        return Z

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

    public Rect getPlateCoords() {
        return this.plateCoords;
    }
}