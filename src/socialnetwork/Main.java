package socialnetwork;

import socialnetwork.domain.LinkedBacklog;
import socialnetwork.domain.LinkedBoard;
import socialnetwork.domain.Worker;

import java.util.Arrays;

public class Main {

  private static final int NUM_OF_WORKERS = 10;
  private static final int NUM_OF_USERS = 20;

  public static void main(String[] args) {
    LinkedBacklog backlog = new LinkedBacklog();
    SocialNetwork socialNetwork = new SocialNetwork(backlog);
    Worker[] workers = new Worker[NUM_OF_WORKERS];
    Arrays.setAll(workers, i -> new Worker(backlog));
    Arrays.stream(workers).forEach(Thread::start);

    User[] users = new User[NUM_OF_USERS];
    Arrays.setAll(users, i -> new User("user" + Integer.toString(i), socialNetwork));
    Arrays.stream(users).forEach(user -> socialNetwork.register(user, new LinkedBoard()));
    Arrays.stream(users).forEach(Thread::start);
    Arrays.stream(users).forEach(user -> {
      try {
        user.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });

    while (backlog.numberOfTasksInTheBacklog() != 0) {
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    Arrays.stream(workers).forEach(Worker::interrupt);
    Arrays.stream(workers).forEach(worker -> {
      try {
        worker.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
  }
}
