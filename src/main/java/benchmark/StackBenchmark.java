package benchmark;

import stack.ArrayStack;
import utils.CSVWriter;
import utils.Timer;

public class StackBenchmark {

    private static final int WARMUP = BenchmarkRunner.warmupRuns();
    private static final int REPETITIONS = BenchmarkRunner.measuredRuns();
    private static final int BATCH_SIZE = BenchmarkRunner.batchSize();
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

            int[] sizes = sizesFor(operationName);
            for (int n : sizes) {
                BenchmarkStats stats;

                if ("peek".equals(operationName)) {
                    stats = benchmarkPeek(n);
                } else {
                    stats = benchmarkMutatingOperation(operationName, n);
                }

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

    /**
     * peek no modifica la estructura.
     * Reutilizamos el stack base y medimos por lotes para reducir ruido.
     */
    private static BenchmarkStats benchmarkPeek(int n) {
        ArrayStack<Integer> stack = buildPreloadedStack(n);

        return BenchmarkRunner.runBatched(
                () -> Timer.measure(stack::peek),
                WARMUP,
                REPETITIONS,
                BATCH_SIZE
        );
    }

    /**
     * Estas operaciones sí modifican el stack.
     * Cada muestra parte de un stack nuevo precargado.
     */
    private static BenchmarkStats benchmarkMutatingOperation(String operationName, int n) {
        return BenchmarkRunner.run(
                () -> measureMutatingOperation(operationName, n),
                WARMUP,
                REPETITIONS
        );
    }

    private static long measureMutatingOperation(String operationName, int n) {
        ArrayStack<Integer> stack = buildPreloadedStack(n);

        switch (operationName) {
            case "push":
                return Timer.measure(() -> stack.push(-1));

            case "pop":
                return Timer.measure(stack::pop);

            case "delete":
                return Timer.measure(() -> stack.delete(0));

            default:
                throw new IllegalArgumentException("Unsupported operation: " + operationName);
        }
    }

    private static ArrayStack<Integer> buildPreloadedStack(int n) {
        ArrayStack<Integer> stack = new ArrayStack<>();
        for (int i = 0; i < n; i++) {
            stack.push(i);
        }
        return stack;
    }

    private static int[] sizesFor(String operationName) {
        return SIZES;
    }
}