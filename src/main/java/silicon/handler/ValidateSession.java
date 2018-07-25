package silicon.handler;

import silicon.model.Session;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.GenericFilterBean;
import silicon.service.SessionService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
@EnableJpaRepositories("model")
@EnableTransactionManagement
public class ValidateSession extends GenericFilterBean {

    public SessionService sessionService;

    public ValidateSession(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    void redirectError(ServletRequest req, ServletResponse res) throws IOException{
        HttpServletResponse response = (HttpServletResponse) res;
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
    }

    @Override
    @Transactional
    public void doFilter(
            ServletRequest request, ServletResponse response, FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        Boolean validPath = path.matches("^/api/.*");
        if(validPath){
            String token = request.getParameter("token");

            if(token == null || token.equals("")){
                this.redirectError(request, response);
                return;
            }

            Session session = sessionService.findByToken(token);
            if(session == null) {
                this.redirectError(request, response);
                return;
            }

            if(!session.isValid()){
                this.redirectError(request, response);
                return;
            }

            session.setExpirationDate(Utils.addTime(new Date(), Calendar.HOUR, 1));
            sessionService.save(session);
        }

        chain.doFilter(request, response);
    }
}
