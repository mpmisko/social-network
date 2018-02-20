package socialnetwork.domain;

import socialnetwork.node.Queue;
import java.util.Optional;

public class LinkedBacklog extends Queue<Task> implements Backlog{

  @Override
  public boolean add(Task task) {
    return super.addElement(task);
  }

  @Override
  public Optional<Task> getNextTaskToProcess() {
    return super.getHeadVal();
  }

  @Override
  public int numberOfTasksInTheBacklog() {
    return super.size();
  }
}
