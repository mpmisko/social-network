package socialnetwork.node;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class SortedLinkedList<T extends Comparable<T>> {

  private LockFreeNode<T> head;
  private AtomicInteger size;

  public SortedLinkedList() {
    this.head = new LockFreeNode<T>();
    this.size = new AtomicInteger(0);
  }

  public boolean addObject(T val) {
    do {
      System.out.println("adding");
      Pair<LockFreeNode<T>> pair = findPrevNode(val);
      LockFreeNode<T> prevNode = pair.getPrev();
      LockFreeNode<T> currNode = pair.getNext();
      LockFreeNode<T> newNode = new LockFreeNode<T>(val, currNode);
      if(prevNode.setNextIfValid(currNode, newNode)) {
        size.incrementAndGet();
        return true;
      }
    } while (true);
  }

  public boolean deleteObject(T val) {
    do {
      Pair<LockFreeNode<T>> pair = findPrevNode(val);
      LockFreeNode<T> prevNode = pair.getPrev();
      LockFreeNode<T> currNode = pair.getNext();
      if(currNode == null) {
        return false;
      }
      if(!prevNode.setInvalid(currNode)) {
        continue;
      }

      //prevNode.setNextIfValid(currNode, currNode.getNext());
      size.decrementAndGet();
      return true;

    } while (true);
  }

  public synchronized List<T> getListSnapshot() {
    java.util.LinkedList<T> messages = new java.util.LinkedList<>();
    LockFreeNode<T> currNode = head.getNext();
    while(currNode != null) {
      messages.add(currNode.getVal());
    }
    return messages;
  }

  public int size() {
    return size.get();
  }

  private Pair<LockFreeNode<T>> findPrevNode(T val) {
    LockFreeNode<T> currNode = head;
    while(currNode.getNext() != null) {
      LockFreeNode<T> nextNode = currNode.getNext();
      if (!compareVal(nextNode.getVal(), val)) {
        return new Pair<>(currNode, currNode.getNext());
      }
      currNode = currNode.getNext();
    }
    return new Pair<>(currNode, currNode.getNext());
  }

  private boolean compareVal(T a, T b) {
    return (a == null) | (a.compareTo(b) > 0);
  }

  private boolean equal(T a, T b) {
    return a.compareTo(b) == 0;
  }

  public static void main(String[] args) {
    SortedLinkedList<Integer> list = new SortedLinkedList<>();
    List<Thread> threads = new LinkedList<>();
    for (int i = 0; i < 1000; i++) {
      threads.add(new Thread(() -> {
        list.addObject(7);
        list.addObject(6);
        list.addObject(5);
        list.addObject(4);
        list.deleteObject(6);
        list.deleteObject(4);
        list.deleteObject(7);
      }));
    }

    threads.forEach(t ->
    t.run());

    threads.forEach(t -> {
      try {
        t.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });

    System.out.println(list.size());
  }
}
