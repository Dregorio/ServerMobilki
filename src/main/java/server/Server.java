package server;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

public class Server {

    private static ServerSocket listener = null;
    private static Socket socket = null;
    private static Gson gson = null;
    private static Connection conn = null;
    private static Statement stmt = null;
    private static ResultSet rs = null;
    private static PreparedStatement preparedStatement = null;
    private static Boolean isInDb = false;

    public static void main(String[] args) {

        try {
            listener = new ServerSocket(9090);
            gson = new Gson();
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            while (true) {
                socket = listener.accept();
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                    JsonReader reader = new JsonReader(in);
                    JsonWriter writer = new JsonWriter(out);
                    String fromJson = null;

                    try{
                        if (socket.isConnected()){
                            conn = DriverManager.getConnection("jdbc:mysql://localhost/test? user=root&password=");
                        }
                        fromJson = gson.fromJson(reader, String.class);
                        String[] data = fromJson.split(":");

                        String selectSQL = "SELECT k.login, k.haslo FROM klienci k WHERE k.login = ? AND k.haslo = ?";
                        if (!conn.isClosed()){
                                preparedStatement = conn.prepareStatement(selectSQL);
                                preparedStatement.setString(1, data[0]);
                                preparedStatement.setString(2, data[1]);

                                rs = preparedStatement.executeQuery();

                                if (rs.next()){
                                    isInDb = true;
                                }

                                gson.toJson(isInDb, Boolean.class, writer);
                        }



                    }catch (SQLException sqle){
                        System.out.println("SQLException: " + sqle.getMessage());
                        System.out.println("SQLState: " + sqle.getSQLState());
                        System.out.println("VendorError: " + sqle.getErrorCode());
                    }


                    //gson.toJson(writer, )
                }finally {
                    socket.close();
                }
            }
        }catch (ClassNotFoundException ce){
            ce.printStackTrace();
        }catch (IOException e){
            System.out.println(e.getMessage());
            System.exit(-1);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } finally {
            try{
                if (listener != null)
                    listener.close();
            }catch (IOException e){
                System.out.println(e.getMessage());
                System.exit(-1);
            }

        }

    }
}
