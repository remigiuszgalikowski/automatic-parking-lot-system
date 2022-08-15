import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class OutputManager {

    private final Converter imageConverter;

    private int debugCounter = 0;

    public OutputManager(Converter converter) {
        this.imageConverter = converter;
    }

    public void show(MatOfPoint2f mop, Mat inputMat, String name) {
        outline(mop, inputMat);
        HighGui.imshow("debug("+ this.debugCounter + ")" + name, inputMat);
        HighGui.waitKey();
        this.debugCounter++;
    }
    public void show(MatOfPoint2f mop, Mat inputMat) {
        outline(mop, inputMat);
        HighGui.imshow("debug("+ this.debugCounter + ")", inputMat);
        HighGui.waitKey();
        this.debugCounter++;
    }
    public void show(Mat inputMat, String name) {
        HighGui.imshow("debug("+ this.debugCounter + ")" + name, inputMat);
        HighGui.waitKey();
        this.debugCounter++;
    }
    public void show(Mat inputMat) {
        HighGui.imshow("debug("+ this.debugCounter + ")", inputMat);
        HighGui.waitKey();
        this.debugCounter++;
    }

    public void save(Mat mat, String folderPath, String filename) throws IOException {
        BufferedImage image = imageConverter.toBufferedImage(mat);
        File outputfile = new File(folderPath + '/' + filename);
        ImageIO.write(image, filename.substring(filename.indexOf(".")+1), outputfile);
    }
    public void save(BufferedImage image, String folderPath, String filename) throws IOException {
        File outputfile = new File(folderPath + '/' + filename);
        ImageIO.write(image, filename.substring(filename.indexOf(".")+1), outputfile);
    }

    private void outline(MatOfPoint2f mop, Mat inputMat) {
        Imgproc.line(inputMat, mop.toList().get(0), mop.toList().get(1), new Scalar(255, 255, 255, 255), 3);
        Imgproc.line(inputMat, mop.toList().get(1), mop.toList().get(2), new Scalar(255, 255, 255, 255), 3);
        Imgproc.line(inputMat, mop.toList().get(2), mop.toList().get(3), new Scalar(255, 255, 255, 255), 3);
        Imgproc.line(inputMat, mop.toList().get(3), mop.toList().get(0), new Scalar(255, 255, 255, 255), 3);
    }
}
