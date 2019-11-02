package simple.disruptor;

import com.lmax.disruptor.EventHandler;

/**
 * 事件处理器（消费者）
 */
public class LongEventHandler implements EventHandler<LongEvent> {
    @Override
    public void onEvent(LongEvent event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println("Event:" + event);
    }
}
