import list.DoublyLinkedList;
import list.DoublyLinkedListTail;
import list.ListADT;
import list.Position;
import list.SinglyLinkedList;
import list.SinglyLinkedListTail;

import java.util.function.Supplier;

public class TestList {

	public static void main(String[] args) {
		runListSuite("SinglyLinkedList", SinglyLinkedList::new);
		runListSuite("SinglyLinkedListTail", SinglyLinkedListTail::new);
		runListSuite("DoublyLinkedList", DoublyLinkedList::new);
		runListSuite("DoublyLinkedListTail", DoublyLinkedListTail::new);
		System.out.println("All list tests passed.");
	}

	private static void runListSuite(String name, Supplier<ListADT<Integer>> factory) {
		testPushAndTop(name, factory);
		testPop(name, factory);
		testFindAndErase(name, factory);
		testAddBeforeAndAfter(name, factory);
		testEmptyAndSize(name, factory);
	}

	private static void testPushAndTop(String name, Supplier<ListADT<Integer>> factory) {
		ListADT<Integer> list = factory.get();
		list.pushFront(2);
		list.pushFront(1);
		list.pushBack(3);

		assertEquals(name + " topFront", 1, list.topFront());
		assertEquals(name + " topBack", 3, list.topBack());
		assertEquals(name + " size after push", 3, list.size());
	}

	private static void testPop(String name, Supplier<ListADT<Integer>> factory) {
		ListADT<Integer> list = factory.get();
		list.pushBack(10);
		list.pushBack(20);
		list.pushBack(30);

		assertEquals(name + " popFront", 10, list.popFront());
		assertEquals(name + " popBack", 30, list.popBack());
		assertEquals(name + " remaining", 20, list.topFront());
		assertEquals(name + " size after pop", 1, list.size());
	}

	private static void testFindAndErase(String name, Supplier<ListADT<Integer>> factory) {
		ListADT<Integer> list = factory.get();
		list.pushBack(5);
		list.pushBack(6);
		list.pushBack(7);

		Position<Integer> pos = list.find(6);
		assertTrue(name + " find existing", pos != null && pos.getValue() == 6);

		list.erase(pos);
		assertTrue(name + " erase removed", list.find(6) == null);
		assertEquals(name + " size after erase", 2, list.size());
	}

	private static void testAddBeforeAndAfter(String name, Supplier<ListADT<Integer>> factory) {
		ListADT<Integer> list = factory.get();
		list.pushBack(1);
		list.pushBack(3);

		Position<Integer> three = list.find(3);
		list.addBefore(three, 2);

		Position<Integer> two = list.find(2);
		list.addAfter(two, 99);

		assertTrue(name + " addBefore", list.find(2) != null);
		assertTrue(name + " addAfter", list.find(99) != null);
		assertEquals(name + " size after add", 4, list.size());
	}

	private static void testEmptyAndSize(String name, Supplier<ListADT<Integer>> factory) {
		ListADT<Integer> list = factory.get();
		assertTrue(name + " initially empty", list.isEmpty());

		list.pushFront(42);
		assertTrue(name + " non-empty", !list.isEmpty());
		assertEquals(name + " size one", 1, list.size());

		list.popFront();
		assertTrue(name + " empty after remove", list.isEmpty());
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
