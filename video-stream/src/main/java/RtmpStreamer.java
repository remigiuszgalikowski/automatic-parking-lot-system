public class RtmpStreamer implements Streamer {

    private final Preparer framePreparer;

    public RtmpStreamer(Preparer framePreparer) {
        this.framePreparer = framePreparer;
    }

    @Override
    public void stream() {
        //ToDo streaming via RTMP protocol
    }
}
