package socialnetwork.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class Queue<T> {

  private Node<T> head;
  private Node<T> tail;
  private AtomicInteger size;

  public Queue() {
    this.head = null;
    this.tail = null;
    this.size = new AtomicInteger(0);
  }

  public synchronized boolean addElement(T val) {
     if (head == null) {
       head = new Node<>(val, null);
       tail = head;
       size.getAndIncrement();
       return true;
     }
     Node<T> newNode = new Node<>(val, null);
     tail.setNext(newNode);
     tail = newNode;
     size.getAndIncrement();
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
    size.getAndDecrement();
    return Optional.of(ret);
  }

  public synchronized int size() {
    return size.get();
  }

}
