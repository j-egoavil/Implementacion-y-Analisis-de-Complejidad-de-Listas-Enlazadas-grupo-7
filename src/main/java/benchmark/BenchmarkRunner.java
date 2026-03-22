package benchmark;

import java.util.Arrays;
import java.util.function.LongSupplier;

public class BenchmarkRunner {

    public static BenchmarkStats run(LongSupplier measuredRun, int warmupRuns, int measuredRuns) {
        for (int i = 0; i < warmupRuns; i++) {
            measuredRun.getAsLong();
        }

        long[] samples = new long[measuredRuns];
        long sum = 0;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;

        for (int i = 0; i < measuredRuns; i++) {
            long value = measuredRun.getAsLong();
            samples[i] = value;
            sum += value;
            if (value < min) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
        }

        Arrays.sort(samples);
        long median;
        if (samples.length % 2 == 0) {
            int idx = samples.length / 2;
            median = (samples[idx - 1] + samples[idx]) / 2;
        } else {
            median = samples[samples.length / 2];
        }

        long average = sum / measuredRuns;
        return new BenchmarkStats(average, median, min, max);
    }

    public static int[] sizes(boolean include10Pow8) {
        if (include10Pow8) {
            return new int[] {10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000};
        }
        return new int[] {10, 100, 1_000, 10_000, 100_000, 1_000_000};
    }

    public static boolean include10Pow8() {
        return Boolean.parseBoolean(System.getProperty("benchmark.include10pow8", "false"));
    }

    public static int warmupRuns() {
        return Integer.parseInt(System.getProperty("benchmark.warmup", "1"));
    }

    public static int measuredRuns() {
        return Integer.parseInt(System.getProperty("benchmark.repetitions", "2"));
    }
}