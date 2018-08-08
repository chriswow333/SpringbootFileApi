package cwb.opendata.fileapi.common.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("userDao")
public class UserDao {
	
    @Autowired
    private JdbcTemplate template;
    
    private static final String GET_COUNT_FROM_USER_BY_AUTHKEY = "SELECT count(*) as count FROM user WHERE authkey = ? AND status = 1";
    
    private static final String GET_USERNAME_FROM_USER_BY_AUTHKEY = "SELECT userid FROM user WHERE authkey = ? AND status = 1";

    public boolean checkAuthorizationExist(String authkey) {
    	return template.queryForObject(GET_COUNT_FROM_USER_BY_AUTHKEY, new Object[] {authkey},Integer.class)==1?true:false;
    }
    
    public String getUsernameByAuthorization(String authkey) {
    	try {
    		return template.queryForObject(GET_USERNAME_FROM_USER_BY_AUTHKEY, new Object[] {authkey}, String.class);
    	}catch(EmptyResultDataAccessException ex) {
    		return null;
    	}
    }
    
}
