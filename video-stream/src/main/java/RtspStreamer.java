public class RtspStreamer implements Streamer {

    private final Preparer framePreparer;

    public RtspStreamer(Preparer framePreparer) {
        this.framePreparer = framePreparer;
    }

    @Override
    public void stream() {
        //ToDo streaming via RTSP protocol
    }
}
