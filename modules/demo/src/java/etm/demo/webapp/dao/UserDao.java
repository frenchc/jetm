package etm.demo.webapp.dao;

/**
 *
 *
 * @version $Id: UserDao.java,v 1.2 2006/06/25 13:57:45 french_c Exp $
 * @author void.fm
 */
public interface UserDao {


  public User create(User user);

  public User findUser(String aUsername, String aPassword);

  public boolean hasUserWithUsername(String aUsername);
}
