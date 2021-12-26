import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

public class Debugger {

    private int debugCounter = 0;

    public void debug(MatOfPoint2f mop, Mat inputMat, String name) {
        Mat outputMat = inputMat;
        Imgproc.line(outputMat, mop.toList().get(0), mop.toList().get(1), new Scalar(255, 255, 255, 255), 3);
        Imgproc.line(outputMat, mop.toList().get(1), mop.toList().get(2), new Scalar(255, 255, 255, 255), 3);
        Imgproc.line(outputMat, mop.toList().get(2), mop.toList().get(3), new Scalar(255, 255, 255, 255), 3);
        Imgproc.line(outputMat, mop.toList().get(3), mop.toList().get(0), new Scalar(255, 255, 255, 255), 3);
        HighGui.imshow("debug("+ this.debugCounter + ")" + name, outputMat);
        HighGui.waitKey();
        this.debugCounter++;
    }

    public void debug(MatOfPoint2f mop, Mat inputMat) {
        Mat outputMat = inputMat;
        Imgproc.line(outputMat, mop.toList().get(0), mop.toList().get(1), new Scalar(255, 255, 255, 255), 3);
        Imgproc.line(outputMat, mop.toList().get(1), mop.toList().get(2), new Scalar(255, 255, 255, 255), 3);
        Imgproc.line(outputMat, mop.toList().get(2), mop.toList().get(3), new Scalar(255, 255, 255, 255), 3);
        Imgproc.line(outputMat, mop.toList().get(3), mop.toList().get(0), new Scalar(255, 255, 255, 255), 3);
        HighGui.imshow("debug("+ this.debugCounter + ")", outputMat);
        HighGui.waitKey();
        this.debugCounter++;
    }
    public void debug(Mat inputMat, String name) {
        Mat outputMat = inputMat;
        HighGui.imshow("debug("+ this.debugCounter + ")" + name, outputMat);
        HighGui.waitKey();
        this.debugCounter++;
    }

    public void debug(Mat inputMat) {
        Mat outputMat = inputMat;
        HighGui.imshow("debug("+ this.debugCounter + ")", outputMat);
        HighGui.waitKey();
        this.debugCounter++;
    }
}
