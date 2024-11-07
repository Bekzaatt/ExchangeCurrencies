package ExceptionHandler;


import ExceptionHandler.Exceptions.*;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ExceptionHandler implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        }catch (CurrencyNotFoundException ex) {
            ((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
            servletResponse.setContentType("application/json");
            servletResponse.getWriter().write("{\"error\": \"" + ex.getMessage() + "\"}");
        }catch(AbsentFormException ex){
            ((HttpServletResponse)servletResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
            servletResponse.setContentType("application/json");
            servletResponse.getWriter().write("{\"error\": \"" + ex.getMessage() + "\"}");
        }catch(AbsentOfCurrencyPair ex){
            ((HttpServletResponse)servletResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
            servletResponse.setContentType("application/json");
            servletResponse.getWriter().write("{\"error\": \"" + ex.getMessage() + "\"}");
        }catch (DBException ex){
            ((HttpServletResponse)servletResponse).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            servletResponse.setContentType("application/json");
            servletResponse.getWriter().write("{\"error\": \"" + ex.getMessage() + "\"}");
        }catch (SuchCurrenciesExists ex){
            ((HttpServletResponse)servletResponse).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            servletResponse.setContentType("application/json");
            servletResponse.getWriter().write("{\"error\": \"" + ex.getMessage() + "\"}");
        }
    }


}
