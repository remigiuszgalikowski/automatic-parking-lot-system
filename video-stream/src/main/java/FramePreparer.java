import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;


public class FramePreparer implements Preparer {

    private final Adapter<Mat> adapter;
    private final Converter converter;
    private final PlateRecognizer plateRecognizer;

    public FramePreparer(Adapter<Mat> adapter, Converter converter, PlateRecognizer plateRecognizer) {
        this.adapter = adapter;
        this.plateRecognizer = plateRecognizer;
        this.converter = converter;
    }

    @Override
    public BufferedImage getPreparedFrame() {
        Mat frame = this.outlinePlate(this.adapter.getFrame(), this.plateRecognizer.getPlateCoords());
        return this.converter.toBufferedImage(frame);
    }

    private Mat outlinePlate(Mat mat, Rect rectangle) {
        Mat highlightedMat = mat;
        if (rectangle != null) {
            Imgproc.rectangle(highlightedMat, rectangle, new Scalar(0, 255, 0, 255), 4);
        }
        return highlightedMat;
    }

}
