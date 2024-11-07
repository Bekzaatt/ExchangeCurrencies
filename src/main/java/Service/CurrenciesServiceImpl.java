package Service;

import dao.CurrenciesDao;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;

public class CurrenciesServiceImpl implements CurrenciesService{
    private CurrenciesDao currenciesDao;

    public CurrenciesServiceImpl(CurrenciesDao currenciesDao) {
        this.currenciesDao = currenciesDao;
    }

    @Override
    public JSONArray getAllCurrencies() throws SQLException {
        return currenciesDao.getAllCurrencies();
    }

    public JSONObject getCurrencyByCode(String code) throws SQLException {
        return currenciesDao.getCurrencyByCode(code);
    }

    @Override
    public JSONObject addNewCurrency(JSONObject jsonObject) throws SQLException {
        JSONObject json = currenciesDao.getCurrencyByCode(String.valueOf(jsonObject.get("code")));
        if(json == null || json.isEmpty()){
            return currenciesDao.addNewCurrency(jsonObject);
        }else return null;
    }


    public void checkCurrency(HttpServletResponse response, String requestParam) throws SQLException, IOException {
        JSONObject jsonObject =
                currenciesDao.getCurrencyByCode(requestParam.substring(1).toUpperCase());
        if(jsonObject == null || jsonObject.isEmpty()){
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Валюта не найдена");
            return;
        }
    }
}
