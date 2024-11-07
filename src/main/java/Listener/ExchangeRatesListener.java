package Listener;

import Service.ExchangeRatesService;
import Service.ExchangeRatesServiceImpl;
import dao.ExchangeRatesDao;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ExchangeRatesListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ExchangeRatesDao exchangeRatesDao = new ExchangeRatesDao();
        ExchangeRatesService exchangeRatesService = new ExchangeRatesServiceImpl(exchangeRatesDao);
        sce.getServletContext().setAttribute("exchangeRatesService", exchangeRatesService);
    }
}
