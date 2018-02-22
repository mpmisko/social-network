package socialnetwork.node;


import java.util.concurrent.atomic.AtomicMarkableReference;

public class LockFreeNode<T> {
  private T val;
  //mark of current node and reference to next
  private AtomicMarkableReference<LockFreeNode<T>> next;

  public LockFreeNode(T val, LockFreeNode<T> next) {
    this.next = new AtomicMarkableReference<>(next, true);
    this.val = val;
  }

  public LockFreeNode(LockFreeNode<T> next) {
    this.next = new AtomicMarkableReference<>(next, true);
    this.val = null;
  }

  public LockFreeNode() {
    this(null);
  }

  public T getVal() {
    return val;
  }

  public LockFreeNode<T> getNext() {
    return next.getReference();
  }

  public boolean setInvalid() {
      LockFreeNode<T> nextVal = getNext();
      return next.compareAndSet(nextVal, nextVal, true, false);
  }

  public boolean setNext(LockFreeNode<T> expected, LockFreeNode<T> newNext, boolean
      expectedValidity) {
    return next.compareAndSet(expected, newNext, expectedValidity, true);
  }

  public boolean isValid() {
    return next.isMarked();
  }

  public boolean mark(boolean newMark) {
    return next.attemptMark(getNext(), newMark);
  }

}
