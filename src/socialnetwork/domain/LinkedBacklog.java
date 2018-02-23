package socialnetwork.domain;

import socialnetwork.sortedlinkedlist.SortedLinkedList;

import java.util.Optional;

public class LinkedBacklog extends SortedLinkedList<Task> implements Backlog {

  @Override
  public boolean add(Task task) {
    return super.addObject(task);
  }

  @Override
  public Optional<Task> getNextTaskToProcess() {
    return super.getAndRemoveHead();
  }

  @Override
  public int numberOfTasksInTheBacklog() {
    return super.size();
  }
}
