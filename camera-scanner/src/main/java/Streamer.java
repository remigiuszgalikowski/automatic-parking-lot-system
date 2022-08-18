import org.opencv.core.Mat;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

import java.awt.image.BufferedImage;

public class Streamer {

    private final Timer timer;
    private final VideoStreamAdapter videoAdapter;
    private final PlateFinder plateFinder;
    private final TesseractReader tesseractReader;
    private final FramePreparer framePreparer;

    private final VideoWriter out;

    private boolean send = false;

    private Mat frame;
    private BufferedImage plate;
    private String text = "";
    private Mat preperedFrame;

    public Streamer(Timer timer, VideoStreamAdapter videoAdapter, PlateFinder plateFinder, TesseractReader tesseractReader, FramePreparer framePreparer) {
        this.timer = timer;
        this.videoAdapter = videoAdapter;
        this.plateFinder = plateFinder;
        this.tesseractReader = tesseractReader;
        this.framePreparer = framePreparer;
        this.out = new VideoWriter("appsrc ! videoconvert ! x264enc speed-preset=ultrafast bitrate=600 key-int-max=40 ! clientside location=rtsp://localhost:8554/mystream",
                Videoio.CAP_GSTREAMER,0, videoAdapter.getFramesPerSecond(), videoAdapter.getFrameSize(), true);
    }

    public void start() {
        this.send = true;
        this.checkConditions();
        while (this.send) {
            this.frame = this.videoAdapter.getFrame();
            if (!this.frame.empty() && this.frame != null) {
                this.plate = this.plateFinder.find(this.frame);
                if (this.plate != null) {
                    this.text = this.tesseractReader.read(plate);
                }
                this.preperedFrame = this.framePreparer.getPreparedFrame(frame,this.plateFinder.getPlateCoords(), text);
            }
            this.out.write(this.preperedFrame);
            this.timer.await(this.videoAdapter.getMillisecondsPerFrame());
            this.checkConditions();
            this.resetContainers();
        }
    }

    public void stop() {
        this.send = false;
    }

    private void checkConditions() {
        if (!this.videoAdapter.isVideoCaptureOpened()) {this.stop();}
    }

    private void resetContainers() {
        this.frame = null;
        this.plate = null;
        this.text = "";
        this.preperedFrame = null;
    }







//    import cv2
//import numpy as np
//    from time import sleep
//
//            fps = 20
//    width = 800
//    height = 600
//
//    out = cv2.VideoWriter('appsrc ! videoconvert ! x264enc speed-preset=ultrafast bitrate=600 key-int-max=40 ! rtspclientsink location=rtsp://localhost:8554/mystream',
//    cv2.CAP_GSTREAMER, 0, fps, (width, height), True)
//            if not out.isOpened():
//    raise Exception("can't open video writer")
//
//while True:
//    frame = np.zeros((height, width, 3), np.uint8)
//
//            # create a red rectangle
//    for y in range(0, int(frame.shape[0] / 2)):
//            for x in range(0, int(frame.shape[1] / 2)):
//    frame[y][x] = (0, 0, 255)
//
//            out.write(frame)
//    print("frame written to the server")
//
//    sleep(1 / fps)
}
