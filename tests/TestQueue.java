import queue.CircularArrayQueue;

public class TestQueue {

	public static void main(String[] args) {
		testEnqueueDequeueFront();
		testDeleteFirstOccurrenceOnly();
		testIsEmptyAndSize();
		System.out.println("All queue tests passed.");
	}

	private static void testEnqueueDequeueFront() {
		CircularArrayQueue<Integer> queue = new CircularArrayQueue<>();
		queue.enqueue(1);
		queue.enqueue(2);
		queue.enqueue(3);

		assertEquals("front", 1, queue.front());
		assertEquals("dequeue 1", 1, queue.dequeue());
		assertEquals("dequeue 2", 2, queue.dequeue());
		assertEquals("dequeue 3", 3, queue.dequeue());
	}

	private static void testDeleteFirstOccurrenceOnly() {
		CircularArrayQueue<Integer> queue = new CircularArrayQueue<>();
		queue.enqueue(5);
		queue.enqueue(9);
		queue.enqueue(9);
		queue.enqueue(7);

		queue.delete(9);

		assertEquals("first kept", 5, queue.dequeue());
		assertEquals("only first 9 removed", 9, queue.dequeue());
		assertEquals("last value", 7, queue.dequeue());
	}

	private static void testIsEmptyAndSize() {
		CircularArrayQueue<Integer> queue = new CircularArrayQueue<>();
		assertTrue("initial empty", queue.isEmpty());

		queue.enqueue(11);
		queue.enqueue(12);
		assertEquals("size after enqueue", 2, queue.size());

		queue.dequeue();
		queue.dequeue();
		assertTrue("empty after dequeue", queue.isEmpty());
	}

	private static void assertEquals(String label, int expected, int actual) {
		if (expected != actual) {
			throw new AssertionError(label + " expected=" + expected + " actual=" + actual);
		}
	}

	private static void assertTrue(String label, boolean condition) {
		if (!condition) {
			throw new AssertionError(label);
		}
	}
}
