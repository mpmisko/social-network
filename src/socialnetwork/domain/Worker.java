package socialnetwork.domain;

import java.util.Optional;

import socialnetwork.domain.Task.Command;

public class Worker extends Thread {

  private final Backlog backlog;
  private boolean interrupted = false;
  private final int WORKER_SLEEP_LEN = 200;

  public Worker(Backlog backlog) {
    this.backlog = backlog;
  }

  @Override
  public void run() {
    while (!interrupted) {
      Optional<Task> t = backlog.getNextTaskToProcess();
      if (t.isPresent()) {
        process(t.get());
      } else {
        try {
          Thread.sleep(WORKER_SLEEP_LEN);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public void interrupt() {
    this.interrupted = true;
  }

  public void process(Task nextTask) {
    Message m = nextTask.getMessage();
    if (nextTask.getCommand() == Command.POST) {
      nextTask.getBoard().addMessage(m);
    } else {
      if (!nextTask.getBoard().deleteMessage(m)) {
        backlog.add(nextTask);
      }
    }
  }
}
