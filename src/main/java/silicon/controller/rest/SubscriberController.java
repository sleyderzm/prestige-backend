package silicon.controller.rest;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import silicon.handler.*;
import silicon.model.*;
import silicon.service.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/subscribers")
public class SubscriberController {

    @Autowired
    RoleService roleService;

    @Autowired
    UserService userService;

    @Autowired
    SessionService sessionService;

    @Autowired
    private IMResponseService imResponseService;

    @Autowired
    SubscriberService subscriberService;

    @Autowired
    ProjectService projectService;

    @Autowired
    ClientService clientService;

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void export(
            HttpServletResponse response,
            @RequestParam String apiToken
    ) throws IOException {

        User currentUser = sessionService.getCurrentUser();
        String csvFileName = "subscribers.csv";

        response.setContentType("text/csv");

        // creates mock data
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                csvFileName);
        response.setHeader(headerKey, headerValue);
        Project currentProject = projectService.findByApiToken(apiToken);

        List<Subscriber> subscribers = subscriberService.list(currentProject);

        // uses the Super CSV API to generate CSV data from the model data
        ICsvBeanWriter csvWriter = new CsvBeanWriter2(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);

        String[] header = {"Id","FirstName", "LastName", "Email", "Country", "State", "ExtensionFile",
                "Contribution", "Birthdate", "PublicAddress", "TypeAddress", "DocumentType", "ApiToken",
                "Validated", "Ip", "AwsAccessKeyId", "Status", "CreatedAt", "BillingAddress", "Referr"};

        csvWriter.writeHeader(header);

        if(!currentUser.isSubscriberRole()){
            for (Subscriber subscriber : subscribers) {
                csvWriter.write(subscriber, header);
            }
        }

