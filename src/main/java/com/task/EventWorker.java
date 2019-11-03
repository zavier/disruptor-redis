package com.task;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.queue.EventQueue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventWorker {
    private Integer threadPoolSize;
    private Integer ringBufferSize;
    private Map<EventQueue, EventHandler> eventHandlerMap;
    private Map<String, EventQueue> eventQueueMap;
    private Disruptor<Event> disruptor;
    private RingBuffer<Event> ringBuffer;
    private List<EventPublishThread> eventPublishThreads;

    public void setEventHandlerMap(Map<EventQueue, EventHandler> eventHandlerMap) {
        this.eventHandlerMap = eventHandlerMap;
        if (eventHandlerMap != null && eventHandlerMap.size() > 0) {
            this.eventQueueMap = new HashMap<>();
            for (Map.Entry<EventQueue, EventHandler> entry : eventHandlerMap.entrySet()) {
                final EventQueue queue = entry.getKey();
                this.eventQueueMap.put(queue.getQueueName(), queue);
            }
        }
    }

    public void init() {
        disruptor = new Disruptor<>(
                Event::new,
                ringBufferSize,
                DaemonThreadFactory.INSTANCE,
                ProducerType.MULTI,
                new BlockingWaitStrategy());
        ringBuffer = disruptor.getRingBuffer();
        disruptor.setDefaultExceptionHandler(new ExceptionEventHandler());

        WorkHandler<Event> workHandler = new WorkHandler<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                final String type = event.getEventType();
                final EventQueue queue = eventQueueMap.get(type);
                final EventHandler handler = eventHandlerMap.get(queue);
                handler.onEvent(event.getKey(), type, queue);
            }
        };

        WorkHandler[] workHandlers = new WorkHandler[threadPoolSize];
        for (int i = 0; i < threadPoolSize; i++) {
            workHandlers[i] = workHandler;
        }
        disruptor.handleEventsWithWorkerPool(workHandlers);

        disruptor.start();

        for (Map.Entry<String, EventQueue> eventQueueEntry : eventQueueMap.entrySet()) {
            final String eventType = eventQueueEntry.getKey();
            final EventQueue eventQueue = eventQueueEntry.getValue();
            final EventPublishThread thread = new EventPublishThread(eventType, eventQueue, ringBuffer);
            eventPublishThreads.add(thread);
            thread.start();
        }
    }


    public void stop() {
        for(EventPublishThread thread : eventPublishThreads) {
            thread.shutdown();
        }
        disruptor.shutdown();
    }

    static class ExceptionEventHandler implements ExceptionHandler<Event> {

        @Override
        public void handleEventException(Throwable ex, long sequence, Event event) {
            System.out.println("has error" + ex.getMessage());
        }

        @Override
        public void handleOnStartException(Throwable ex) {
            System.out.println("start ex");
        }

        @Override
        public void handleOnShutdownException(Throwable ex) {
            System.out.println("shutdown ex");
        }
    }
}
