package dao;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrenciesDao {
    private static Connection connection;
    private static final String URL = "jdbc:sqlite:C:/Users/Asus/sqlite/mydatabase.db";
    static{
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(URL);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Driver not found");
        }
    }

    public JSONArray getAllCurrencies() throws SQLException {
        JSONArray json = new JSONArray();

        PreparedStatement preparedStatement =
                    connection.prepareStatement("SELECT * FROM Currencies");
        ResultSet resultSet = preparedStatement.executeQuery();
        json = convertToJsonArray(resultSet);
        return json;
    }

    public JSONObject getCurrencyByCode(String code) throws SQLException {
        JSONObject json = new JSONObject();
            PreparedStatement preparedStatement =
                    connection.prepareStatement("SELECT * FROM Currencies WHERE Code=?");
            preparedStatement.setString(1,code);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                int columnCount = resultSetMetaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    json.put(resultSetMetaData.getColumnName(i), resultSet.getObject(i));
                }
            }
        return json;
    }

    public JSONObject addNewCurrency(JSONObject jsonObject){
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("INSERT INTO Currencies(Code, FullName, Sign) VALUES (?, ?, ?)");
            preparedStatement.setString(1, String.valueOf(jsonObject.get("code")));
            preparedStatement.setString(2, String.valueOf(jsonObject.get("name")));
            preparedStatement.setString(3, String.valueOf(jsonObject.get("sign")));

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }



    private JSONArray convertToJsonArray(ResultSet resultSet){
        JSONArray jsonArray = new JSONArray();
        try {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int numColumns = resultSetMetaData.getColumnCount();
            List<String> names = new ArrayList<>();
            for(int i = 0; i < numColumns; i++){
                names.add(resultSetMetaData.getColumnName(i + 1));
            }

            while (resultSet.next()){
                JSONObject row = new JSONObject();
                for(int i = 0; i < names.size(); i++){
                    row.put(names.get(i), resultSet.getObject(names.get(i)));
                }
                jsonArray.put(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return jsonArray;
    }
}
