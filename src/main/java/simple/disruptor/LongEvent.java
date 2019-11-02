package simple.disruptor;

/**
 * 要处理的事件（队列中的元素）
 */
public class LongEvent {
    private long value;

    public void set(long value) {
        this.value = value;
    }
}
