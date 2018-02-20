package socialnetwork.domain;

import java.util.List;
import socialnetwork.node.SortedLinkedList;


public class LinkedBoard extends SortedLinkedList<Message> implements Board{

  @Override
  public boolean addMessage(Message message) {
    return super.addObject(message);
  }

  @Override
  public boolean deleteMessage(Message message) {
    return super.deleteObject(message);
  }

  @Override
  public int size() {
    return super.size();
  }

  @Override
  public List<Message> getBoardSnapshot() {
    return super.getListSnapshot();
  }
}



