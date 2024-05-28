package com.example.statusplusplus.DatabaseModels;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.example.statusplusplus.Classes.*;

public class Database {

    /**
     * The required strings for accessing the database.
     */
    private static final String URL = "jdbc:mysql://localhost:3306/380Project"; 
    private static final String USERNAME = "root";
    private static final String PASSWORD = "cs380";

    /**
     * Connects to the database.
     *
     * @return A connection object which represents the database connection.
     * @throws SQLException thrown when there is an error accessing the database.
     */
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }



    /*=====================================================================================
                                 USER MODELS
    =====================================================================================*/
    /**
     * Adds a user to the database, also initializes their "userstats" tuple.
     */
    public void addUser(String userName, String email, String password) throws SQLException {
        String sql = "INSERT INTO users (userName, email, password) VALUES (?, ?, ?)";
        String statsql = "INSERT INTO userStats (userIDStats, intelligence, strength, endurance" +
                         ", wisdom, vitality, skillpoints, exp, level) VALUES (?, 0, 0, 0, 0, 0, 0, 0, 1)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            // Execute the first statement, adding the user to the db
            pstmt.setString(1, userName);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.executeUpdate();

            // Get the userID that was automatically made with previous statement
            ResultSet rs = pstmt.getGeneratedKeys();

            //Prepare the statement to intitialize the userstats entry
            PreparedStatement initUserStats = conn.prepareStatement(statsql);

            // See if the autogenerated key exists
            if(rs.next()){
                int userID = rs.getInt(1);

                initUserStats.setInt(1, userID);
                initUserStats.executeUpdate();

                System.out.println("User and stats added successfully");
            } else{
                System.out.println("Failed to retrieve userID. User was created but their stats were not!");
            }

        } catch (SQLException e) {
            System.out.println("Error adding user: " + e.getMessage());
        }
    }

    /**
     * Updates the user.
     *
     * @param userID The ID of the user.
     * @param userName The name of the user.
     * @param email The email of the user.
     * @param password The password of the user.
     * @throws SQLException Thrown when there is an error accessing the database.
     */
    public void updateUser(int userID, String userName, String email, String password) throws SQLException {
        String sql = "UPDATE users SET userName = ?, email = ?, password = ? WHERE userID = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setInt(4, userID);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("User updated successfully.");
            } else {
                System.out.println("No user found with ID: " + userID);
            }
        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
    }

    /**
     * Deletes a user from the database.
     *
     * @param userID The id of the user.
     * @throws SQLException Thrown when there is an error accessing the database.
     */
    public void deleteUser(int userID) throws SQLException {
        String sql = "DELETE FROM users WHERE userID = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("User deleted successfully.");
            } else {
                System.out.println("No user found with ID: " + userID);
            }
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }

    /**
     * Prints all the users.
     *
     * @throws SQLException Thrown when there is an error accessing the database.
     */
    public void printUsers() throws SQLException {
        String sql = "SELECT userID, userName, email, password FROM users"; // Include password if necessary for verification (not recommended to print passwords)
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("UserID: " + rs.getInt("userID") + ", UserName: " + rs.getString("userName") + ", Email: " + rs.getString("email") + ", Password: " + rs.getString("password"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving users: " + e.getMessage());
        }
    }

    /**
     * Checks the credentials of the user.
     *
     * @param email The email of the user.
     * @param password The password of the user.
     * @return The result of comparing the input strings to the database, true or false depending on if they match.
     * @throws SQLException
     */
    public boolean checkCredentials(String email, String password) throws SQLException {
        String sql = "SELECT password FROM users WHERE email = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password);
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Error checking credentials: " + e.getMessage());
            return false;
        }
    }

    /**
     * A method to get a user from the database
     * @param userID The userID for the user you need the information of.
     * @return  The user that has the id input. Type: userID
     */
    public User getUserByID(int userID){

        // Need to think about if this will be needed, along with what
        return null;
    }

    /*=====================================================================================
                                 TASK MODELS
    =====================================================================================*/
    /**
     * Adds a new Task to the database.
     * @param expGained The amount of exp gained by completing the task. Type: Integer
     * @param taskName  The name of the task (must be unique, or this will fail). Type: String
     * @param category The category number of the task. Type: Integer
     */
    public void addTask(int expGained, String taskName, int category){
        String sql = "INSERT INTO tasks (expGained, taskName, category) VALUES (?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, expGained);
            pstmt.setString(2, taskName);
            pstmt.setInt(3, category);
            pstmt.executeUpdate();
            System.out.println("Task added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding task: " + e.getMessage());
        }
    }

    public void assignUserTask(int userID, int taskID){
        String sql = "INSERT INTO usertasks (userIDTasks, taskID) VALUES (?,?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            pstmt.setInt(2, taskID);
            pstmt.executeUpdate();
            System.out.println("Added task to user-tasks table!");
        } catch (SQLException e) {
            System.out.println("Error adding to user-tasks table: " + e.getMessage());
        }
    }


    /**
     * A function to get all of the tasks for a user.
     * @param userID The userID of the user you are trying to get the task list of. Type: Integer
     * @return  List of Tasks that belong to the user.
     */
    public ArrayList<Task> getAllUserTasks(int userID){
        ArrayList<Task> tasks = new ArrayList<>();

        String sql = "SELECT tasks.* " +
                "FROM tasks " +
                "JOIN userTasks ON tasks.taskID = userTasks.taskID " +
                "WHERE userTasks.userIDTasks = ?";
        try(Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, userID);
            System.out.println("Successfully retrieved users tasks. Returning as ResultSet. ");
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                // Get all the fields of a task
                int id = rs.getInt("taskID");
                int xp = rs.getInt("expGained");
                String name = rs.getString("taskName");
                int catVal = rs.getInt("category");
                TaskCategory category = TaskCategory.fromInt(catVal);

                // Try to rebuild the task
                Task t = new Task(id, xp,category ,name);

                // Add the task to the result list
                tasks.add(t);
            }
        }catch (SQLException e){
            System.out.println("Failed to retrieve users tasks: " + e.getMessage());
        }

        return tasks;
    }

    /**
     * Removes a task from a users task list
     * @param userID The id of the user you want to remove the task from. Type: Integer
     * @param taskID The id of the task you want to remove from the user. Type: Integer
     */
    public void removeUserTask(int userID, int taskID){
        String sql = "DELETE FROM usertasks " +
                     "WHERE userIDTasks = ? " +
                     "AND taskID = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            pstmt.setInt(2, taskID);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Task deleted successfully from User.");
            } else {
                System.out.println("FAILED TO DELETE USER TASK!");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting user task: " + e.getMessage());
        }
    }



    /*=====================================================================================
                                 STATS MODELS

    NOTE: Lots of very similar code, chose this way for each of use for backend functionality
          instead of needing to convert from enum to string to increase different attributes.
          Hopefully it will slim down functionality code by having dedicated methods for each thing.
    =====================================================================================*/

    /**
     * Modify a specified user stat by a specified value.
     * @param userID The ID of user to change. Type: Integer
     * @param userStat The user stat to change. Type: TaskCategory
     * @param increaseBy The amount to change by (can be negative). Type: Integer
     */
    public void increaseUserStat(int userID, TaskCategory userStat, int increaseBy) {

        String stat = userStat.getString();
        String sql = "UPDATE userstats SET " + stat + " = "  + stat +  " + ? WHERE userIDStats = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, increaseBy);
            pstmt.setInt(2, userID);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Increased user " + stat);
            } else {
                System.out.println("No user found with ID: " + userID);
            }

        } catch (SQLException e) {
            System.out.println("Error increasing " + stat + ": " + e.getMessage());
        }
    }

    /**
     * Increase the level of a user
     * @param userID UserID of user to increase the level of. Type: Integer
     * @param increaseBy The amount to increase the level of the user. Type: Integer
     */
    public void increaseUserLevel(int userID, int increaseBy){
        String sql = "UPDATE userstats SET level = level + ? WHERE userIDStats = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, increaseBy);
            pstmt.setInt(2, userID);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Increased the user level by: " + increaseBy);
            } else {
                System.out.println("No user found with ID: " + userID);
            }

        } catch (SQLException e) {
            System.out.println("Error increasing level: " + e.getMessage());
        }
    }

    /**
     * Changes a users skillpoints.
     * @param userID The ID of user to change. Type: Integer
     * @param increaseBy The amount to change by (can be negative). Type: Integer
     */
    public void increaseUserSkillPoints(int userID, int increaseBy){
        String sql = "UPDATE userstats SET skillpoints = skillpoints + ? WHERE userIDStats = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, increaseBy);
            pstmt.setInt(2, userID);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Increased user skillpoints.");
            } else {
                System.out.println("No user found with ID: " + userID);
            }

        } catch (SQLException e) {
            System.out.println("Error increasing skillpoints: " + e.getMessage());
        }
    }

    /**
     * Changes a users exp amount.
     * @param userID The ID of user to change. Type: Integer
     * @param increaseBy The amount to change by (can be negative). Type: Integer
     */
    public void increaseUserEXP(int userID, int increaseBy){
        String sql = "UPDATE userstats SET exp = exp + ? WHERE userIDStats = ?";


        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, increaseBy);
            pstmt.setInt(2, userID);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Increased user exp.");
            } else {
                System.out.println("No user found with ID: " + userID);
            }

        } catch (SQLException e) {
            System.out.println("Error increasing exp: " + e.getMessage());
        }
    }

    /**
     * Resets a users exp stat to 0. Will be used when they gain a skillpoint/level up.
     * @param userID The user ID of the user to reset the exp of.
     */
    public void resetUserEXP(int userID){
        String sql = "UPDATE userstats SET exp = 0 WHERE userIDStats = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Reset user exp.");
            } else {
                System.out.println("No user found with ID: " + userID);
            }

        } catch (SQLException e) {
            System.out.println("Error resetting: " + e.getMessage());
        }
    }

    /**
     * Gets one skill level of a user
     * @param userID The user-id of the user to get a skill level from. Type: Integer
     * @param t The skill category to get from the user. Type: TaskCategory Enum
     * @return Skill level of specified skill category of the user. Type: Integer
     */
    public int getSkillLevel(int userID, TaskCategory t){
        
        String stat = t.getString();
        String sql = "SELECT " + stat + " FROM userstats WHERE userIDStats = ?";
        int skillLevel = Integer.MIN_VALUE;

        try(Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, userID);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                skillLevel = rs.getInt(1);
            }

        }catch (SQLException e){
            System.out.println("Failed to retrieve users tasks: " + e.getMessage());
        }

        return skillLevel;
    }

    /**
     * A method that takes in an email from the login page, gets the user and returns it.
     * @param EMAIL The email of the user to try to build.
     * @return A complete user object.
     */
    public User getUserByEmail(String EMAIL){
        // Join user table to userstats on userId and get the info needed to build a user object
        String sql = "SELECT u.userId, u.userName, s.intelligence, s.strength, s.endurance, s.wisdom, s.vitality, s.skillpoints, s.level, s.exp FROM users u JOIN userstats s ON u.userID = s.userIDStats WHERE email = ?";

        try(Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, EMAIL);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("userId");
                String userName = rs.getString("userName");
                int INT = rs.getInt("intelligence");
                int STR = rs.getInt("strength");
                int END = rs.getInt("endurance");
                int WIS = rs.getInt("wisdom");
                int VIT = rs.getInt("vitality");
                int skillP = rs.getInt("skillpoints");
                int level = rs.getInt("level");
                int exp = rs.getInt("exp");

                //SkillLevels(int intelligence, int strength, int endurance, int wisdom, int vitality)
                SkillLevels sk = new SkillLevels(INT, STR, END, WIS, VIT);

                //UserStats(int skillPoints, int exp, int level, SkillLevels skillLevels)
                UserStats stats = new UserStats(skillP, exp, level, sk);

                //User(String userName, int userID, UserStats stats, boolean isOnStreak)
                // We dont have functionality for the streak yet, but its still in user class so assume false for now
                User s = new User(userName, id, stats, false );
                System.out.println(s);
                return s;
            }
        }catch (Exception e){
            System.out.println("Failed to get the user by email: " + e.getMessage());
        }

        // Make sure to check if null at all times
        return null;
    }
}
