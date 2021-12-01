import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.core.Core;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class PlateRecognizer implements Recognition, MotionDetection {

    private final Adaptation adapter;
    private Mat previousFrame;

    public PlateRecognizer(Adaptation adapter) {
        this.adapter = adapter;
        this.previousFrame = this.adapter.getFrame();
    }

    @Override
    public String recognize() {
        while (detectMotion(this.adapter.getFrame(), this.previousFrame)) {
            return examine(this.adapter.getFrame());
        }
        return null;
    }

    private String examine(Mat frame) {
        return null;
    }

    @Override
    public boolean detectMotion(Mat currentFrame, Mat previousFrame) {
        BufferedImage temp;
        Mat frame1 = new Mat();
        Mat frame2 = new Mat();
        Mat subtraction = new Mat();
        Mat threshold = new Mat();
        Imgproc.cvtColor(currentFrame, frame1, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(previousFrame, frame2, Imgproc.COLOR_BGR2GRAY);
        try {
            temp = Mat2BufferedImage(currentFrame);
            TimeUnit.SECONDS.sleep(10);
            temp = Mat2BufferedImage(previousFrame);
            TimeUnit.SECONDS.sleep(10);

            temp = Mat2BufferedImage(frame1);
            TimeUnit.SECONDS.sleep(10);
            temp = Mat2BufferedImage(frame2);
            TimeUnit.SECONDS.sleep(10);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        Imgproc.GaussianBlur(frame1, frame1, new Size(21,21),0);
        Imgproc.GaussianBlur(frame2, frame2, new Size(21,21),0);
        Core.absdiff(frame1, frame2, subtraction);
        Imgproc.threshold(subtraction,threshold,25,255, Imgproc.THRESH_BINARY);
        //Imgproc.dilate();
        //Imgproc.dilate();
        this.previousFrame = currentFrame;
        return false;
    }

        public static BufferedImage Mat2BufferedImage(Mat mat) throws IOException {
        //Encoding the image
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, matOfByte);
        //Storing the encoded Mat in a byte array
        byte[] byteArray = matOfByte.toArray();
        //Preparing the Buffered Image
        InputStream in = new ByteArrayInputStream(byteArray);
        BufferedImage bufImage = ImageIO.read(in);
        return bufImage;
    }

}