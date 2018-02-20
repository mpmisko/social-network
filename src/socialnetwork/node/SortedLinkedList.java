package socialnetwork.node;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


public class SortedLinkedList<T extends Comparable<T>> {

  private Node<T> head;

  public SortedLinkedList() {
    this.head = new Node<>();
  }

  public boolean addObject(T val) {
    Node<T> newNode = new Node<>(val, null);
    Node<T> prevNode = new Node<>();
    Node<T> nextNode = new Node<>();
    try {
      prevNode = findPrevNode(val);
      nextNode = prevNode.getNext();
      newNode.setNext(nextNode);
      prevNode.setNext(newNode);
    } finally{
      prevNode.unlock();
      if(nextNode != null) {
        nextNode.unlock();
      }
    }
    return true;
  }

  public boolean deleteObject(T val) {
    Node<T> prevNode = new Node<>();
    try {
      prevNode = findPrevNode(val);
      if ((prevNode.getNext() == null) | !equal(prevNode.getNext().getVal(), val)) {
        return false;
      }
      prevNode.setNext(prevNode.getNext().getNext());
    }
    finally {
      prevNode.unlock();
    }
    return true;
  }

  public synchronized List<T> getListSnapshot() {
    java.util.LinkedList<T> messages = new java.util.LinkedList<>();
    Node<T> currNode = head.getNext();
    while (currNode != null) {
      messages.addLast(currNode.getVal());
      currNode = currNode.getNext();
    }
    return messages;
  }

  public synchronized int size() {
    int count = 0;
    Node<T> currNode = head.getNext();
    while (currNode != null) {
      count++;
      currNode = currNode.getNext();
    }
    return count;
  }

  private Node<T> findPrevNode(T val) {
    Node<T> currNode = head;
    currNode.lock();
    Node<T> nextNode = currNode.getNext();
    while ((nextNode != null)) {
      nextNode.lock();
      if (!compareVal(nextNode.getVal(), val)) {
        return currNode;
      }
      currNode.unlock();
      currNode = currNode.getNext();
      nextNode = currNode.getNext();
    }
    return currNode;
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
