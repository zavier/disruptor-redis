package com.task;

import com.queue.EventQueue;

public class EventHandler {

    public void onEvent(String skuId, String eventType, EventQueue queue) {
        try {
            queue.success(skuId);
        } catch (Exception e) {
            queue.fail(skuId);
        }
    }
}
