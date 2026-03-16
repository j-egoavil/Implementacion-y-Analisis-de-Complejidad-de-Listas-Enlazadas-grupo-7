package benchmark;

import queue.CircularArrayQueue;
import utils.Timer;
import utils.CSVWriter;

public class QueueBenchmark {

    public static void testEnqueue() {

        int[] sizes = {10, 100, 10000, 1000000};

        try {

            CSVWriter writer = new CSVWriter("data/queue_enqueue.csv");

            for (int n : sizes) {

                CircularArrayQueue<Integer> queue = new CircularArrayQueue<>();

                long time = Timer.measure(() -> {

                    for (int i = 0; i < n; i++) {
                        queue.enqueue(i);
                    }

                });

                writer.write(n, time);

                System.out.println("Queue Enqueue n=" + n + " time=" + time);
            }

            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}