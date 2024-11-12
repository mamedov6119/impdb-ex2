package buffer;

import java.util.ArrayDeque;

public class SimpleTwoQueueBuffer extends PageFaultRateBuffer {

    private final ArrayDeque<Slot> a1 = new ArrayDeque<>();
    private final ArrayDeque<Slot> am = new ArrayDeque<>();
    private int kin;

    public SimpleTwoQueueBuffer(int capacity) {
        super(capacity);
        // TODO
        this.kin = capacity / 4; // 25% of total capacity
    }

    @Override
    protected Buffer.Slot fix(char c) throws IllegalStateException {
        // TODO
        Slot slot = super.fix(c);

        if(slot != null){
            // If page is already in "am", move to the front
            if(am.contains(slot)){
                am.remove(slot);
                am.addFirst(slot);
            }
            else if (a1.contains(slot)){
                a1.remove(slot);
                am.addFirst(slot);
            }
        } else {
            // If super.fix(c) returns null, this is a page fault and 'c' was not in the buffer
            slot = lookUp(c);

            // Adding new page to "a1"
            a1.addFirst(slot);

            // If 'a1' exceeds its capacity 'kin', remove the oldest page
            if(a1.size() > kin){
                a1.removeLast();
            }
        }

        return slot;
    }

    protected Slot victim() {
        // TODO
        if (!a1.isEmpty()) {
            return a1.removeLast(); // FIFO eviction from `a1`
        } else if (!am.isEmpty()) {
            return am.removeLast(); // LRU eviction from `am`
        }
        return null; // No victim if both queues are empty
    }
}
