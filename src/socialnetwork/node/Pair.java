package socialnetwork.node;

public class Pair<T> {
  private final T prev;
  private final T next;

  public Pair(T prev, T next) {
    this.prev = prev;
    this.next = next;
  }

  public T getPrev() {
    return prev;
  }

  public T getNext() {
    return next;
  }
}
