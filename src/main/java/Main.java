import benchmark.StackBenchmark;
import benchmark.QueueBenchmark;
import benchmark.ListBenchmark;

public class Main {

    public static void main(String[] args) {

        if (args.length == 2 && "stack".equalsIgnoreCase(args[0])) {
            System.out.println("Running single stack benchmark: " + args[1]);
            StackBenchmark.runSingle(args[1]);
            System.out.println("Benchmark finished.");
            return;
        }

        if (args.length == 2 && "queue".equalsIgnoreCase(args[0])) {
            System.out.println("Running single queue benchmark: " + args[1]);
            QueueBenchmark.runSingle(args[1]);
            System.out.println("Benchmark finished.");
            return;
        }

        if (args.length == 3 && "list".equalsIgnoreCase(args[0])) {
            System.out.println("Running single list benchmark: " + args[1] + " / " + args[2]);
            ListBenchmark.runSingle(args[1], args[2]);
            System.out.println("Benchmark finished.");
            return;
        }

        System.out.println("Running benchmark suite...");

        System.out.println("Running stack benchmarks...");
        StackBenchmark.runAll();

        System.out.println("Running queue benchmarks...");
        QueueBenchmark.runAll();

        System.out.println("Running list benchmarks...");
        ListBenchmark.runAll();

        System.out.println("Benchmarks finished.");
    }
}
