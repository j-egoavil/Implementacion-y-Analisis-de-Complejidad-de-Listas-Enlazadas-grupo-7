package benchmark;

import tree.AvlTree;
import tree.BstTree;
import utils.Timer;

import java.io.FileWriter;
import java.io.IOException;

public class TreeBenchmark {

    private static final int[] SIZES = {10, 100, 1000, 10000};

    public static void runAll() {
        System.out.println("Running tree benchmarks...");

        try {
            FileWriter writer = new FileWriter("data/tree_results.csv");
            writer.write("Structure,Operation,Size,Time(ns)\n");

            for (int n : SIZES) {
                runBenchmark(n, writer);
            }

            writer.close();
            System.out.println("Tree benchmark finished. Results saved in data/tree_results.csv");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runBenchmark(int n, FileWriter writer) throws IOException {

        AvlTree<Integer> avl = new AvlTree<>();
        BstTree<Integer> bst = new BstTree<>();

        int[] data = generateData(n);

        Timer timer = new Timer();

        timer.start();
        for (int x : data) {
            avl.insert(x);
        }
        long avlInsertTime = timer.stop();

        timer.start();
        for (int x : data) {
            bst.insert(x);
        }
        long bstInsertTime = timer.stop();

        writer.write("AVL,Insert," + n + "," + avlInsertTime + "\n");
        writer.write("BST,Insert," + n + "," + bstInsertTime + "\n");

        timer.start();
        for (int x : data) {
            avl.contains(x);
        }
        long avlSearchTime = timer.stop();

        timer.start();
        for (int x : data) {
            bst.contains(x);
        }
        long bstSearchTime = timer.stop();

        writer.write("AVL,Search," + n + "," + avlSearchTime + "\n");
        writer.write("BST,Search," + n + "," + bstSearchTime + "\n");

        timer.start();
        for (int x : data) {
            avl.remove(x);
        }
        long avlRemoveTime = timer.stop();

        timer.start();
        for (int x : data) {
            bst.remove(x);
        }
        long bstRemoveTime = timer.stop();

        writer.write("AVL,Remove," + n + "," + avlRemoveTime + "\n");
        writer.write("BST,Remove," + n + "," + bstRemoveTime + "\n");
    }

    private static int[] generateData(int n) {
        int[] data = new int[n];
        for (int i = 0; i < n; i++) {
            data[i] = i;
        }
        return data;
    }
}
