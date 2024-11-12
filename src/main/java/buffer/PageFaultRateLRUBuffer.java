package buffer;

public class PageFaultRateLRUBuffer extends LRUBuffer {
    private int fsCount = 0;
    private int sCount = 0;

    public PageFaultRateLRUBuffer(int capacity) {
        super(capacity);
    }

    public double getFSR() {
        // TODO
        if (sCount == 0) return 0.0;
        return (double) fsCount / sCount;
    }

    @Override
    protected Buffer.Slot fix(char c) throws IllegalStateException {
        // TODO
        sCount++; // Increment total page access count

        // Check if page is in the buffer
        Buffer.Slot slot = lookUp(c);
        if (slot == null) {
            fsCount++;
        }
        
        return super.fix(c);
    }
}
