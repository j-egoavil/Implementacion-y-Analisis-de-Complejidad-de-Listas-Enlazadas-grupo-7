package benchmark;

import stack.ArrayStack;
import utils.Timer;
import utils.CSVWriter;

import java.util.Random;

public class StackBenchmark {

    private static final int WARMUP = BenchmarkRunner.warmupRuns();
    private static final int REPETITIONS = BenchmarkRunner.measuredRuns();
    private static final int[] SIZES = BenchmarkRunner.sizes(BenchmarkRunner.include10Pow8());

    public static void runAll() {
        runOperation("push");
        runOperation("pop");
        runOperation("peek");
        runOperation("delete");
    }

    public static void testPush() {
        runOperation("push");
    }

    public static void runSingle(String operationName) {
        switch (operationName) {
            case "push":
            case "pop":
            case "peek":
            case "delete":
                runOperation(operationName);
                return;
            default:
                throw new IllegalArgumentException("Unsupported stack operation: " + operationName);
        }
    }

    private static void runOperation(String operationName) {
        String csvPath = "data/data-stack/stack_" + operationName + ".csv";

        try {
            CSVWriter writer = new CSVWriter(csvPath, "size,avg_time_ns,median_ns,min_ns,max_ns");

            for (int n : SIZES) {
                BenchmarkStats stats = BenchmarkRunner.run(
                    () -> measureOperation(operationName, n),
                    WARMUP,
                    REPETITIONS
                );

                writer.writeStats(n, stats);
                System.out.println(
                    "Stack " + operationName +
                    " n=" + n + " avg=" + stats.getAverageNs() +
                    " median=" + stats.getMedianNs()
                );
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static long measureOperation(String operationName, int n) {
        return Timer.measure(() -> {
            ArrayStack<Integer> stack = new ArrayStack<>();
            Random random = new Random(17L * n + operationName.hashCode());

            switch (operationName) {
                case "push":
                    for (int i = 0; i < n; i++) {
                        stack.push(i);
                    }
                    break;
                case "pop":
                    for (int i = 0; i < n; i++) {
                        stack.push(i);
                    }
                    for (int i = 0; i < n; i++) {
                        stack.pop();
                    }
                    break;
                case "peek":
                    for (int i = 0; i < n; i++) {
                        stack.push(i);
                    }
                    int sampledOps = sampledOps(n);
                    for (int i = 0; i < sampledOps; i++) {
                        stack.peek();
                    }
                    break;
                case "delete":
                    for (int i = 0; i < n; i++) {
                        stack.push(i);
                    }
                    int deletions = Math.max(1, Math.min(1_000, n));
                    for (int i = 0; i < deletions; i++) {
                        int target = random.nextInt(n);
                        stack.delete(target);
                        stack.push(target);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported operation: " + operationName);
            }
        });
    }

    private static int sampledOps(int n) {
        return Math.max(1, Math.min(50_000, n));
    }
}