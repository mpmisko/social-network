package socialnetwork.domain;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import socialnetwork.node.Queue;
import java.util.Optional;

public class LinkedBacklog extends Queue<Task> implements Backlog{
  private Lock lock;


  public LinkedBacklog() {
    this.lock = new ReentrantLock();
  }

  @Override
  public boolean add(Task task) {
    lock.lock();
    try{
      return super.addElement(task);
    } finally{
      lock.unlock();
    }
  }

  @Override
  public Optional<Task> getNextTaskToProcess() {
    lock.lock();
    try{
      return super.getHeadVal();
    } finally{
      lock.unlock();
    }
  }

  @Override
  public int numberOfTasksInTheBacklog() {
    lock.lock();
    try{
      return super.size();
    } finally{
      lock.unlock();
    }
  }
}
