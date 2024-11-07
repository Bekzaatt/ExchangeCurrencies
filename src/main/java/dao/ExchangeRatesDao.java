package dao;

import ExceptionHandler.Exceptions.AbsentOfCurrencyPair;
import ExceptionHandler.Exceptions.CurrencyNotFoundException;
import Model.Currencies;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExchangeRatesDao {
    private static final String URL = "jdbc:sqlite:C:/Users/Asus/sqlite/mydatabase.db";
    private static Connection connection;
    private CurrenciesDao currenciesDao = new CurrenciesDao();

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(URL);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Map<String, Object>>  getAllExchangeRates() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT exchng.id, curr1.id as c1_id, curr1.code as c1_code, curr1.Fullname as c1_name, curr1.sign as c1_sign, curr2.id as c2_id, curr2.code as c2_code" +
                        ", curr2.Fullname as c2_name, curr2.sign as c2_sign, exchng.rate FROM Exchange_Rates exchng INNER JOIN " +
                        "Currencies curr1 ON curr1.id = exchng.baseCurrencyId INNER JOIN Currencies curr2" +
                        " ON curr2.id = exchng.targetCurrencyId WHERE curr1.id != curr2.id");
        ResultSet resultSet = preparedStatement.executeQuery();

        List<Map<String, Object>> exchangeRatesList = new ArrayList<>();
        while (resultSet.next()){
            Map<String, Object> exchangeRateMap = new LinkedHashMap<>();

            exchangeRateMap.put("ID", resultSet.getInt("ID"));

            Currencies baseCurrency = new Currencies(
                    resultSet.getInt("c1_id"),
                    resultSet.getString("c1_code"),
                    resultSet.getString("c1_name"),
                    resultSet.getString("c1_sign")
            );
            exchangeRateMap.put("baseCurrency", baseCurrency);

            Currencies targetCurrency = new Currencies(
                    resultSet.getInt("c2_id"),
                    resultSet.getString("c2_code"),
                    resultSet.getString("c2_name"),
                    resultSet.getString("c2_sign")
            );
            exchangeRateMap.put("targetCurrency", targetCurrency);

            BigDecimal rate = resultSet.getBigDecimal("rate");
            exchangeRateMap.put("rate", rate);

            exchangeRatesList.add(exchangeRateMap);

        }

        return exchangeRatesList;
    }

    public List<Map<String, Object>> getSpecifiedExchangeRate(String code) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT e.id, " +
                        "c1.id as c1_id, " +
                        "c1.code as c1_code, " +
                        "c1.fullname as c1_name, " +
                        "c1.sign as c1_sign, c2.id as " +
                        "c2_id, c2.code as c2_code, " +
                        "c2.fullname as c2_name, " +
                        "c2.sign as c2_sign," +
                        "e.rate FROM exchange_rates e " +
                        "INNER JOIN Currencies c1 ON c1.id = e.baseCurrencyId " +
                        "Inner JOIN Currencies c2 ON c2.id = e.targetCurrencyId " +
                        "WHERE c1.id != c2.id" +
                        " and (c1.code||c2.code) = ?");
        preparedStatement.setString(1, code);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Map<String, Object>> exchangeRatesList = new ArrayList<>();
        while (resultSet.next()){
            Map<String, Object> tempMap = new LinkedHashMap<>();
            tempMap.put("ID", resultSet.getInt("ID"));

            Map<String, Object> base = new LinkedHashMap<>();
            base.put("id", resultSet.getInt("c1_id"));
            base.put("code", resultSet.getString("c1_code"));
            base.put("name", resultSet.getString("c1_name"));
            base.put("sign", resultSet.getString("c1_sign"));
            tempMap.put("baseCurrency", base);


            Map<String, Object> target = new LinkedHashMap<>();
            target.put("id", resultSet.getInt("c2_id"));
            target.put("code", resultSet.getString("c2_code"));
            target.put("name", resultSet.getString("c2_name"));
            target.put("sign", resultSet.getString("c2_sign"));
            tempMap.put("targetCurrency", target);

            tempMap.put("rate", resultSet.getString("rate"));

            exchangeRatesList.add(tempMap);
        }
        return exchangeRatesList;
    }

    public List<Map<String, Object>> addExchangeRate(Map<String, Object> exchange) throws SQLException {
        JSONObject jsonBase =
                currenciesDao.getCurrencyByCode(String.valueOf(exchange.get("baseCurrencyCode")));
        JSONObject jsonTarget  =
                currenciesDao.getCurrencyByCode(String.valueOf(exchange.get("targetCurrencyCode")));

        if (jsonBase == null || jsonTarget == null || jsonBase.isEmpty() || jsonTarget.isEmpty()) {
            throw new CurrencyNotFoundException("Одна (или обе) валюта из валютной пары не существует в БД");
        }
        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO Exchange_Rates(BaseCurrencyId, TargetCurrencyId, Rate) VALUES (?, ?, ?)");

        BigDecimal rate = new BigDecimal(String.valueOf(exchange.get("rate")));
        preparedStatement.setInt(1, jsonBase.getInt("ID"));
        preparedStatement.setInt(2, jsonTarget.getInt("ID"));
        preparedStatement.setBigDecimal(3, rate);


        preparedStatement.executeUpdate();

        String base = jsonBase.getString("Code");
        String target = jsonTarget.getString("Code");
        String code = base + target;

        return getSpecifiedExchangeRate(code);
    }

    public List<Map<String, Object>> changeExchangeRates(String rate, String code) throws SQLException {
        String targetCode = code.substring(3);
        String baseCode = code.substring(0, 3);

        JSONObject base = currenciesDao.getCurrencyByCode(baseCode);
        JSONObject target = currenciesDao.getCurrencyByCode(targetCode);

        if(base == null || base.isEmpty() || target == null || target.isEmpty()){
            throw new AbsentOfCurrencyPair("Валютная пара отсутствует в базе данных");
        }
        PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE Exchange_Rates SET BaseCurrencyId = ?, TargetCurrencyId = ?, Rate = ?" +
                        " WHERE ID = ?");
        BigDecimal bigDecimal = new BigDecimal(rate);
        preparedStatement.setInt(1, base.getInt("ID"));
        preparedStatement.setInt(2, target.getInt("ID"));
        preparedStatement.setBigDecimal(3, bigDecimal);
        preparedStatement.setInt(4 , 1);

        preparedStatement.executeUpdate();

        return getSpecifiedExchangeRate(code);
    }

    public List<Map<String, Object>> exchange(String from, String to, String amount, String convertedAmount) throws SQLException {
        List<Map<String, Object>> exchangeResult = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT c1.id as c1_id, " +
                        "c1.code as c1_code, " +
                        "c1.fullname as c1_name, " +
                        "c1.sign as c1_sign, c2.id as " +
                        "c2_id, c2.code as c2_code, " +
                        "c2.fullname as c2_name, " +
                        "c2.sign as c2_sign, " +
                        "e.rate FROM exchange_rates e " +
                        "INNER JOIN Currencies c1 ON c1.id = e.baseCurrencyId " +
                        "Inner JOIN Currencies c2 ON c2.id = e.targetCurrencyId " +
                        "WHERE c1.id != c2.id" +
                        " and (c1.code||c2.code) = ?");
        preparedStatement.setString(1, from+to);
        ResultSet resultSet = preparedStatement.executeQuery();
        System.out.println(resultSet.getBigDecimal("rate"));
        while (resultSet.next()){
            Map<String, Object> tempMap = new LinkedHashMap<>();

            Map<String, Object> base = new LinkedHashMap<>();
            base.put("id", resultSet.getInt("c1_id"));
            base.put("code", resultSet.getString("c1_code"));
            base.put("name", resultSet.getString("c1_name"));
            base.put("sign", resultSet.getString("c1_sign"));
            tempMap.put("baseCurrency", base);


            Map<String, Object> target = new LinkedHashMap<>();
            target.put("id", resultSet.getInt("c2_id"));
            target.put("code", resultSet.getString("c2_code"));
            target.put("name", resultSet.getString("c2_name"));
            target.put("sign", resultSet.getString("c2_sign"));
            tempMap.put("targetCurrency", target);

            tempMap.put("rate", resultSet.getBigDecimal("rate"));
            tempMap.put("amount", amount);
            tempMap.put("convertedAmount", convertedAmount);

            exchangeResult.add(tempMap);
        }
        return exchangeResult;
    }
}
