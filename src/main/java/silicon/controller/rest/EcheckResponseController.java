package silicon.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import silicon.handler.*;
import silicon.model.*;
import silicon.service.*;

import javax.validation.ConstraintViolationException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

@RestController
public class EcheckResponseController {

    @Autowired
    SessionService sessionService;

    @Autowired
    SubscriberService subscriberService;

    @Autowired
    ProjectService projectService;

    @Autowired
    EcheckResponseService echeckResponseService;

    @Autowired
    CheckStatusResultService checkStatusResultService;

    @Autowired
    CheckProcessInfoService checkProcessInfoService;

    @Autowired
    OrderService orderService;


    @RequestMapping(value="/update_transaction" ,method = RequestMethod.GET)
    public ResponseEntity<?> updateTransactionGet(
            @RequestParam String ChkID,
            @RequestParam String TransID
    ) {

        String checkId = Utils.validSringParam(ChkID);
        String transactionId = Utils.validSringParam(TransID);

        List<String> invalidParams = Utils.validateRequiredParams(
                new String[]{"Transaction ID", "Check ID"},
                new Object[]{transactionId, checkId}
        );

        if(invalidParams != null){
            ErrorResponse error = new ErrorResponse("Invalid params " + invalidParams.toString());
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        EcheckResponse echeckResponse = echeckResponseService.findByTransactionId(transactionId);

        if(echeckResponse == null){
            ErrorResponse error = new ErrorResponse("Invalid Transaction ID");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        CheckStatusResult checkStatusResult = GreenMoney.getCheckIdStatus(checkId);

        if(checkStatusResult == null){
            ErrorResponse error = new ErrorResponse("Error When Try to get status for transaction, Invalid Check ID");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        CheckProcessInfo checkProcessInfo = GreenMoney.getCheckIdProcessInfo(checkId);

        if(checkProcessInfo == null){
            ErrorResponse error = new ErrorResponse("Error When Try to get information for transaction");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        echeckResponse.setCheckID(checkId);

        try{
            echeckResponseService.save(echeckResponse);
        }catch (ConstraintViolationException ex){
            return ConstraintViolationExceptionHandler.getResponse(ex);
        }catch (Exception ex){
            ErrorResponse error = new ErrorResponse("error when try to save echeck");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        checkStatusResult.setEcheckResponse(echeckResponse);
        checkProcessInfo.setEcheckResponse(echeckResponse);

        try{
            checkStatusResultService.save(checkStatusResult);
        }catch (ConstraintViolationException ex){
            return ConstraintViolationExceptionHandler.getResponse(ex);
        }catch (Exception ex){
            ErrorResponse error = new ErrorResponse("error when try to save echeck status result");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        try{
            checkProcessInfoService.save(checkProcessInfo);
        }catch (ConstraintViolationException ex){
            return ConstraintViolationExceptionHandler.getResponse(ex);
        }catch (Exception ex){
            ErrorResponse error = new ErrorResponse("error when try to save process info");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        User user = echeckResponse.getSubscriber().getUser();
        MailHandler.sendCheckStatusResult(checkStatusResult, user);



        if(checkStatusResult.getResult().equals("0") && checkStatusResult.getVerifyResult().equals("0")){

            Double amountSent = Double.parseDouble(checkProcessInfo.getCheckAmount());
            Order order = new Order(Order.USD, amountSent, null, transactionId, user, Order.PENDING);

            try{
                orderService.save(order);
            }catch (ConstraintViolationException ex){
                return ConstraintViolationExceptionHandler.getResponse(ex);
            }catch (Exception ex){
                ErrorResponse error = new ErrorResponse("error when try to save order");
                return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
            }

            MailHandler.sendCreateOrder(order, user);
        }

        return new ResponseEntity<MessageResponse>(new MessageResponse("Done"), HttpStatus.OK);
    }

    @RequestMapping(value="/api/echeck_responses" ,method = RequestMethod.POST)
    public ResponseEntity<?> createResponse(
            @RequestParam Double amount,
            @RequestParam String apiToken
    ) {

        apiToken = Utils.validSringParam(apiToken);

        List<String> invalidParams = Utils.validateRequiredParams(
                new String[]{"Amount", "Api Token"},
                new Object[]{amount, apiToken}
        );

        if(invalidParams != null){
            ErrorResponse error = new ErrorResponse("Invalid params " + invalidParams.toString());
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        User currentUser = this.sessionService.getCurrentUser();

        if(!currentUser.isSubscriberRole()){
            ErrorResponse error = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }

        Project project = this.projectService.findByApiToken(apiToken);

        if(project == null){
            ErrorResponse error = new ErrorResponse("invalid apiToken");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        Subscriber subscriber = subscriberService.findByProjectAndUser(project, currentUser);

        if(subscriber == null){
            ErrorResponse error = new ErrorResponse("invalid subscriber");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        EcheckResponse echeckResponse = new EcheckResponse(subscriber, amount);

        try{
            echeckResponseService.save(echeckResponse);
        }catch (ConstraintViolationException ex){
            return ConstraintViolationExceptionHandler.getResponse(ex);
        }catch (Exception ex){
            ErrorResponse error = new ErrorResponse("error when try to save echeck");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<EcheckResponse>(echeckResponse, HttpStatus.OK);
    }
}
