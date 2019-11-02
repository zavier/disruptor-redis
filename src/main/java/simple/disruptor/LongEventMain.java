package simple.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class LongEventMain {

    public static void main(String[] args) throws InterruptedException {
        final LongEventFactory longEventFactory = new LongEventFactory();
        // 需要为2的整次幂
        int bufferSize = 1024;

        // 构造 disruptor
        Disruptor<LongEvent> disruptor = new Disruptor<>(longEventFactory, bufferSize, DaemonThreadFactory.INSTANCE);
        // 连接 handler处理器（消费者）
        disruptor.handleEventsWith(new LongEventHandler());
        // 启动 disruptor
        disruptor.start();

        // 从 disruptor中获取 ring buffer, 来进行发布
        final RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        final LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(ringBuffer);
        final ByteBuffer bb = ByteBuffer.allocate(8);
        for (long l = 0; true; l++) {
            bb.putLong(0, l);
            producer.onData(bb);
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
