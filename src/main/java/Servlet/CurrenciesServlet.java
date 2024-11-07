package Servlet;

import ExceptionHandler.Exceptions.AbsentFormException;
import ExceptionHandler.Exceptions.DBException;
import ExceptionHandler.Exceptions.SuchCurrenciesExists;
import Service.CurrenciesService;
import Service.CurrenciesServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

public class CurrenciesServlet extends HttpServlet{
    private CurrenciesService currenciesService;

    public void init() throws ServletException {
        this.currenciesService = (CurrenciesServiceImpl) getServletContext().getAttribute("currenciesService");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processingAllGetMethods(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processingAllPostMethods(request, response);
    }

    private void processingAllGetMethods(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String requestPath = request.getRequestURI();
        String requestParam = request.getPathInfo();
        System.out.println("Request Path: " + requestPath);
        System.out.println("Request Param: " + requestParam);

        if(requestPath.equals("/currencies")){
            try {
                JSONArray jsonArray = currenciesService.getAllCurrencies();
                writer.write(String.valueOf(jsonArray));
                response.setStatus(200);
                return;
            }catch (SQLException e){
                throw new DBException("Owibka");
            }
        }
        if(requestParam != null && requestParam.toUpperCase().matches("\\/[A-Z]{3}") ){
            try {
                JSONObject jsonObject = currenciesService.getCurrencyByCode(requestParam.substring(1).toUpperCase());
                currenciesService.checkCurrency(response, requestParam);

                writer.write(String.valueOf(jsonObject));
            }catch (SQLException e){
                throw new DBException("Owibka");
            }
            response.setStatus(200);
        }else{
            throw new AbsentFormException("Код валюты отсутствует в адресе");
        }

    }

    private void processingAllPostMethods(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter writer;
        try {
            writer = response.getWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String pathInfo = request.getRequestURI();

        String name = request.getParameter("name");
        String code = request.getParameter("code");
        String sign = request.getParameter("sign");

        if(name == null || name.isEmpty() || code==null || code.isEmpty()
                || sign==null || sign.isEmpty()){
            throw new AbsentFormException("Отсутствует нужное поле формы");
        }

        JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", name);
            jsonObject.put("code", code);
            jsonObject.put("sign", sign);

        if (pathInfo.equals("/currencies")) {
            try {
                jsonObject = currenciesService.addNewCurrency(jsonObject);
                if (jsonObject == null || jsonObject.isEmpty()) {
                    throw new SuchCurrenciesExists("Валюта с таким кодом уже существует");
                }
                writer.write(String.valueOf(jsonObject));

            }catch (SQLException e){
                throw new DBException("Owibka");
            }

            response.setStatus(201);
        }


    }


}
