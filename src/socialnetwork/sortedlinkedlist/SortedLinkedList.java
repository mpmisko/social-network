package socialnetwork.sortedlinkedlist;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;


public class SortedLinkedList<T extends Comparable<T>> {

  private LockFreeNode<T> head;
  private AtomicInteger size;

  public SortedLinkedList() {
    this.head = new LockFreeNode<>();
    this.size = new AtomicInteger(0);
  }

  protected boolean addObject(T val) {
    do {
      Pair<LockFreeNode<T>> pair = findPrevNode(val, this::compareVal);
      LockFreeNode<T> prevNode = pair.getPrev();
      LockFreeNode<T> currNode = pair.getNext();
      LockFreeNode<T> newNode = new LockFreeNode<>(val, currNode);
      if (prevNode.setNextIfValid(currNode, newNode)) {
        size.incrementAndGet();
        return true;
      }

    } while (true);
  }

  protected Optional<T> getAndRemoveHead() {
    do {
      clearNodes();
      LockFreeNode<T> headNode = head.getNext();
      if (headNode == null) {
        return Optional.empty();
      }
      if (!headNode.setInvalid()) {
        continue;
      }
      size.decrementAndGet();
      return Optional.of(headNode.getVal());

    } while (true);
  }


  protected boolean deleteObject(T val) {
    do {
      Pair<LockFreeNode<T>> pair = findPrevNode(val, this::notEqual);
      LockFreeNode<T> currNode = pair.getNext();

      if (currNode == null) {
        return false;
      }

      if (!currNode.setInvalid()) {
        continue;
      }

      size.decrementAndGet();
      return true;
    } while (true);
  }

  protected List<T> getListSnapshot() {
    clearNodes();
    java.util.LinkedList<T> messages = new java.util.LinkedList<>();
    LockFreeNode<T> currNode = head.getNext();
    while ((currNode != null)) {
      messages.add(currNode.getVal());
      currNode = currNode.getNext();
    }
    return messages;
  }

  public int size() {
    return size.get();
  }

  private Pair<LockFreeNode<T>> findPrevNode(T val, BiFunction<T, T, Boolean> compareFunction) {
    clearNodes();
    LockFreeNode<T> currNode = head;
    while (currNode.getNext() != null) {
      LockFreeNode<T> nextNode = currNode.getNext();
      if (!compareFunction.apply(nextNode.getVal(), val) && nextNode.isValid()) {
        return new Pair<>(currNode, nextNode);
      }
      currNode = currNode.getNext();
    }
    return new Pair<>(currNode, null);
  }

  private void clearNodes() {
    LockFreeNode<T> fst = head;
    while (fst != null) {
      while ((fst.getNext() != null) && (fst.getNext().isValid())) {
        fst = fst.getNext();
      }
      if (fst.getNext() != null) {
        LockFreeNode<T> snd = fst.getNext();
        while ((snd != null) && !snd.isValid()) {
          snd = snd.getNext();
        }
        if (!fst.setNextIfValid(fst.getNext(), snd)) {
          clearNodes();
        }
        fst = snd;
      } else {
        break;
      }
    }
  }

  private boolean compareVal(T a, T b) {
    return (a == null) | (a.compareTo(b) > 0);
  }

  private boolean notEqual(T a, T b) {
    return !(a.compareTo(b) == 0);
  }
}
