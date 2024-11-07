package Servlet;

import ExceptionHandler.Exceptions.AbsentFormException;
import ExceptionHandler.Exceptions.AbsentOfCurrencyPair;
import ExceptionHandler.Exceptions.DBException;
import ExceptionHandler.Exceptions.SuchCurrenciesExists;
import Service.ExchangeRatesService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeRatesService exchangeRatesService;

    public void init() throws ServletException {
        this.exchangeRatesService = (ExchangeRatesService)getServletContext().getAttribute("exchangeRatesService");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processingAllGetMethods(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            processingAllPostMethods(request, response);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void processingAllGetMethods(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        String path = request.getRequestURI();
        String requestParam = request.getPathInfo();
        Gson gson = new Gson();
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        String amount = request.getParameter("amount");
        System.out.println("path: " + path);
        System.out.println("requestParam: " + requestParam);

        if(path.equals("/exchangeRates")){
            try {
                writer.write(gson.toJson(exchangeRatesService.getAllExchangeRates()));
                response.setStatus(200);
                return;
            } catch (SQLException e) {
                throw new DBException("Owbika");
            }

        }
        else if(requestParam != null && requestParam.toUpperCase().matches("\\/[A-Z]{6}")){
            try {
                List<Map<String, Object>> exchangeRates =
                        exchangeRatesService.getSpecifiedExchangeRate(requestParam.substring(1).toUpperCase());
                if(exchangeRates == null || exchangeRates.isEmpty()){
                    throw new AbsentOfCurrencyPair("Обменный курс для пары не найден");
                }
                writer.write(gson.toJson(exchangeRates));
                response.setStatus(200);
            } catch (SQLException e) {
                throw new DBException("Owbika");
            }
        }
        else if(path.equals("/exchange")){
            try {
                writer.write(gson.toJson(exchangeRatesService.exchange(from, to, amount)));
            } catch (SQLException e) {
                throw new DBException("Owbika");
            }
        }
        else{
            throw new AbsentFormException("Коды валют пары отсутствуют в адресе");
        }


    }

    protected void processingAllPostMethods(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        String path = request.getRequestURI();

        String baseCurrencyCode = request.getParameter("baseCurrencyCode");
        String targetCurrencyCode = request.getParameter("targetCurrencyCode");
        String rate = request.getParameter("rate");
        if(baseCurrencyCode == null || baseCurrencyCode.isEmpty() ||
                targetCurrencyCode == null || targetCurrencyCode.isEmpty() ||
                rate == null || rate.isEmpty()){
            throw new AbsentFormException("Отсутствует нужное поле формы");
        }
        Map<String, Object> exchange = new LinkedHashMap<>();
        exchange.put("baseCurrencyCode", baseCurrencyCode);
        exchange.put("targetCurrencyCode", targetCurrencyCode);
        exchange.put("rate", rate);
        String code = exchange.get("baseCurrencyCode") + "" + exchange.get("targetCurrencyCode");
        List<Map<String,Object>> checkList = exchangeRatesService.getSpecifiedExchangeRate(code);

        if(checkList != null && !checkList.isEmpty()){
            throw new SuchCurrenciesExists("Валютная пара с таким кодом уже существует");
        }

        PrintWriter writer = response.getWriter();
        Gson gson = new Gson();

        if(path.equals("/exchangeRates")){

            try {
                List<Map<String, Object>> exchangeRate = exchangeRatesService.addExchangeRate(exchange);
                writer.write(gson.toJson(exchangeRate));

            } catch (SQLException e) {
                throw new DBException("Owibka");
            }
        }
    }
    @Override
    protected void doPut(HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp) throws ServletException, IOException {
        processingAllPatchMethods(req, resp);
    }


    protected void processingAllPatchMethods(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String rate = request.getParameter("rate");

        String param = request.getPathInfo();
        Writer writer = response.getWriter();
        Gson gson = new Gson();
        if(rate != null && param != null && param.toUpperCase().matches("\\/[A-Z]{6}")){
            try {
                writer.write(gson.toJson(
                        String.valueOf(exchangeRatesService.changeExchangeRates(rate, param.toUpperCase().substring(1)))));

            } catch (SQLException e) {
                throw new DBException("Owibka");
            }
        }else{
            throw new AbsentFormException("Отсутствует нужное поле формы");
        }
    }
}
