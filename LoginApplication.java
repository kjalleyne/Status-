import javax.xml.transform.Result;
import java.beans.PropertyEditorSupport;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LoginApplication {

    public static void main(String[] args) throws SQLException {

        Scanner scanner = new Scanner(System.in);
        Database databaseManager = new Database();

        //Create a default user for demonstration purposes)
        String defaultUserName = "Ethan Roppel";
        String defaultEmail = "ethan@example.com";
        String defaultPassword = "password123";
        databaseManager.addUser(defaultUserName, defaultEmail, defaultPassword);
        System.out.println("Default user created.");

        // User login attempt
        System.out.println("Please enter your email:");
        String email = scanner.nextLine();
        System.out.println("Please enter your password:");
        String password = scanner.nextLine();


        // Check credentials
        if (databaseManager.checkCredentials(email, password)) {
            System.out.println("Login success.");
        } else {
            System.out.println("Login failed.");
        }


        // We will use the enum value for Wisdom
        int categoryID = TaskCategory.STR.getValue();

        // Add a new task to the db
        databaseManager.addTask(65, "Workout",categoryID );

        // Assign the first task in the database to the user with id 1
        databaseManager.assignUserTask(1, 1);

        // Go through and select all tasks of user with id = 1
        List<Task> tasks = databaseManager.getAllUserTasks(1);
        for(Task t: tasks){
            System.out.println(t);
            System.out.println("----------");
        }

        scanner.close();
    }
}