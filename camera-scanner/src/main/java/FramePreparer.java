import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class FramePreparer {

    private final Scalar color;
    private final int offset;

    public FramePreparer(Scalar color, int offset) {
        this.color = color;
        this.offset = offset;
    }

    public Mat getPreparedFrame(Mat frame, List<PlateReading> readings) {
        for (PlateReading r : readings) {
            Imgproc.rectangle(frame, r.location(), new Scalar(0,0,0,255), 10);
            Imgproc.rectangle(frame, r.location(), color, 4);
            Imgproc.putText(frame,r.text(),
                    new Point(r.location().x-4*offset,r.location().y-offset),
                    Imgproc.FONT_HERSHEY_DUPLEX, 2, new Scalar(0,0,0,255), 8);
            Imgproc.putText(frame,r.text(),
                    new Point(r.location().x-4*offset,r.location().y-offset),
                    Imgproc.FONT_HERSHEY_DUPLEX, 2, color, 2);
        }
        return frame;
    }
}
