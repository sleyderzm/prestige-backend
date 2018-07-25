package silicon.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import silicon.handler.ConstraintViolationExceptionHandler;
import silicon.handler.ErrorResponse;
import silicon.model.Client;
import silicon.model.User;
import silicon.service.ClientService;
import silicon.service.SessionService;

import javax.persistence.Column;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    SessionService sessionService;

    @Autowired
    ClientService clientService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> listClients() {
        User currentUser = sessionService.getCurrentUser();
        List clients = new ArrayList();

        if(currentUser.isSubscriberRole()){
            ErrorResponse error = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }

        if(currentUser.isAdminRole()){
            clients = clientService.list();
        }else{
            clients.add(currentUser.getClient());
        }
        return new ResponseEntity<List>(clients, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public ResponseEntity<?> getClient(
            @PathVariable Integer id
    ) {
        Client client = clientService.findById(id);

        User currentUser = sessionService.getCurrentUser();
        if(currentUser.isSubscriberRole() ){
            Client currentClient = currentUser.getClient();
            if(currentClient == null || !currentClient.getId().equals(client.getId())){
                ErrorResponse error = new ErrorResponse("You have not permission");
                return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
            }
        }


        if (client==null) {
            ErrorResponse error = new ErrorResponse("Client with id " + id + " not found");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
        }

        if(!client.havePermission(currentUser)){
            ErrorResponse errorResponse = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(errorResponse,HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<Client>(client, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateClient(@PathVariable("id") Integer id,
                                        @RequestParam(required = false) String name,
                                        @RequestParam(required = false) String backgroundColor,
                                        @RequestParam(required = false) String acceptedColor,
                                        @RequestParam(required = false) String rejectedColor,
                                        @RequestParam(required = false) String pendingColor,
                                        @RequestParam(required = false) String formTitleColor,
                                        @RequestParam(required = false) String fontColor) {

        Client client = clientService.findById(id);

        if (client==null) {
            ErrorResponse error = new ErrorResponse("Client with id " + id + " not found");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
        }

        User currentUser = sessionService.getCurrentUser();

        if(currentUser.isSubscriberRole()){
            ErrorResponse error = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }

        if(!client.havePermission(currentUser)){
            ErrorResponse errorResponse = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(errorResponse,HttpStatus.UNAUTHORIZED);
        }

        if(name != null) client.setName(name);
        if(backgroundColor != null) client.setBackgroundColor(backgroundColor);
        if(acceptedColor != null) client.setAcceptedColor(acceptedColor);
        if(rejectedColor != null) client.setRejectedColor(rejectedColor);
        if(pendingColor != null) client.setPendingColor(pendingColor);
        if(formTitleColor != null) client.setFormTitleColor(formTitleColor);
        if(fontColor != null) client.setFontColor(fontColor);

        try{
            clientService.save(client);
        }catch (ConstraintViolationException ex){
            return ConstraintViolationExceptionHandler.getResponse(ex);
        }catch (Exception ex){
            ErrorResponse error = new ErrorResponse("error when try to save client");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Client>(client, HttpStatus.OK);
    }


}
