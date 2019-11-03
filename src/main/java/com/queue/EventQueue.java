package com.queue;

public class EventQueue {
    /**
     * redis队列实例
     */
    private QueueRedis queueRedis;

    /**
     * 任务队列失败重试次数
     */
    private Integer processingErrorRetryCount;

    /**
     * 等待队列名称
     */
    private String queueName;

    /**
     * 表示正在处理任务的队列名称
     */
    private String processingQueueName;

    /**
     * 镜像队列
     */
    private Integer maxBakSize;

    public String getQueueName() {
        return queueName;
    }

    public String next() {
        while (true) {
            String id = null;
            try {
                id = queueRedis.rightPopAndLeftPush(queueName, processingQueueName);
            } catch (Exception e) {
                continue;
            }

            if (id != null) {
                return id;
            }

        }
    }

    public void success(String id) {
        queueRedis.lRemove(processingQueueName, 0, id);
    }

    public void fail(String skuId) {

    }
}
