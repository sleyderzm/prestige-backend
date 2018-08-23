package silicon.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import silicon.handler.*;
import silicon.model.*;
import silicon.service.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    SessionService sessionService;

    @Autowired
    OrderService orderService;

    @Autowired
    CoinService coinService;

    @Autowired
    ProjectService projectService;

    @Autowired
    SubscriberService subscriberService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> listOrders(
            @RequestParam(required = false) Integer currentPage,
            @RequestParam(required = false) Integer perPage
    ) {
        User currentUser = sessionService.getCurrentUser();
        if(currentUser.isSubscriberRole()){
            ErrorResponse error = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }

        Pagination pagination = new Pagination(currentPage, perPage);

        pagination = orderService.list(pagination);
        return new ResponseEntity<Pagination>(pagination, HttpStatus.OK);
    }

    @RequestMapping(value="/my" ,method = RequestMethod.GET)
    public ResponseEntity<?> listMyOrders(
            @RequestParam(required = false) Integer currentPage,
            @RequestParam(required = false) Integer perPage
    ) {
        User currentUser = sessionService.getCurrentUser();
        if(!currentUser.isSubscriberRole()){
            ErrorResponse error = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }

        Pagination pagination = new Pagination(currentPage, perPage);

        pagination = orderService.list(pagination, currentUser);
        return new ResponseEntity<Pagination>(pagination, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getOrder(@PathVariable Long id) {

        Order order = orderService.findById(id);
        if (order == null) {
            ErrorResponse error = new ErrorResponse("order with id " + id + " not found");
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.NOT_FOUND);
        }

        User currentUser = sessionService.getCurrentUser();
        if(currentUser.isSubscriberRole()){
            if(!currentUser.getId().equals(order.getUser().getId())){
                ErrorResponse error = new ErrorResponse("You have not permission");
                return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
            }
        }

        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void export(HttpServletResponse response) throws IOException {

        User currentUser = sessionService.getCurrentUser();
        String csvFileName = "orders.csv";

        response.setContentType("text/csv");

        // creates mock data
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                csvFileName);
        response.setHeader(headerKey, headerValue);

        List<Order> orders = orderService.list();

        // uses the Super CSV API to generate CSV data from the model data
        ICsvBeanWriter csvWriter = new CsvBeanWriter2(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);


        String[] header = {"FullName","Email", "PaymentMethod", "AmountSent",
                "AmountToken", "WalletAddress", "TransactionId", "createdAt"};

        csvWriter.writeHeader(header);

        if(!currentUser.isSubscriberRole()){
            for (Order order : orders) {
                csvWriter.write(order, header);
            }
        }

        csvWriter.close();
    }


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createOrder(
            @RequestParam String paymentMethod,
            @RequestParam Double amountSent,
            @RequestParam String walletAddress,
            @RequestParam String transactionId,
            @RequestParam String apiToken
    ) {

        paymentMethod = Utils.validSringParam(paymentMethod);
        walletAddress = Utils.validSringParam(walletAddress);
        transactionId = Utils.validSringParam(transactionId);
        apiToken = Utils.validSringParam(apiToken);

        List<String> invalidParams = Utils.validateRequiredParams(
                new String[]{"Payment Method", "Amount Sent", "Wallet Address", "Transaction Id", "Project Token"},
                new Object[]{paymentMethod, amountSent, walletAddress, transactionId, apiToken}
        );

        if(invalidParams != null){
            ErrorResponse error = new ErrorResponse("Invalid params " + invalidParams.toString());
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        if(!paymentMethod.equals("USD") && !paymentMethod.equals("NEO") && !paymentMethod.equals("ETH") && !paymentMethod.equals("BTC")){
            ErrorResponse error = new ErrorResponse("Invalid Payment Method");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        User currentUser = sessionService.getCurrentUser();

        if(!currentUser.isSubscriberRole()){
            ErrorResponse error = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }

        if(paymentMethod.equals(Order.NEO) && transactionId.length() == 64){
            transactionId = "0x" + transactionId.toLowerCase();
        }else if(paymentMethod.equals(Order.ETH) && transactionId.length() == 64){
            transactionId = "0x" + transactionId.toLowerCase();
        }

        Order orderRepeat = orderService.findByTransactionIdAndPaymentMethod(transactionId, paymentMethod);
        if(orderRepeat != null && (orderRepeat.getStatusCode().equals(Order.ACCEPTED) || orderRepeat.getStatusCode().equals(Order.PENDING))){
            ErrorResponse error = new ErrorResponse("Order with this transactionId already exists");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        Project project = this.projectService.findByApiToken(apiToken);
        if(project == null){
            ErrorResponse errorResponse = new ErrorResponse("invalid apiToken");
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Subscriber subscriber = subscriberService.findByProjectAndUser(project, currentUser);

        if(!paymentMethod.equals("USD")){

            if(
               (subscriber.getTypeAddress().equals("etherum_address") && !paymentMethod.equals("ETH")) ||
               (subscriber.getTypeAddress().equals("neo_address") && !paymentMethod.equals("NEO")) ||
               (subscriber.getTypeAddress().equals("bitcoin_address") && !paymentMethod.equals("BTC"))
            ){
                ErrorResponse error = new ErrorResponse("Invalid Payment Method");
                return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
            }

            if(!subscriber.getPublicAddress().toUpperCase().equals(walletAddress.toUpperCase())){
                ErrorResponse error = new ErrorResponse("the wallet doesn't registered");
                return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
            }

        }

        Coin coin = coinService.findBySymbol(paymentMethod);
        Order order = new Order(coin, amountSent, walletAddress, transactionId, currentUser, Order.PENDING);

        try{
            orderService.save(order);
        }catch (ConstraintViolationException ex){
            return ConstraintViolationExceptionHandler.getResponse(ex);
        }catch (Exception ex){
            ErrorResponse error = new ErrorResponse("error when try to save order");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        MailHandler.sendCreateOrder(order, currentUser);

        return new ResponseEntity<Order>(order, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/balance", method = RequestMethod.POST)
    public ResponseEntity<?> getBalance(
    ) {
        User currentUser = sessionService.getCurrentUser();

        if(!currentUser.isSubscriberRole()){
            ErrorResponse error = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }

        Double balance = orderService.balance(currentUser);

        if(balance == null){
            balance = 0.0;
        }

        return new ResponseEntity<Double>(balance, HttpStatus.OK);
    }
}
