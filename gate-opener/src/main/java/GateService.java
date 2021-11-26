import java.util.concurrent.TimeUnit;

public class GateService {

    final int TIME_OPENED = 10;

    public GateService() {}

    public void letIn() throws InterruptedException {
        openGate();
        TimeUnit.SECONDS.sleep(TIME_OPENED);
        closeGate();
    }
    private boolean openGate() {
        return true;
    }
    private boolean closeGate() {
        return false;
    }
}
