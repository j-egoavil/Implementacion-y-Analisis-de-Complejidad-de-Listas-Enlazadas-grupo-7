package benchmark;

import stack.ArrayStack;
import utils.Timer;
import utils.CSVWriter;

public class StackBenchmark {

    public static void testPush() {

        int[] sizes = {10, 100, 10000, 1000000};

        try {

            CSVWriter writer = new CSVWriter("data/stack_push.csv");

            for (int n : sizes) {

                ArrayStack<Integer> stack = new ArrayStack<>();

                long time = Timer.measure(() -> {

                    for (int i = 0; i < n; i++) {
                        stack.push(i);
                    }

                });

                writer.write(n, time);

                System.out.println("Stack Push n=" + n + " time=" + time);
            }

            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}