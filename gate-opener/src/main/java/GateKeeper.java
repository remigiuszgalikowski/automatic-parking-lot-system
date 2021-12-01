public interface GateKeeper {
    default void letGo() {
        open();
        waitUntilItPasses();
        close();
    };
    private void open() {};
    private void close() {};
    private void waitUntilItPasses() {};
}
