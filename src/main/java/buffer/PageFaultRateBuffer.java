package buffer;

public abstract class PageFaultRateBuffer extends Buffer {
    private int fsCount = 0;
    private int sCount = 0;

    public PageFaultRateBuffer(int capacity) {
        super(capacity);
    }

    public double getFSR() {
        // TODO
        if (sCount == 0) return 0.0; // Avoid division by zero
        return (double) fsCount / sCount;
    }

    @Override
    protected Slot fix(char c) throws IllegalStateException {
        // TODO
        sCount++; // Increment total page access count

        Slot slot = lookUp(c); // Check if the page is in the buffer
        if (slot == null){ // Page fault occurs
            fsCount++; // Increment page fault count
        }

        // Calling the base class fix() to handle adding or referencing the page
        return super.fix(c);
    }
}
