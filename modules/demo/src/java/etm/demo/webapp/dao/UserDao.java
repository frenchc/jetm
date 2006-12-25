package etm.demo.webapp.dao;

/**
 *
 *
 * @version $Id$
 * @author void.fm
 */
public interface UserDao {


  public User create(User user);

  public User findUser(String aUsername, String aPassword);

  public boolean hasUserWithUsername(String aUsername);
}
