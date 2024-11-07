package Service;

import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;

public interface CurrenciesService {
    JSONArray getAllCurrencies() throws SQLException;
    JSONObject getCurrencyByCode(String Code) throws SQLException;
    JSONObject addNewCurrency(JSONObject jsonObject) throws SQLException;
    void checkCurrency(HttpServletResponse response, String requestParam) throws IOException, SQLException;
}
