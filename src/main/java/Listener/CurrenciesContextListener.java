package Listener;

import Service.CurrenciesService;
import Service.CurrenciesServiceImpl;
import dao.CurrenciesDao;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class CurrenciesContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        CurrenciesDao currenciesDao = new CurrenciesDao();
        CurrenciesService currenciesService = new CurrenciesServiceImpl(currenciesDao);

        sce.getServletContext().setAttribute("currenciesService", currenciesService);
    }


}
