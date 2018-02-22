package socialnetwork.node;

import java.util.Optional;

public class Queue<T> {

  private Node<T> head;
  private Node<T> tail;

  public Queue() {
    this.head = null;
    this.tail = null;
  }

  public synchronized boolean addElement(T val) {
     if (head == null) {
       head = new Node<>(val, null);
       tail = head;
       return true;
     }
     Node<T> newNode = new Node<>(val, null);
     tail.setNext(newNode);
     tail = newNode;
     return true;
  }

  public synchronized Optional<T> getHeadVal() {
    if(head == null) {
      return Optional.empty();
    }
    T ret = head.getVal();
    head = head.getNext();
    if(head == null) {
      tail = null;
    }
    return Optional.of(ret);
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

  public static void main(String[] args) {
    Queue<Integer> i = new Queue<>();
    i.addElement(2);
    i.getHeadVal();
    System.out.println(i.size());
  }
}
