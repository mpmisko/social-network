package socialnetwork.node;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Node<T>{
  private T val;
  private Node<T> next;
  private Lock lock;


  public Node(T val, Node<T> next) {
    this.val = val;
    this.next = next;
    this.lock = new ReentrantLock();
  }

  public Node() {
    this(null, null);
  }

  public T getVal() {
    return val;
  }

  public Node<T> getNext() {
    return next;
  }

  public void setNext(Node<T> next) {
    this.next = next;
  }

  public void lock() {
    lock.lock();
  }

  public void unlock() {
    lock.unlock();
  }
}
