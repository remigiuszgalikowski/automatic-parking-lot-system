import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MotionDetector implements Detector {

    private final Adapter<Mat> adapter;
    private final Timer timer;
    private final long timeBetweenFrames;
    static double MINIMAL_MOVEMENT_RATIO = 3;

    public MotionDetector(Adapter<Mat> adapter, Timer timer, long timeBetweenFrames) {
        this.adapter = adapter;
        this.timer = timer;
        this.timeBetweenFrames = timeBetweenFrames;
    }

    @Override
    public boolean detect() {
        Mat frame1 = this.adapter.getFrameMiniature();
        this.timer.await(timeBetweenFrames);
        Mat frame2 = this.adapter.getFrameMiniature();
        Mat gray1 = new Mat();
        Mat gray2 = new Mat();
        Imgproc.cvtColor(frame1, gray1, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(frame2, gray2, Imgproc.COLOR_BGR2GRAY);
        Mat blur1 = new Mat();
        Mat blur2 = new Mat();
        Imgproc.GaussianBlur(gray1, blur1, new Size(21, 21), 0);
        Imgproc.GaussianBlur(gray2, blur2, new Size(21, 21), 0);
        Mat subtraction = new Mat();
        Core.absdiff(blur1, blur2, subtraction);

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