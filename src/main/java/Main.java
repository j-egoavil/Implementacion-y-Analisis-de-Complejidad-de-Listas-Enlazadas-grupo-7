import benchmark.StackBenchmark;
import benchmark.QueueBenchmark;
import benchmark.ListBenchmark;

public class Main {

    public static void main(String[] args) {

        System.out.println("Running benchmarks...");

        StackBenchmark.testPush();

        QueueBenchmark.testEnqueue();

        ListBenchmark.testPushFront();

        System.out.println("Benchmarks finished.");
    }
}
