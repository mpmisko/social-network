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
      if(prevNode.setNextIfValid(currNode, newNode)) {
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

  public List<T> getListSnapshot() {
    clearNodes(head);
    java.util.LinkedList<T> messages = new java.util.LinkedList<>();
    LockFreeNode<T> currNode = head.getNext();
    while((currNode != null)) {
      messages.add(currNode.getVal());
      currNode = currNode.getNext();
    }
    return messages;
  }

  public List<LockFreeNode<T>> getNodeListSnapshot() {
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
    clearNodes(head);
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

  private void clearNodes(LockFreeNode<T> start) {
    LockFreeNode<T> fst = start;
    while (fst != null) {
      while ((fst.getNext() != null) && (fst.getNext().isValid())) {
        fst = fst.getNext();
      }
      if (fst.getNext() != null) {
        LockFreeNode<T> snd = fst.getNext();
        while((snd != null) && !snd.isValid()) {
          snd = snd.getNext();
        }
        if (!fst.setNextIfValid(fst.getNext(), snd)) {
          clearNodes(start);
        }
        fst = snd;
      } else {
        fst = null;
      }
    }
  }

  private boolean compareVal(T a, T b) {
    return (a == null) | (a.compareTo(b) > 0);
  }
}
