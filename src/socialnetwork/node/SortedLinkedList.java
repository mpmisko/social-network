package socialnetwork.node;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class SortedLinkedList<T extends Comparable<T>> {

  private LockFreeNode<T> head;
  private volatile AtomicInteger size;

  public SortedLinkedList() {
    this.head = new LockFreeNode<T>();
    this.size = new AtomicInteger(0);
  }

  public boolean addObject(T val) {
    do {
      Pair<LockFreeNode<T>> pair = findPrevNode(val);
      LockFreeNode<T> prevNode = pair.getPrev();
      LockFreeNode<T> currNode = pair.getNext();
      LockFreeNode<T> newNode = new LockFreeNode<T>(val, currNode);
      if(prevNode.setNext(currNode, newNode, true)) {
        size.incrementAndGet();
        return true;
      }
      if(prevNode.setNext(currNode, newNode, false)) {
        size.incrementAndGet();
        return true;
      }
    } while (true);
  }

  public boolean deleteObject(T val) {
    do {
      Pair<LockFreeNode<T>> pair = findPrevNode(val);
      LockFreeNode<T> currNode = pair.getNext();

      if((currNode == null)) {
        return false;
      }

      if(!currNode.setInvalid()) {
        continue;
      }
      size.decrementAndGet();
      return true;
    } while (true);
  }

  public synchronized List<T> getListSnapshot() {
    java.util.LinkedList<T> messages = new java.util.LinkedList<>();
    LockFreeNode<T> currNode = head.getNext();
    while((currNode != null)) {
      messages.add(currNode.getVal());
      currNode = currNode.getNext();
    }
    return messages;
  }

  public synchronized List<LockFreeNode<T>> getNodeListSnapshot() {
    java.util.LinkedList<LockFreeNode<T>> messages = new java.util.LinkedList<>();
    LockFreeNode<T> currNode = head.getNext();
    while((currNode != null)) {
      messages.add(currNode);
      currNode = currNode.getNext();
    }
    return messages;
  }

  public int size() {
    return size.get();
  }

  public int countValid() {
    int c = 0;
    LockFreeNode<T> currNode = head.getNext();
    while(currNode != null) {
      if (currNode.isValid()) {
        c++;
      }
      currNode = currNode.getNext();
    }
    return c;
  }

  private Pair<LockFreeNode<T>> findPrevNode(T val) {
    LockFreeNode<T> currNode = head;
    while(currNode.getNext() != null) {
      LockFreeNode<T> nextNode = currNode.getNext();
      if (!compareVal(nextNode.getVal(), val) && nextNode.isValid()) {
        return new Pair<>(currNode, nextNode);
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
    for (int i = 0; i < 222; i++) {
      threads.add(new Thread(() -> {
        for(int j = 0; j < 100; j++) {
          list.addObject(j);
        }
        list.deleteObject(3);
      }));
    }

    threads.forEach(t ->
    t.start());

    threads.forEach(t -> {
      try {
        t.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });

    System.out.println(list.countValid());
    list.getNodeListSnapshot()
        .forEach(
            e -> {
              System.out.println(e.getVal().toString() + ' ' + e.isValid());
            });
  }
}
