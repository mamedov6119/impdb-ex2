package buffer;

import java.util.ArrayDeque;

public class TwoQueueBuffer extends PageFaultRateBuffer {

    private final ArrayDeque<Slot> a1in = new ArrayDeque<>();
    private final ArrayDeque<Character> a1out = new ArrayDeque<>();
    private final ArrayDeque<Slot> am = new ArrayDeque<>();
    private final int kin;
    private final int kout;

    public TwoQueueBuffer(int capacity) {
        super(capacity);
        // TODO
        this.kin = capacity / 4; // 25% of total capacity for a1in
        this.kout = capacity / 2; // 50% of total capacity for a1out
    }

    // Replacement Strategy:
    //When a page is requested (fix()):
    //If the page is in am, move it to the front of am.
    //If the page is in a1in, keep it in a1in.
    //If the page is in a1out, promote it to am.
    //If the page isn’t in any queue, add it to a1in.
    //If a1in exceeds kin, the oldest page is moved to a1out.
    //If a1out exceeds kout, the oldest page is removed from a1out.
    //The victim() method should first try to evict from a1in and then from am if a1in is empty.

    @Override
    protected Slot fix(char c) throws IllegalStateException {
        // TODO
        Slot slot = super.fix(c);

        if (slot != null) {
            // Page is already in the buffer
            if (am.contains(slot)) {
                // Page is in am, move it to the front of am
                am.remove(slot);
                am.addFirst(slot);
            } else if (a1in.contains(slot)) {
                // Page is in a1in, keep it there (correlated access)
                // No need to move it
            } else if (a1out.contains(c)) {
                // Page is in a1out, promote it to am
                a1out.remove(c);
                am.addFirst(slot);
            }
        } else {
            // Page fault occurred, page is not in any queue
            slot = super.fix(c); // Create or get slot in buffer
            a1in.addFirst(slot); // Add to a1in as a newly accessed page

            // If a1in exceeds kin, move the oldest page from a1in to a1out
            if (a1in.size() > kin) {
                Slot evicted = a1in.removeLast();
                a1out.addFirst(evicted.c);
            }

            // If a1out exceeds kout, remove the oldest entry
            if (a1out.size() > kout) {
                a1out.removeLast();
            }
        }

        return slot;
    }

    protected Slot victim() {
        // TODO
        if (!a1in.isEmpty()) {
            return a1in.removeLast(); // FIFO eviction from a1in
        } else if (!am.isEmpty()) {
            return am.removeLast(); // LRU eviction from am
        }
        return null; // No victim if both queues are empty
    }
}