        csvWriter.close();
    }



    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> listAllSubscriber(
            @RequestParam String apiToken,
            @RequestParam(required = false) Integer currentPage,
            @RequestParam(required = false) Integer perPage
    ) {
        User currentUser = sessionService.getCurrentUser();
        if(currentUser.isSubscriberRole()){
            ErrorResponse error = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }

        Project currentProject = projectService.findByApiToken(apiToken);
        Pagination pagination = new Pagination(currentPage, perPage);
        if(currentProject == null){
            ErrorResponse error = new ErrorResponse("The Project with token " + apiToken + " not exist");
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.NOT_FOUND);
        }

        if (!currentProject.havePermission(currentUser)) {
            ErrorResponse error = new ErrorResponse("You Have Not Permission");
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.NOT_FOUND);
        }

        pagination = subscriberService.list(pagination, currentProject);
        return new ResponseEntity<Pagination>(pagination, HttpStatus.OK);
    }

    //-------------------Retrieve Single Subscriber--------------------------------------------------------

    @RequestMapping(value = "/{apiToken}", method = RequestMethod.GET)
    public ResponseEntity<?> getSubscriber(@PathVariable String apiToken) {
        User currentUser = sessionService.getCurrentUser();
        Subscriber subscriber = subscriberService.findByApiToken(apiToken);

        if (subscriber == null) {
            ErrorResponse error = new ErrorResponse("Subscriber with apiToken " + apiToken + " not found");
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.NOT_FOUND);
        }

        if(currentUser.isSubscriberRole()){
            User userSubscriber = subscriber.getUser();
            if(userSubscriber == null || !userSubscriber.getId().equals(currentUser.getId())){
                ErrorResponse error = new ErrorResponse("You have not permission");
                return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
            }
        }

        if(!subscriber.getProject().havePermission(currentUser)){
            ErrorResponse error = new ErrorResponse("insufficient permissions");
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<Subscriber>(subscriber, HttpStatus.OK);
    }

    //------------------- Update a Subscriber --------------------------------------------------------

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateSubscriber(@PathVariable("id") Integer id,
                                        @RequestParam(required = false) String firstName,
                                        @RequestParam(required = false) String lastName,
                                        @RequestParam(required = false) String email,
                                        @RequestParam(required = false) Long birthdate,
                                        @RequestParam(required = false) String documentType,
                                        @RequestParam(required = false) String addressBitcoin,
                                        @RequestParam(required = false) String addressEthereum,
                                        @RequestParam(required = false) Double contribution,
                                        @RequestParam(required = false) String typeAddress,
                                        @RequestParam(required = false) String publicAddress){

        Subscriber subscriber = subscriberService.findById(id);

        if (subscriber==null) {
            ErrorResponse error = new ErrorResponse("Subscriber with id " + id + " not found");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
        }

        User currentUser = sessionService.getCurrentUser();

        if(currentUser.isSubscriberRole()){
            User userSubscriber = subscriber.getUser();
            if(userSubscriber == null || !userSubscriber.getId().equals(currentUser.getId())){
                ErrorResponse error = new ErrorResponse("You have not permission");
                return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
            }
        }

        if(!subscriber.getProject().havePermission(currentUser)){
            ErrorResponse error = new ErrorResponse("insufficient permissions");
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.UNAUTHORIZED);
        }

        if(firstName != null) subscriber.setFirstName(firstName);
        if(lastName != null) subscriber.setLastName(lastName);
        if(email != null) subscriber.setEmail(email);
        if(birthdate != null) subscriber.setBirthdate(birthdate);
        if(contribution != null) subscriber.setContribution(contribution);
        if(documentType != null) subscriber.setDocumentType(documentType);
        if(publicAddress != null) subscriber.setPublicAddress(publicAddress);
        if(typeAddress != null) subscriber.setTypeAddress(typeAddress);

        try{
            subscriberService.save(subscriber);
        }catch (ConstraintViolationException ex){
            return ConstraintViolationExceptionHandler.getResponse(ex);
        }catch (Exception ex){
            ErrorResponse error = new ErrorResponse("error when try to save subscriber");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Subscriber>(subscriber, HttpStatus.OK);
    }

    @RequestMapping(value = "/update_status/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> updateStatus(
            @PathVariable("id") Integer id,
            @RequestParam(required = false) Integer status
    ){

        Subscriber subscriber = subscriberService.findById(id);

        if (subscriber==null) {
            ErrorResponse error = new ErrorResponse("Subscriber with id " + id + " not found");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
        }

        User currentUser = sessionService.getCurrentUser();
        if(currentUser.isSubscriberRole()){
            ErrorResponse error = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }

        if(!subscriber.getProject().havePermission(currentUser)){
            ErrorResponse error = new ErrorResponse("insufficient permissions");
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.UNAUTHORIZED);
        }

        if(status != null) subscriber.setStatus(status);

        try{
            subscriberService.save(subscriber);
        }catch (ConstraintViolationException ex){
            return ConstraintViolationExceptionHandler.getResponse(ex);
        }catch (Exception ex){
            ErrorResponse error = new ErrorResponse("error when try to save subscriber");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        if(status != Subscriber.STATUS_PENDING){
            MailHandler.sendStatusChange(subscriber);
        }


        return new ResponseEntity<Subscriber>(subscriber, HttpStatus.OK);
    }

    //------------------- Delete a Subscriber --------------------------------------------------------


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable("id") Integer id) {

        Subscriber subscriber = subscriberService.findById(id);

        if (subscriber==null) {
            ErrorResponse error = new ErrorResponse("Subscriber with id " + id + " not found");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
        }

        User currentUser = sessionService.getCurrentUser();

        if(currentUser.isSubscriberRole()){
            ErrorResponse error = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }

        if(!subscriber.getProject().havePermission(currentUser)){
            ErrorResponse error = new ErrorResponse("insufficient permissions");
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.UNAUTHORIZED);
        }

        subscriberService.delete(subscriber);
        MessageResponse message = new MessageResponse("Done");
        return new ResponseEntity<MessageResponse>(message,HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/create_im", method = RequestMethod.POST)
    public ResponseEntity<?> createIM(@PathVariable("id") Integer id) {

        User currentUser = sessionService.getCurrentUser();

        if(!currentUser.isAdminRole()){
            ErrorResponse error = new ErrorResponse("insufficient permissions");
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.UNAUTHORIZED);
        }

        Subscriber subscriber = subscriberService.findById(id);

        if (subscriber==null) {
            ErrorResponse error = new ErrorResponse("Subscriber with id " + id + " not found");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
        }

        //create transaction
        JsonNode resposeIM = IdentityMind.createSubscriberAsConsumer(subscriber);
        if(resposeIM != null){
            String transactionId = resposeIM.get("tid").asText();
            String statusIM = resposeIM.get("state").asText();
            subscriber.setTransactionId(transactionId);
            subscriber.setStatusIM(statusIM);
            subscriberService.save(subscriber);

            //save IMresponse
            IMResponse imResponse = new IMResponse(resposeIM.toString(), subscriber);
            imResponseService.save(imResponse);
        }

        MessageResponse message = new MessageResponse("Done");
        return new ResponseEntity<MessageResponse>(message,HttpStatus.OK);
    }


    @RequestMapping(value = "/subscriberPerDay", method = RequestMethod.GET)
    public ResponseEntity<?> subscriberPerDay(
            @RequestParam Integer days
    ) {
        User currentUser = sessionService.getCurrentUser();

        if(currentUser.isSubscriberRole()){
            ErrorResponse error = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }

        List<Object[]> list = subscriberService.subscriberPerDay(days);
        return new ResponseEntity<List>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "/contributionPerDay", method = RequestMethod.GET)
    public ResponseEntity<?> contributionPerDay(
            @RequestParam Integer days
    ) {
        User currentUser = sessionService.getCurrentUser();

        if(currentUser.isSubscriberRole()){
            ErrorResponse error = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }

        HashMap  list = subscriberService.contributionPerDay(days);
        return new ResponseEntity<HashMap>(list, HttpStatus.OK);
    }
}
