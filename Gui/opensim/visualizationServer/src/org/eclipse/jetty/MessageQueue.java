/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.eclipse.jetty;

/**
 *
 * @author ayman
 */
import org.json.simple.JSONObject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A fixed-capacity, thread-safe circular queue (ring buffer) of JSON
 * messages. When the queue is full, offering a new message evicts the
 * oldest one rather than blocking or rejecting -- appropriate for a
 * "latest N messages" stream (e.g. visualizer Frame messages) where losing
 * stale entries is preferable to applying backpressure to the producer.
 *
 *
 * @author Ayman-NMBL
 */
public class MessageQueue {

    private final int capacity;
    private final Deque<JSONObject> buffer;
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull = lock.newCondition();

    // Simple diagnostics: how many messages have been dropped because the
    // queue was full when offer() (non-blocking) was called.
    private long droppedCount = 0;

    public MessageQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be > 0, got " + capacity);
        }
        this.capacity = capacity;
        this.buffer = new ArrayDeque<>(capacity);
    }

    /**
     * Adds a message to the tail of the queue. If the queue is already at
     * capacity, evicts the oldest (head) message to make room -- never
     * blocks, never rejects.
     */
    public void offer(JSONObject message) {
        lock.lock();
        try {
            if (buffer.size() == capacity) {
                buffer.removeFirst();
                droppedCount++;
            }
            buffer.addLast(message);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Adds a message to the tail of the queue, blocking the caller until
     * space is available instead of dropping the oldest message. Use this
     * variant if losing a message is never acceptable.
     */
    public void offerBlocking(JSONObject message) throws InterruptedException {
        lock.lock();
        try {
            while (buffer.size() == capacity) {
                notFull.await();
            }
            buffer.addLast(message);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    /** Removes and returns the oldest message, or null if the queue is empty. */
    public JSONObject poll() {
        lock.lock();
        try {
            JSONObject message = buffer.pollFirst();
            if (message != null) {
                notFull.signal();
            }
            return message;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes and returns the oldest message, blocking until one is
     * available if the queue is currently empty.
     */
    public JSONObject take() throws InterruptedException {
        lock.lock();
        try {
            while (buffer.isEmpty()) {
                notEmpty.await();
            }
            JSONObject message = buffer.pollFirst();
            notFull.signal();
            return message;
        } finally {
            lock.unlock();
        }
    }

    /** Returns the oldest message without removing it, or null if empty. */
    public JSONObject peek() {
        lock.lock();
        try {
            return buffer.peekFirst();
        } finally {
            lock.unlock();
        }
    }

    /** Removes and returns every currently queued message, oldest first. */
    public List<JSONObject> drainAll() {
        lock.lock();
        try {
            List<JSONObject> drained = new ArrayList<>(buffer);
            buffer.clear();
            notFull.signalAll();
            return drained;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return buffer.size();
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return buffer.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    public boolean isFull() {
        lock.lock();
        try {
            return buffer.size() == capacity;
        } finally {
            lock.unlock();
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public long getDroppedCount() {
        lock.lock();
        try {
            return droppedCount;
        } finally {
            lock.unlock();
        }
    }

    public void clear() {
        lock.lock();
        try {
            buffer.clear();
            notFull.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
