package silicon.controller.rest;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import silicon.handler.ConstraintViolationExceptionHandler;
import silicon.handler.ErrorResponse;
import silicon.handler.MessageResponse;
import silicon.handler.Utils;
import silicon.model.Session;
import silicon.model.User;
import silicon.service.SessionService;
import silicon.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.UUID;

@RestController
public class SessionController {

    @Autowired
    SessionService sessionService;

    @Autowired
    UserService userService;

    @RequestMapping(value="/api/logout", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(
            @RequestParam String token
    ) {
        Session session = sessionService.findByToken(token);
        session.setExpirationDate(new Date());
        sessionService.save(session);

        MessageResponse response = new MessageResponse("Done");
        return new ResponseEntity<MessageResponse>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(HttpServletRequest request) {

        JsonNode body = Utils.getBody(request);
        String email;
        try {
            email = body.get("email").asText();
            if(email.isEmpty()){
                throw new NullPointerException("email is empty");
            }
        }catch (NullPointerException ex){
            ErrorResponse error = new ErrorResponse("The parameter 'email' is required");
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);//You many decide to return HttpStatus.NOT_FOUND
        }

        String password;
        try {
            password = body.get("password").asText();
            if(password.isEmpty()){
                throw new NullPointerException("password is empty");
            }
        }catch (NullPointerException ex){
            ErrorResponse error = new ErrorResponse("The parameter 'password' is required");
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.BAD_REQUEST);//You many decide to return HttpStatus.NOT_FOUND
        }

        String digestPassword = User.getDigestPassword(password);
        User user = userService.findByEmailAndPassword(email, digestPassword);
        if(user == null){
            ErrorResponse error = new ErrorResponse("Authentication Error: Email Or Password incorrect");
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.UNAUTHORIZED);//You many decide to return HttpStatus.NOT_FOUND
        }

        String token = UUID.randomUUID().toString();
        Session session = new Session(user, token);
        try{
            sessionService.save(session);
        }catch (ConstraintViolationException ex){
            return ConstraintViolationExceptionHandler.getResponse(ex);
        }catch (Exception ex){
            ErrorResponse error = new ErrorResponse("error when try to save session");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }


        return new ResponseEntity<Session>(session, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/sessions", method = RequestMethod.POST)
    public ResponseEntity<?> getSession(@RequestParam String token) {
        Session session = sessionService.findByToken(token);
        return new ResponseEntity<Session>(session, HttpStatus.OK);
    }





}
