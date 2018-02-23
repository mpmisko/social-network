package socialnetwork.node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;


public class SortedLinkedList<T extends Comparable<T>> {

  private volatile LockFreeNode<T> head;
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
  public Optional<T> getAndRemoveHead() {
    do {
      clearNodes();
      LockFreeNode<T> headNode = head.getNext();
      if(headNode == null) {
        return Optional.empty();
      }
      if(!headNode.setInvalid()) {
        continue;
      }
      size.decrementAndGet();
      return Optional.of(headNode.getVal());

    } while (true);
  }


  public boolean deleteObject(T val) {
    do {
      Pair<LockFreeNode<T>> pair = findPrevNodeEqual(val);
      LockFreeNode<T> currNode = pair.getNext();

      if((currNode == null) || (!equal(val, currNode.getVal()))) {
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
    java.util.LinkedList<T> messages = new java.util.LinkedList<>();
    LockFreeNode<T> currNode = head.getNext();
    while((currNode != null)) {
      messages.add(currNode.getVal());
      currNode = currNode.getNext();
    }
    return messages;
  }

  public List<LockFreeNode<T>> getListNodeSnapshot() {
    java.util.LinkedList<LockFreeNode<T>> messages = new java.util.LinkedList<>();
    LockFreeNode<T> currNode = head.getNext();
    while((currNode != null)) {
      messages.add(currNode);
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
    clearNodes();
    LockFreeNode<T> currNode = head;
    while(currNode.getNext() != null) {
      LockFreeNode<T> nextNode = currNode.getNext();
      if (!compareVal(nextNode.getVal(), val) && nextNode.isValid()) {
        return new Pair<>(currNode, nextNode);
      }
      currNode = currNode.getNext();
    }
    return new Pair<>(currNode, null);
  }

  private Pair<LockFreeNode<T>> findPrevNodeEqual(T val) {
    clearNodes();
    LockFreeNode<T> currNode = head;
    while(currNode.getNext() != null) {
      LockFreeNode<T> nextNode = currNode.getNext();
      if (equal(nextNode.getVal(), val) && nextNode.isValid()) {
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
        while((snd != null) && !snd.isValid()) {
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
  private boolean equal(T a, T b) {
    return a.compareTo(b) == 0;
  }
}
