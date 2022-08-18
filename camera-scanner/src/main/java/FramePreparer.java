import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class FramePreparer {

    private final Scalar color;
    private final int offset;

    public FramePreparer(Scalar color, int offset) {
        this.color = color;
        this.offset = offset;
    }

    public Mat getPreparedFrame(Mat frame, Rect cords, String text) {
        if (cords != null) {
            Imgproc.rectangle(frame, cords, color, 4);
            if (text != null) {
                Imgproc.putText(frame,text,
                        new Point(cords.x-offset,cords.y-offset),
                        Imgproc.FONT_HERSHEY_DUPLEX, 2, color);
            }
        }
        return frame;
    }
}
