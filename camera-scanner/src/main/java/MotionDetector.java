import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MotionDetector implements Detector {

    static double MINIMAL_MOVEMENT_RATIO = 3;

    public MotionDetector() {}

    @Override
    public boolean detect(Object previousFrame, Object currentFrame) {

        Mat frame1 = new Mat();
        Mat frame2 = new Mat();
        Imgproc.cvtColor((Mat) currentFrame, frame1, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor((Mat) previousFrame, frame2, Imgproc.COLOR_BGR2GRAY);
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

        return valueFromMatrix > MINIMAL_MOVEMENT_RATIO;
    }

}