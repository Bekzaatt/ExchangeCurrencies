package Service;

import ExceptionHandler.Exceptions.AbsentOfCurrencyPair;
import dao.CurrenciesDao;
import dao.ExchangeRatesDao;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExchangeRatesServiceImpl implements ExchangeRatesService{
    ExchangeRatesDao exchangeRatesDao;
    private CurrenciesDao currenciesDao = new CurrenciesDao();

    public ExchangeRatesServiceImpl(ExchangeRatesDao exchangeRatesDao) {

        this.exchangeRatesDao = exchangeRatesDao;

    }

    @Override
    public List<Map<String, Object>> getAllExchangeRates() throws SQLException {
        return exchangeRatesDao.getAllExchangeRates();
    }

    public List<Map<String, Object>> getSpecifiedExchangeRate(String code) throws SQLException {
        return exchangeRatesDao.getSpecifiedExchangeRate(code);
    }

    public List<Map<String, Object>> addExchangeRate(Map<String, Object> exchange) throws SQLException{
        return exchangeRatesDao.addExchangeRate(exchange);
    }

    public List<Map<String, Object>> changeExchangeRates(String rate, String code) throws SQLException {
        return exchangeRatesDao.changeExchangeRates(rate, code);
    }

    public List<Map<String, Object>> exchange(String from, String to, String amount) throws SQLException {
        List<Map<String, Object>> ex = getSpecifiedExchangeRate(from+to);
        List<Map<String, Object>> res = new ArrayList<>();
        if(ex != null && !ex.isEmpty()){
            Double rateDouble = getRate(ex.get(0));;
            double convertedAmount = rateDouble * Double.parseDouble(amount) * 1.0;
            res = exchangeRatesDao.exchange(from, to, amount, String.valueOf(convertedAmount));
            return res;
        }
        ex = getSpecifiedExchangeRate(to + from);
        if(ex != null && !ex.isEmpty()){
            Double rateDouble = getRate(ex.get(0));
            Double convertedAmount = Double.parseDouble(amount) / rateDouble;
            Map<String, Object> resultMap = initilizeMap(from, to, amount);
            resultMap.put("rate", rateDouble);
            resultMap.put("convertedAmount", convertedAmount);
            res.add(resultMap);
            return res;
        }
        ex = getSpecifiedExchangeRate(from + "USD");
        List<Map<String, Object>> reverseEx = getSpecifiedExchangeRate("USD" + to);
        if(!ex.isEmpty() && !reverseEx.isEmpty()){
            Double rateDoubleFrom = getRate(ex.get(0));
            Double convertedToUSD = Double.parseDouble(amount) * rateDoubleFrom;

            Double rateDoubleTo = getRate(reverseEx.get(0));
            Double convertedFromUSD = convertedToUSD * rateDoubleTo;

            Map<String, Object> tempResult = initilizeMap(from,to,amount);

            tempResult.put("convertedAmount", convertedFromUSD);

            res.add(tempResult);
            return res;
        }
        ex = getSpecifiedExchangeRate( "USD" + from);
        reverseEx = getSpecifiedExchangeRate(to + "USD");
        if(!ex.isEmpty() && !reverseEx.isEmpty()){
            Double rateDoubleFrom = getRate(ex.get(0));
            Double convertedToUSD = Double.parseDouble(amount) * rateDoubleFrom;

            Double rateDoubleTo = getRate(reverseEx.get(0));
            Double convertedFromUSD = convertedToUSD * rateDoubleTo;

            Map<String, Object> tempResult = initilizeMap(from,to,amount);

            tempResult.put("convertedAmount", convertedFromUSD);

            res.add(tempResult);
            return res;
        }
        throw new AbsentOfCurrencyPair("Валюта не найдена");
    }

    public Double getRate(Map<String, Object> exchangeRateData){
        String rateString = String.valueOf(exchangeRateData.get("rate"));
        Double rateDouble = Double.parseDouble(rateString);
        return rateDouble;
    }
    public Map<String, Object> initilizeMap(String from, String to, String amount) throws SQLException {
        Map<String, Object> resultMap = new LinkedHashMap<>();

        JSONObject baseObject = currenciesDao.getCurrencyByCode(from);
        JSONObject targetObject = currenciesDao.getCurrencyByCode(to);

        Map<String, Object> base = new LinkedHashMap<>();
        base.put("id",  baseObject.get("ID"));
        base.put("name",  baseObject.get("FullName"));
        base.put("code",  baseObject.get("Code"));
        base.put("sign",  baseObject.get("Sign"));

        resultMap.put("baseCurrency", base);

        Map<String, Object> target = new LinkedHashMap<>();
        target.put("id",  targetObject.get("ID"));
        target.put("name",  targetObject.get("FullName"));
        target.put("code",  targetObject.get("Code"));
        target.put("sign",  targetObject.get("Sign"));

        resultMap.put("targetCurrency", target);
        resultMap.put("amount", amount);
        return resultMap;
    }
}
