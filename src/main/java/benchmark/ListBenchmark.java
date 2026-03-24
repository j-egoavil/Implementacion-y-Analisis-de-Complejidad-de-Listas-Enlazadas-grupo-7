package benchmark;

import list.DoublyLinkedList;
import list.DoublyLinkedListTail;
import list.ListADT;
import list.Position;
import list.SinglyLinkedList;
import list.SinglyLinkedListTail;
import utils.CSVWriter;
import utils.Timer;

import java.util.function.Supplier;

public class ListBenchmark {

    private static final int WARMUP = BenchmarkRunner.warmupRuns();
    private static final int REPETITIONS = BenchmarkRunner.measuredRuns();
    private static final int BATCH_SIZE = BenchmarkRunner.batchSize();
    private static final int[] SIZES = BenchmarkRunner.sizes(BenchmarkRunner.include10Pow8());

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

    private static void runForImplementation(String implementationName,
                                             Supplier<ListADT<Integer>> factory,
                                             String operationName) {
        String csvPath = csvPathFor(implementationName, operationName);

        try {
            CSVWriter writer = new CSVWriter(csvPath, "size,avg_time_ns,median_ns,min_ns,max_ns");

            int[] sizes = sizesFor(implementationName, operationName);
            for (int n : sizes) {
                BenchmarkStats stats;

                if ("find".equals(operationName)) {
                    stats = benchmarkFind(factory, n);
                } else {
                    stats = benchmarkMutatingOperation(factory, operationName, n);
                }

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

    /**
     * find no modifica la estructura.
     * Reutilizamos una lista base y medimos por lotes para reducir ruido.
     */
    private static BenchmarkStats benchmarkFind(Supplier<ListADT<Integer>> factory, int n) {
        ListADT<Integer> list = buildPreloadedList(factory, n);
        int targetValue = targetValueFor(n);

        return BenchmarkRunner.runBatched(
                () -> Timer.measure(() -> list.find(targetValue)),
                WARMUP,
                REPETITIONS,
                BATCH_SIZE
        );
    }

    /**
     * Estas operaciones sí modifican la estructura.
     * Cada muestra parte de una lista nueva precargada.
     *
     * erase, add_before y add_after miden la operación con Position ya obtenida.
     * El find previo NO entra al cronómetro.
     */
    private static BenchmarkStats benchmarkMutatingOperation(Supplier<ListADT<Integer>> factory,
                                                             String operationName,
                                                             int n) {
        return BenchmarkRunner.run(
                () -> measureMutatingOperation(factory, operationName, n),
                WARMUP,
                REPETITIONS
        );
    }

    private static long measureMutatingOperation(Supplier<ListADT<Integer>> factory,
                                                 String operationName,
                                                 int n) {
        ListADT<Integer> list = buildPreloadedList(factory, n);
        int targetValue = targetValueFor(n);

        switch (operationName) {
            case "push_front":
                return Timer.measure(() -> list.pushFront(-1));

            case "push_back":
                return Timer.measure(() -> list.pushBack(-1));

            case "pop_front":
                return Timer.measure(list::popFront);

            case "pop_back":
                return Timer.measure(list::popBack);

            case "erase":
                Position<Integer> eraseTarget = list.find(targetValue);
                return Timer.measure(() -> {
                    if (eraseTarget != null) {
                        list.erase(eraseTarget);
                    }
                });

            case "add_before":
                Position<Integer> addBeforeTarget = list.find(targetValue);
                return Timer.measure(() -> {
                    if (addBeforeTarget != null) {
                        list.addBefore(addBeforeTarget, -1);
                    }
                });

            case "add_after":
                Position<Integer> addAfterTarget = list.find(targetValue);
                return Timer.measure(() -> {
                    if (addAfterTarget != null) {
                        list.addAfter(addAfterTarget, -1);
                    }
                });

            default:
                throw new IllegalArgumentException("Unsupported operation: " + operationName);
        }
    }

    private static ListADT<Integer> buildPreloadedList(Supplier<ListADT<Integer>> factory, int n) {
        ListADT<Integer> list = factory.get();
        for (int i = 0; i < n; i++) {
            list.pushFront(i);
        }
        return list;
    }

    private static int targetValueFor(int n) {
        return Math.max(0, n / 2);
    }

    private static int[] sizesFor(String implementationName, String operationName) {
        return SIZES;
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