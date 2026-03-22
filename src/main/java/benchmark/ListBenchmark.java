package benchmark;

import list.DoublyLinkedList;
import list.DoublyLinkedListTail;
import list.ListADT;
import list.Position;
import list.SinglyLinkedList;
import list.SinglyLinkedListTail;
import utils.Timer;
import utils.CSVWriter;

import java.util.Random;
import java.util.function.Supplier;

public class ListBenchmark {

    private static final int WARMUP = BenchmarkRunner.warmupRuns();
    private static final int REPETITIONS = BenchmarkRunner.measuredRuns();
    private static final int[] BASE_SIZES = BenchmarkRunner.sizes(BenchmarkRunner.include10Pow8());
    private static final int[] EXPENSIVE_SIZES = {10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000};
    private static final int[] VERY_EXPENSIVE_SIZES = {10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000};

    public static void runAll() {
        runForImplementation("singly", SinglyLinkedList::new);
        runForImplementation("singly_tail", SinglyLinkedListTail::new);
        runForImplementation("doubly", DoublyLinkedList::new);
        runForImplementation("doubly_tail", DoublyLinkedListTail::new);
    }

    public static void testPushFront() {
        runForImplementation("singly", SinglyLinkedList::new, "push_front");
    }

    public static void runSingle(String implementationName, String operationName) {
        switch (implementationName) {
            case "singly":
                runForImplementation("singly", SinglyLinkedList::new, operationName);
                return;
            case "singly_tail":
                runForImplementation("singly_tail", SinglyLinkedListTail::new, operationName);
                return;
            case "doubly":
                runForImplementation("doubly", DoublyLinkedList::new, operationName);
                return;
            case "doubly_tail":
                runForImplementation("doubly_tail", DoublyLinkedListTail::new, operationName);
                return;
            default:
                throw new IllegalArgumentException("Unsupported list implementation: " + implementationName);
        }
    }

    private static void runForImplementation(String implementationName, Supplier<ListADT<Integer>> factory) {
        runForImplementation(implementationName, factory, "push_front");
        runForImplementation(implementationName, factory, "push_back");
        runForImplementation(implementationName, factory, "pop_front");
        runForImplementation(implementationName, factory, "pop_back");
        runForImplementation(implementationName, factory, "find");
        runForImplementation(implementationName, factory, "erase");
        runForImplementation(implementationName, factory, "add_before");
        runForImplementation(implementationName, factory, "add_after");
    }

    private static void runForImplementation(String implementationName, Supplier<ListADT<Integer>> factory, String operationName) {
        String csvPath = csvPathFor(implementationName, operationName);

        try {
            CSVWriter writer = new CSVWriter(csvPath, "size,avg_time_ns,median_ns,min_ns,max_ns");

            int[] sizes = sizesFor(implementationName, operationName);
            for (int n : sizes) {
                BenchmarkStats stats = BenchmarkRunner.run(
                    () -> measureOperation(factory, operationName, n),
                    WARMUP,
                    REPETITIONS
                );

                writer.writeStats(n, stats);
                System.out.println(
                    "List " + implementationName + " " + operationName +
                    " n=" + n + " avg=" + stats.getAverageNs() +
                    " median=" + stats.getMedianNs()
                );
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static long measureOperation(Supplier<ListADT<Integer>> factory, String operationName, int n) {
        ListADT<Integer> list = factory.get();
        Random random = new Random(31L * n + operationName.hashCode());
        int sampledOps = sampledOps(n);

        switch (operationName) {
            case "push_front":
                return Timer.measure(() -> {
                    for (int i = 0; i < n; i++) {
                        list.pushFront(i);
                    }
                });
            case "push_back":
                return Timer.measure(() -> {
                    for (int i = 0; i < n; i++) {
                        list.pushBack(i);
                    }
                });
            case "pop_front":
                preloadForReadWriteOps(list, n);
                return Timer.measure(() -> {
                    for (int i = 0; i < n; i++) {
                        list.popFront();
                    }
                });
            case "pop_back":
                preloadForReadWriteOps(list, n);
                return Timer.measure(() -> {
                    for (int i = 0; i < n; i++) {
                        list.popBack();
                    }
                });
            case "find":
                preloadForReadWriteOps(list, n);
                return Timer.measure(() -> {
                    for (int i = 0; i < sampledOps; i++) {
                        list.find(random.nextInt(n));
                    }
                });
            case "erase":
                preloadForReadWriteOps(list, n);
                return Timer.measure(() -> {
                    for (int i = 0; i < sampledOps; i++) {
                        int value = random.nextInt(n);
                        Position<Integer> target = list.find(value);
                        if (target != null) {
                            list.erase(target);
                            list.pushFront(value);
                        }
                    }
                });
            case "add_before":
                preloadForReadWriteOps(list, n);
                return Timer.measure(() -> {
                    for (int i = 0; i < sampledOps; i++) {
                        int value = random.nextInt(n);
                        Position<Integer> target = list.find(value);
                        if (target != null) {
                            list.addBefore(target, -1);
                            list.popFront();
                        }
                    }
                });
            case "add_after":
                preloadForReadWriteOps(list, n);
                return Timer.measure(() -> {
                    for (int i = 0; i < sampledOps; i++) {
                        int value = random.nextInt(n);
                        Position<Integer> target = list.find(value);
                        if (target != null) {
                            list.addAfter(target, -1);
                            list.popFront();
                        }
                    }
                });
            default:
                throw new IllegalArgumentException("Unsupported operation: " + operationName);
        }
    }

    private static void preloadForReadWriteOps(ListADT<Integer> list, int n) {
        for (int i = 0; i < n; i++) {
            list.pushFront(i);
        }
    }

    private static int sampledOps(int n) {
        return Math.max(1, Math.min(5_000, n));
    }

    private static int[] sizesFor(String implementationName, String operationName) {
        if (
            ("singly".equals(implementationName) || "doubly".equals(implementationName)) &&
            "push_back".equals(operationName)
        ) {
            return VERY_EXPENSIVE_SIZES;
        }

        if (
            "pop_back".equals(operationName) ||
            "find".equals(operationName) ||
            "erase".equals(operationName) ||
            "add_before".equals(operationName) ||
            "add_after".equals(operationName)
        ) {
            return EXPENSIVE_SIZES;
        }
        return BASE_SIZES;
    }

    private static String csvPathFor(String implementationName, String operationName) {
        if ("singly".equals(implementationName)) {
            return "data/data-list/list-singly/no-tail/list_singly_" + operationName + ".csv";
        }
        if ("singly_tail".equals(implementationName)) {
            return "data/data-list/list-singly/whit-tail/list_singly_tail_" + operationName + ".csv";
        }
        if ("doubly".equals(implementationName)) {
            return "data/data-list/list-doubly/no-tail/list_doubly_" + operationName + ".csv";
        }
        if ("doubly_tail".equals(implementationName)) {
            return "data/data-list/list-doubly/whit-tail/list_doubly_tail_" + operationName + ".csv";
        }
        throw new IllegalArgumentException("Unsupported list implementation: " + implementationName);
    }
}
