package org.example;

import java.nio.file.Files;
import java.util.concurrent.ConcurrentMap;

public class Consumer extends Thread {
    private MonitorQueue<lineRecord> queue;
    private ConcurrentMap<Integer, String> map;

    public Consumer(MonitorQueue queue, ConcurrentMap<Integer, String> map) {
        this.queue = queue;
        this.map = map;
    }

    @Override
    public void run() {
        StringBuilder result = new StringBuilder();
        StringBuilder word = new StringBuilder();

        while (true) {
            lineRecord record = queue.take();
            if (record == null) {
                break;
            }
            String item = record.line();


            result.ensureCapacity(item.length());
            word.setLength(0);

            for (int i = 0; i < item.length(); i++) {
                char c = item.charAt(i);
                if (Character.isWhitespace(c)) {
                    if (!word.isEmpty()) {
                        result.append(word.reverse());
                        word.setLength(0);
                    }
                    result.append(c);
                } else {
                    word.append(c);
                }
            }

            if (!word.isEmpty()) {
                result.append(word.reverse());
            }

            map.putIfAbsent(record.number(), result.toString());
        }
    }
}
