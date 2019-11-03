package com.task;

import com.lmax.disruptor.EventTranslatorTwoArg;
import com.lmax.disruptor.RingBuffer;
import com.queue.EventQueue;

public class EventPublishThread extends Thread {

    private String eventType;

    private EventQueue eventQueue;

    private RingBuffer<Event> ringBuffer;

    private boolean running;

    public EventPublishThread(String eventType, EventQueue eventQueue, RingBuffer<Event> ringBuffer) {
        this.eventType = eventType;
        this.eventQueue = eventQueue;
        this.ringBuffer = ringBuffer;
    }

    private static final EventTranslatorTwoArg<Event, String, String> TRANSLATOR =
            new EventTranslatorTwoArg<Event, String, String>() {
                @Override
                public void translateTo(Event event, long sequence, String key, String eventType) {
                    event.setKey(key);
                    event.setEventType(eventType);
                }
            };

    @Override
    public void run() {
        while (running) {
            String nextKey = null;
            try {
                // redis中获取
                nextKey = eventQueue.next();
                if (nextKey != null) {
                    ringBuffer.publishEvent(TRANSLATOR, nextKey, eventType);
                }
            } catch (Exception e) {
                System.out.println("nextKey:" + nextKey + ", err: " + e.getMessage());
            }
        }
    }

    public void shutdown() {
        this.running = false;
    }
}
