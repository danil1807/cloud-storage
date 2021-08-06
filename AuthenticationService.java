package auth;

import db.DBHandler;
import db.User;
import java.sql.SQLException;
import java.util.ArrayList;


public class AuthenticationService {


    DBHandler dbHandler = new DBHandler();
    private ArrayList<User> entries;
    {
        try {
            entries = dbHandler.printUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public User findUserByCredentials(String login, String password){
        for (User entry: entries) {
            if(entry.getLogin().equals(login) && entry.getPassword().equals(password)){
                return entry;
            }
        }
        return null;
    }
}


