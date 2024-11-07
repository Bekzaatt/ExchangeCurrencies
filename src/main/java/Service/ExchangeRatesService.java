package Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ExchangeRatesService {
    List<Map<String, Object>> getAllExchangeRates() throws SQLException;
    List<Map<String, Object>> getSpecifiedExchangeRate(String Code) throws SQLException;
    List<Map<String, Object>> addExchangeRate(Map<String, Object> exchange) throws SQLException;

    List<Map<String, Object>> changeExchangeRates(String rate, String code) throws SQLException;

    List<Map<String, Object>> exchange(String from, String to, String rate) throws SQLException;
}
