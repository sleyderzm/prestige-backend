package silicon.controller;

import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import silicon.handler.*;
import silicon.model.*;
import silicon.service.*;
import silicon.thread.IdentityMindThread;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

@RestController
public class ApplicationController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private IMResponseService imResponseService;


    @Autowired
    private SubscriberService subscriberService;

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String validatePassword,
            @RequestParam String clientName
    ) {

        if(!validatePassword.equals(password)){
            ErrorResponse error = new ErrorResponse("the password and validation do not match");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }


        User repeatUser = userService.findByEmail(email);
        if (repeatUser != null) {
            ErrorResponse error = new ErrorResponse("the user " + email + " already exist");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.CONFLICT);
        }


        Client client = new Client(clientName);

        try{
            clientService.save(client);
        }catch (ConstraintViolationException ex){
            return ConstraintViolationExceptionHandler.getResponse(ex);
        }catch (Exception ex){
            ErrorResponse error = new ErrorResponse("error when try to save client");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        Role role = roleService.findById(Role.CLIENT_ID);
        String digestPassword = User.getDigestPassword(password);
        User user = new User(firstName, lastName, email, digestPassword, role, client);

        try{
            userService.save(user);
        }catch (ConstraintViolationException ex){
            return ConstraintViolationExceptionHandler.getResponse(ex);
        }catch (Exception ex){
            ErrorResponse error = new ErrorResponse("error when try to save user");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<User>(user, HttpStatus.CREATED);
    }

    @RequestMapping(value="/create_subscriber",method = RequestMethod.POST)
    public ResponseEntity<?> createSubscriber(
            HttpServletRequest request,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam Long birthdate,
            @RequestParam String apiToken,
            @RequestParam String country,
            //@RequestParam String fingerprint,
            @RequestParam Double contribution,
            @RequestParam(required = false) String publicAddress,
            @RequestParam(required = false) String typeAddress,
            @RequestParam(required = false) String documentType,
            @RequestParam(required = false) String awsAccessKeyId,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String extensionFile,
            @RequestParam(required = false) String billingAddress,
            @RequestParam(required = false) String referr
    ) {

        firstName = Utils.validSringParam(firstName);
        lastName = Utils.validSringParam(lastName);
        email = Utils.validSringParam(email);
        documentType = Utils.validSringParam(documentType);
        publicAddress = Utils.validSringParam(publicAddress);
        typeAddress = Utils.validSringParam(typeAddress);

        apiToken = Utils.validSringParam(apiToken);
        country = Utils.validSringParam(country);
        //fingerprint = Utils.validSringParam(fingerprint);
        awsAccessKeyId = Utils.validSringParam(awsAccessKeyId);
        state = Utils.validSringParam(state);
        extensionFile = Utils.validSringParam(extensionFile);
        billingAddress = Utils.validSringParam(billingAddress);
        referr = Utils.validSringParam(referr);

        List<String> invalidParams = Utils.validateRequiredParams(
                new String[]{"firstName", "lastName", "email", "publicAddress", "typeAddress", "apiToken", "country", /*"fingerprint",*/ "birthdate", "contribution"},
                new Object[]{firstName, lastName, email, publicAddress, typeAddress, apiToken, country, /*fingerprint,*/ birthdate, contribution}
        );

        if(invalidParams != null){
            ErrorResponse error = new ErrorResponse("Invalid params " + invalidParams.toString());
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        if(( typeAddress == null || typeAddress.isEmpty())) {
            ErrorResponse error = new ErrorResponse("Invalid Type Address ");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        if(
            typeAddress == null ||
            typeAddress.isEmpty() ||
            (!typeAddress.equals("etherum_address") && !typeAddress.equals("neo_address") && !typeAddress.equals("bitcoin_address"))
        ) {
            ErrorResponse error = new ErrorResponse("Invalid Address (you need at least one address) ");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        if(!publicAddress.matches("[a-zA-Z0-9]*")){
            ErrorResponse error = new ErrorResponse("Invalid Public Addres");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        if(typeAddress.equals("etherum_address")){
            if(publicAddress.length() == 40){
                publicAddress = "0x" + publicAddress;
            }

            if(publicAddress.length() != 42){
                ErrorResponse error = new ErrorResponse("The public address isn't valid for ETH");
                return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
            }
        }

        if(typeAddress.equals("bitcoin_address") && publicAddress.length() != 34){
            ErrorResponse error = new ErrorResponse("The public address isn't valid for BTC");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        if(typeAddress.equals("neo_address") && publicAddress.length() != 34){
            ErrorResponse error = new ErrorResponse("The public address isn't valid for NEO");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        if(contribution >= Subscriber.BIG_CONTRIBUTION && awsAccessKeyId == null){
            ErrorResponse error = new ErrorResponse("Invalid Document Image");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }



        if(contribution >= Subscriber.BIG_CONTRIBUTION && documentType == null){
            ErrorResponse error = new ErrorResponse("Invalid Document Type");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        Project project = projectService.findByApiToken(apiToken);

        if(project == null){
            ErrorResponse error = new ErrorResponse("Invalid Api Token");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        Subscriber repeatSubscriber = subscriberService.findByEmailAndProject(email, project);
        if (repeatSubscriber != null) {
            ErrorResponse error = new ErrorResponse("the subscriber " + email + " already exist");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.CONFLICT);
        }

        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null || "".equals(ip)) {
            ip = request.getRemoteAddr();
        }

        User user = userService.findByEmail(email);
        boolean isNewUser = false;
        if(user == null){
            isNewUser = true;
            //create as user
            Role role = roleService.findById(Role.SUBSCRIBER_ID);
            user = new User(firstName, lastName, email, null, role, project.getClient());
        }else{
            //update User
            user.setFirstName(firstName);
            user.setLastName(lastName);
        }

        userService.save(user);

        Subscriber subscriber = new Subscriber(firstName, lastName, email, birthdate, publicAddress, typeAddress, documentType, project, awsAccessKeyId, country, state, null, contribution, user, extensionFile, ip, billingAddress, referr);

        try{
            subscriberService.save(subscriber);
        }catch (ConstraintViolationException ex){
            return ConstraintViolationExceptionHandler.getResponse(ex);
        }catch (Exception ex){
            ErrorResponse error = new ErrorResponse("error when try to save subscriber");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        //send validation mail
        if(isNewUser){
            MailHandler.sendSubscribeValidation(subscriber);
        }else{
            MailHandler.sendLoginLink(subscriber);
        }

        //create subscriber as consumer in IM
        IdentityMindThread identityMindThread = new IdentityMindThread(subscriber,this.subscriberService, this.imResponseService);
        identityMindThread.start();

        return new ResponseEntity<Subscriber>(subscriber, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/update_password", method = RequestMethod.POST)
    public ResponseEntity<?> updatePassword(@RequestParam String apiToken,
                                        @RequestParam String password,
                                        @RequestParam String validatePassword) {

        Pattern specailCharPatten = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
        Pattern letterPatten = Pattern.compile("[A-Za-z]");
        Pattern digitCasePatten = Pattern.compile("[0-9 ]");

        if (!password.equals(validatePassword)) {
            ErrorResponse error = new ErrorResponse("The password must have minimum eight characters");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        if(password.length() < 8){
            ErrorResponse error = new ErrorResponse("the password must have 8 characters");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        if (!specailCharPatten.matcher(password).find()) {
            ErrorResponse error = new ErrorResponse("The password must have at least one special character");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }
        if (!letterPatten.matcher(password).find()) {
            ErrorResponse error = new ErrorResponse("The password must have at least one letter");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }
        if (!digitCasePatten.matcher(password).find()) {
            ErrorResponse error = new ErrorResponse("The password must have at least one number");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        String first = password.substring(0,1);
        if(!letterPatten.matcher(first).find()){
            ErrorResponse error = new ErrorResponse("The password must start with a letter");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        Subscriber subscriber = subscriberService.findByApiToken(apiToken);
        if(subscriber == null){
            ErrorResponse error = new ErrorResponse("Subscriber with apiToken " + apiToken + " not found");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
        }

        if(subscriber.getValidated()){
            ErrorResponse error = new ErrorResponse("Subscriber was validated before");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
        }

        User user = subscriber.getUser();

        if (user==null) {
            ErrorResponse error = new ErrorResponse("User not found");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
        }

        if (user.getPassword() != null){
            ErrorResponse error = new ErrorResponse("User was update before");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
        }

        String digestPassword = User.getDigestPassword(password);
        user.setPassword(digestPassword);

        userService.save(user);

        subscriber.setValidated(true);
        subscriberService.save(subscriber);

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

    @RequestMapping(value = "/reset_password", method = RequestMethod.POST)
    public ResponseEntity<?> resetPassword(@RequestParam String tokenResetPassword,
                                            @RequestParam String password,
                                            @RequestParam String validatePassword) {


        Pattern specailCharPatten = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
        Pattern letterPatten = Pattern.compile("[A-Za-z]");
        Pattern digitCasePatten = Pattern.compile("[0-9 ]");

        if (!password.equals(validatePassword)) {
            ErrorResponse error = new ErrorResponse("password and validatePassword not match");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }
        if (password.length() < 8) {
            ErrorResponse error = new ErrorResponse("The password must have minimum eight characters");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }
        if (!specailCharPatten.matcher(password).find()) {
            ErrorResponse error = new ErrorResponse("The password must have at least one special character");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }
        if (!letterPatten.matcher(password).find()) {
            ErrorResponse error = new ErrorResponse("The password must have at least one letter");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }
        if (!digitCasePatten.matcher(password).find()) {
            ErrorResponse error = new ErrorResponse("The password must have at least one number");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        String first = password.substring(0,1);
        if(!letterPatten.matcher(first).find()){
            ErrorResponse error = new ErrorResponse("The password must start with a letter");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        User user = userService.findByTokenResetPassword(tokenResetPassword);;

        if (user==null) {
            ErrorResponse error = new ErrorResponse("Token reset Password not Found");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
        }

        String digestPassword = User.getDigestPassword(password);
        user.setPassword(digestPassword);
        user.setTokenResetPassword(null);
        userService.save(user);

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

    @RequestMapping(value = "/forgot_password", method = RequestMethod.POST)
    public ResponseEntity<?> updatePassword(@RequestParam String email) {
        User user = userService.findByEmail(email);
        if(user == null){
            ErrorResponse error = new ErrorResponse("User with email " + email + " not found");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
        }

        user.setTokenResetPassword(UUID.randomUUID().toString());
        userService.save(user);
        MailHandler.forgotPassword(user);

        return new ResponseEntity<MessageResponse>(new MessageResponse("Done"), HttpStatus.OK);
    }

    @RequestMapping(value = "/validate-password-update", method = RequestMethod.POST)
    public ResponseEntity<?> validatePasswordUpdate(
            @RequestParam String tokenResetPassword
    ) {
        User user = userService.findByTokenResetPassword(tokenResetPassword);

        if (user == null) {
            ErrorResponse error = new ErrorResponse("User with tokenResetPassword " + tokenResetPassword + " not found");
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/subscriber", method = RequestMethod.GET)
    public ResponseEntity<?> getSubscriber(
            @RequestParam String apiToken
    ) {
        Subscriber subscriber = subscriberService.findByApiToken(apiToken);

        if (subscriber == null) {
            ErrorResponse error = new ErrorResponse("Subscriber with apiToken " + apiToken + " not found");
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.NOT_FOUND);
        }

        User user = subscriber.getUser();

        if (user.getPassword() != null) {
            ErrorResponse error = new ErrorResponse("The user have password");
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<Subscriber>(subscriber, HttpStatus.OK);
    }


    @RequestMapping(value = "project/{apiToken}", method = RequestMethod.GET)
    public ResponseEntity<?> getProject(@PathVariable String apiToken) {

        Project project = projectService.findByApiToken(apiToken);
        if (project == null) {
            ErrorResponse error = new ErrorResponse("project with apiToken " + apiToken + " not found");
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Project>(project, HttpStatus.OK);
    }

    @RequestMapping(value = "/callback-merchant", method = RequestMethod.POST)
    public ResponseEntity<?> callbackMerchant(
            HttpServletRequest request
    ) {
        JsonNode body = Utils.getBody(request);
        MessageResponse message = new MessageResponse("Done");
        return new ResponseEntity<MessageResponse>(message,HttpStatus.OK);
    }

    @RequestMapping(value = "/callback-consumer", method = RequestMethod.POST)
    public ResponseEntity<?> callbackConsumer(
            HttpServletRequest request
    ) {

        JsonNode body = Utils.getBody(request);

        if(body.get("tid") == null){
            ErrorResponse errorResponse = new ErrorResponse("Invalid Transaction Id");
            return new ResponseEntity<ErrorResponse>(errorResponse,HttpStatus.BAD_REQUEST);
        }

        String transactionId = body.get("tid").asText();
        Subscriber subscriber = subscriberService.findByTransactionId(transactionId);
        if(subscriber == null){
            ErrorResponse errorResponse = new ErrorResponse("Invalid Transaction Id");
            return new ResponseEntity<ErrorResponse>(errorResponse,HttpStatus.BAD_REQUEST);
        }

        if(body.get("state") == null){
            ErrorResponse errorResponse = new ErrorResponse("Invalid State");
            return new ResponseEntity<ErrorResponse>(errorResponse,HttpStatus.BAD_REQUEST);
        }

        subscriber.setStatusIM(body.get("state").asText());

        if(subscriber.getStatusIM().equals(Subscriber.STATUS_ACCEPTED)){
            subscriber.setStatus(subscriber.getStatusIM());
        }

        try{
            subscriberService.save(subscriber);
        }catch (ConstraintViolationException ex){
            return ConstraintViolationExceptionHandler.getResponse(ex);
        }catch (Exception ex){
            ErrorResponse error = new ErrorResponse("error when try to save subscriber");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        if(subscriber.getStatusIM().equals(Subscriber.STATUS_ACCEPTED)){
            MailHandler.sendStatusChange(subscriber);
        }

        //save IMresponse
        IMResponse imResponse = new IMResponse(body.toString(), subscriber);
        imResponseService.save(imResponse);

        MessageResponse message = new MessageResponse("Done");
        return new ResponseEntity<MessageResponse>(message,HttpStatus.OK);
    }

    @RequestMapping(value = "/callback-transaction", method = RequestMethod.POST)
    public ResponseEntity<?> callbackTransaction(
            HttpServletRequest request
    ) {
        JsonNode body = Utils.getBody(request);

        if(body.get("mid") == null){
            ErrorResponse errorResponse = new ErrorResponse("Invalid Transaction Id");
            return new ResponseEntity<ErrorResponse>(errorResponse,HttpStatus.BAD_REQUEST);
        }

        String transactionId = body.get("mid").asText();
        Subscriber subscriber = subscriberService.findByTransactionId(transactionId);
        if(subscriber == null){
            ErrorResponse errorResponse = new ErrorResponse("Invalid Transaction Id");
            return new ResponseEntity<ErrorResponse>(errorResponse,HttpStatus.BAD_REQUEST);
        }

        if(body.get("decision") == null){
            ErrorResponse errorResponse = new ErrorResponse("Invalid Decision");
            return new ResponseEntity<ErrorResponse>(errorResponse,HttpStatus.BAD_REQUEST);
        }

        subscriber.setStatusIM(body.get("decision").asText());

        if(subscriber.getStatusIM().equals(Subscriber.STATUS_ACCEPTED)){
            subscriber.setStatus(subscriber.getStatusIM());
        }

        try{
            subscriberService.save(subscriber);
        }catch (ConstraintViolationException ex){
            return ConstraintViolationExceptionHandler.getResponse(ex);
        }catch (Exception ex){
            ErrorResponse error = new ErrorResponse("error when try to save subscriber");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        if(subscriber.getStatusIM().equals(Subscriber.STATUS_ACCEPTED)){
            MailHandler.sendStatusChange(subscriber);
        }

        //save IMresponse
        IMResponse imResponse = new IMResponse(body.toString(), subscriber);
        imResponseService.save(imResponse);

        MessageResponse message = new MessageResponse("Done");
        return new ResponseEntity<MessageResponse>(message,HttpStatus.OK);
    }

    @RequestMapping(value = "/redirect_dashboard_subscriber", method = RequestMethod.GET)
    @CrossOrigin(origins = "*")
    public ModelAndView method() {
        String url = System.getenv("FRONT_URL") + "/dashboard/subscriber_project/general?greenPayment=success";
        return new ModelAndView("redirect:" + url);
    }

}



