package benchmark;

public class BenchmarkStats {

    private final long averageNs;
    private final long medianNs;
    private final long minNs;
    private final long maxNs;

    public BenchmarkStats(long averageNs, long medianNs, long minNs, long maxNs) {
        this.averageNs = averageNs;
        this.medianNs = medianNs;
        this.minNs = minNs;
        this.maxNs = maxNs;
    }

    public long getAverageNs() {
        return averageNs;
    }

    public long getMedianNs() {
        return medianNs;
    }

    public long getMinNs() {
        return minNs;
    }

    public long getMaxNs() {
        return maxNs;
    }
}