package simple.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * 创建事件的工厂
 */
public class LongEventFactory implements EventFactory<LongEvent> {
    @Override
    public LongEvent newInstance() {
        return new LongEvent();
    }
}
