package socialnetwork.node;

import java.util.List;

public class SortedLinkedList<T extends Comparable<T>>{

  private Node<T> head;

  public SortedLinkedList() {
    this.head = null;
  }

  public synchronized boolean addObject(T val) {
    if (head == null) {
      head = new Node<>(val, null);
      return true;
    }
    if (!compareVal(head.getVal(), val)) {
      head = new Node<>(val, head);

      return true;
    }
    Node<T> prevNode = findPrevNode(val);
    prevNode.setNext(new Node<>(val, prevNode.getNext()));
    return true;
  }

  public synchronized boolean deleteObject(T val) {
    if (head == null) {
      return false;
    }
    if(equal(head.getVal(), val)) {
      head = head.getNext();
      return true;
    }
    Node<T> prevNode = findPrevNode(val);
    if (!equal(prevNode.getNext().getVal(), val)) {
      return false;
    }
    prevNode.setNext(prevNode.getNext().getNext());
    return true;
  }


  public synchronized List<T> getListSnapshot() {
    java.util.LinkedList<T> messages = new java.util.LinkedList<>();
    Node<T> currNode = head;
    while (currNode != null) {
      messages.addLast(currNode.getVal());
      currNode = currNode.getNext();
    }
    return messages;
  }

  private synchronized Node<T> findPrevNode(T val) {
    Node<T> currNode = head;
    while ((currNode.getNext() != null)) {
      if (!compareVal(currNode.getNext().getVal(), val)) {
        return currNode;
      }
      currNode = currNode.getNext();
    }
    return currNode;
  }

  public synchronized int size() {
    int count = 0;
    Node<T> currNode = head;
    while (currNode != null) {
      count++;
      currNode = currNode.getNext();
    }
    return count;
  }

  private boolean compareVal(T a, T b) {
    return a.compareTo(b) > 0;
  }

  private boolean equal(T a, T b) {
    return a.compareTo(b) == 0;
  }
}
