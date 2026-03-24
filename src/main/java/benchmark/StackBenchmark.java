package benchmark;

import stack.ArrayStack;
import utils.Timer;
import utils.CSVWriter;

public class StackBenchmark {

    private static final int WARMUP = BenchmarkRunner.warmupRuns();
    private static final int REPETITIONS = BenchmarkRunner.measuredRuns();
    private static final int[] SIZES = {10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000};

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
                ArrayStack<Integer> stack = createPreloadedStack(n);
                BenchmarkStats stats = BenchmarkRunner.run(
                    () -> measureOperationInPlace(stack, operationName),
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

    private static ArrayStack<Integer> createPreloadedStack(int n) {
        ArrayStack<Integer> stack = new ArrayStack<>();
        for (int i = 0; i < n; i++) {
            stack.push(i);
        }
        return stack;
    }

    private static long measureOperationInPlace(ArrayStack<Integer> stack, String operationName) {

        switch (operationName) {
            case "push": {
                long elapsed = Timer.measure(() -> stack.push(-1));
                stack.pop();
                return elapsed;
            }
            case "pop": {
                final int[] removed = new int[1];
                long elapsed = Timer.measure(() -> removed[0] = stack.pop());
                stack.push(removed[0]);
                return elapsed;
            }
            case "peek": {
                return Timer.measure(stack::peek);
            }
            case "delete": {
                long elapsed = Timer.measure(() -> stack.delete(0));
                stack.push(0);
                return elapsed;
            }
            default:
                throw new IllegalArgumentException("Unsupported operation: " + operationName);
        }
    }

    private static int[] sizesFor(String operationName) {
        return SIZES;
    }
}