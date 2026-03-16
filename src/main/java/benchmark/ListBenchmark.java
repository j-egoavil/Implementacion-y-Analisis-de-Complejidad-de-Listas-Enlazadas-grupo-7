package benchmark;

import list.SinglyLinkedList;
import utils.Timer;
import utils.CSVWriter;

public class ListBenchmark {

    public static void testPushFront() {

        int[] sizes = {10, 100, 10000, 1000000};

        try {

            CSVWriter writer = new CSVWriter("data/list_pushfront.csv");

            for (int n : sizes) {

                SinglyLinkedList<Integer> list = new SinglyLinkedList<>();

                long time = Timer.measure(() -> {

                    for (int i = 0; i < n; i++) {
                        list.pushFront(i);
                    }

                });

                writer.write(n, time);

                System.out.println("List PushFront n=" + n + " time=" + time);
            }

            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}