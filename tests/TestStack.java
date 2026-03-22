import stack.ArrayStack;

public class TestStack {

	public static void main(String[] args) {
		testPushPopPeek();
		testDeleteFirstMatchFromTopTraversal();
		testIsEmptyAndSize();
		System.out.println("All stack tests passed.");
	}

	private static void testPushPopPeek() {
		ArrayStack<Integer> stack = new ArrayStack<>();
		stack.push(1);
		stack.push(2);
		stack.push(3);

		assertEquals("peek", 3, stack.peek());
		assertEquals("pop 1", 3, stack.pop());
		assertEquals("pop 2", 2, stack.pop());
		assertEquals("pop 3", 1, stack.pop());
	}

	private static void testDeleteFirstMatchFromTopTraversal() {
		ArrayStack<Integer> stack = new ArrayStack<>();
		stack.push(10);
		stack.push(20);
		stack.push(30);
		stack.push(20);

		stack.delete(20);

		assertEquals("delete removes top-most matching value", 30, stack.pop());
		assertEquals("next value after delete", 20, stack.pop());
		assertEquals("bottom value", 10, stack.pop());
	}

	private static void testIsEmptyAndSize() {
		ArrayStack<Integer> stack = new ArrayStack<>();
		assertTrue("initial empty", stack.isEmpty());
		stack.push(7);
		stack.push(8);
		assertEquals("size after push", 2, stack.size());
		stack.pop();
		stack.pop();
		assertTrue("empty after pops", stack.isEmpty());
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
