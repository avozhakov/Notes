package com.av;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by av on 12.02.16.
 */
public class DataBase {

    public static Connection connection;
    public static Statement statement;
    public static ResultSet resSet;

    private static DataBase instance = null;

    private DataBase(){

    }

    public static DataBase getInstance(){
        if(instance == null)
            instance = new DataBase();
        return instance;
    }

    // Data Base connecting
    public static void connect() {
        connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:Notes.s3db");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create tables
    public void createDB() {

        String str = Service.readFile("SQL/dump.sql");
        String [] commands = str.toString().split("AND");

        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for(String command : commands) {
            try {
                statement.execute(command);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(getSections().size() == 0)
            addSection("Scribe");

        //System.out.println("Таблица создана или уже существует.");
    }

    public void addNote(int section_id) {

        String date = Service.date("d  MMMM  yyyy");
        try {
            statement.execute("INSERT INTO notes ('section_id', 'header', 'date', 'text') VALUES " +
                    "('"+ (section_id + 1) +"', 'New note','"+ date +"', ''); ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateNote(Note note, int section_id){

        try {
            statement.executeUpdate("UPDATE notes SET text = '"+ note.getText() +"', " +
                    "header = '"+ note.getHeader() +"' WHERE id = " +
                    note.getId() + " AND section_id = '"+ (section_id + 1) +"'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addSection(String name) {

        try {
            statement.execute("INSERT INTO sections ('name') VALUES ('" + name + "'); ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getSections() {

        ArrayList<String> sections = new ArrayList<String>();
        try {
            resSet = statement.executeQuery("SELECT * FROM sections");
            while(resSet.next())
            {
                String  name = resSet.getString("name");
                sections.add(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sections;
    }

    public ArrayList<Note> getNotes(int section_id){

        ArrayList<Note> notes = new ArrayList<Note>();
        try {
            resSet = statement.executeQuery("SELECT * FROM notes WHERE section_id = " + (section_id + 1));
            while(resSet.next())
            {
                Note n = new Note();
                n.setId(resSet.getInt("id"));
                n.setHeader(resSet.getString("header"));
                n.setDate(resSet.getString("date"));
                n.setText(resSet.getString("text"));
                notes.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }

    public void closeDB() {

        try {
            connection.close();
            statement.close();
            resSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}