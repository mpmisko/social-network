package socialnetwork.domain;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import socialnetwork.node.SortedLinkedList;


public class LinkedBoard extends SortedLinkedList<Message> implements Board{
  private Lock lock;


  public LinkedBoard() {
    this.lock = new ReentrantLock();
  }

  @Override
  public boolean addMessage(Message message) {
    lock.lock();
    try {
      return super.addObject(message);
    } finally{
      lock.unlock();
    }
  }

  @Override
  public boolean deleteMessage(Message message) {
    lock.lock();
    try {
      return super.deleteObject(message);
    } finally{
      lock.unlock();
    }

  }

  @Override
  public int size() {
    lock.lock();
    try {
      return super.size();
    } finally{
      lock.unlock();
    }
  }

  @Override
  public List<Message> getBoardSnapshot() {
    lock.lock();
    try {
      return super.getListSnapshot();
    } finally{
      lock.unlock();
    }
  }
}



