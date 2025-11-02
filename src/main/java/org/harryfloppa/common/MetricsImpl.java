package org.harryfloppa.common;

import java.util.HashMap;
import java.util.Map;


public class MetricsImpl implements Metrics {
    private long startTime;
    private long endTime;
    private final Map<String, Long> counters;
    
    public MetricsImpl() {
        this.counters = new HashMap<>();
        reset();
    }
    
    @Override
    public void startTiming() {
        startTime = System.nanoTime();
    }
    
    @Override
    public void stopTiming() {
        endTime = System.nanoTime();
    }
    
    @Override
    public long getElapsedTimeNanos() {
        return endTime - startTime;
    }
    
    @Override
    public double getElapsedTimeMillis() {
        return getElapsedTimeNanos() / 1_000_000.0;
    }
    
    @Override
    public void incrementCounter(String counterName) {
        counters.put(counterName, counters.getOrDefault(counterName, 0L) + 1);
    }
    
    @Override
    public long getCounter(String counterName) {
        return counters.getOrDefault(counterName, 0L);
    }
    
    @Override
    public void reset() {
        startTime = 0;
        endTime = 0;
        counters.clear();
    }
    
    @Override
    public String getReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Execution Time: ").append(String.format("%.3f", getElapsedTimeMillis())).append(" ms\n");
        if (!counters.isEmpty()) {
            sb.append("Operation Counters:\n");
            counters.forEach((name, value) -> 
                sb.append("  ").append(name).append(": ").append(value).append("\n"));
        }
        return sb.toString();
    }
}
