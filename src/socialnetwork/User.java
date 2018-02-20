package socialnetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import socialnetwork.domain.Board;
import socialnetwork.domain.Message;

public class User extends Thread {

  private static final AtomicInteger nextId = new AtomicInteger(0);

  protected final SocialNetwork socialNetwork;
  private final int id;
  private final String name;
  private static double DEFAULT_DELETE_PROBABLITY = 0.5;
  private final Random random;


  public User(String username, SocialNetwork socialNetwork) {
    this.name = username;
    this.id = User.nextId.getAndIncrement();
    this.socialNetwork = socialNetwork;
    random = new Random();
  }

  public int getUserId() {
    return id;
  }

  @Override
  public void run() {
    Set<User> users = randomSubset(socialNetwork.getAllUsers());
    socialNetwork.postMessage(this, users, "hello there");

    Board board = socialNetwork.userBoard(this);

    if(board.size() > 0) {
      for (Message m : board.getBoardSnapshot()) {
        if(shouldDelete(DEFAULT_DELETE_PROBABLITY)) {
          socialNetwork.deleteMessage(m);
        }
      }
    }
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", name='" + name + '\'' +
        '}';
  }

  @Override
  public int hashCode() {
    return id;
  }

  private <T> Set<T> randomSubset(Set<T> set){
    List<T> list = new ArrayList<>();
    list.addAll(set);
    Collections.shuffle(list);
    list = list.subList(0, random.nextInt(list.size()-1));
    return new HashSet<>(list);
  }

  private boolean shouldDelete(double probability) {
    return random.nextInt(100) > 100*(1-probability);
  }
}
