package socialnetwork.node;

public class Node<T>{
  private T val;
  private Node<T> next;

  public Node(T val, Node<T> next) {
    this.val = val;
    this.next = next;
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
}