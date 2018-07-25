package silicon.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import silicon.handler.ErrorResponse;
import silicon.handler.MessageResponse;
import silicon.handler.S3;
import silicon.handler.Utils;
import silicon.model.Subscriber;
import silicon.model.User;
import silicon.service.*;

import java.net.URL;
import java.util.List;

@RestController
public class S3Controller {
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

    @RequestMapping(value="/api/s3/getSignedURL", method = RequestMethod.GET)
    public ResponseEntity<?> getSignedURL(
            @RequestParam String awsAccessKeyId
    ) {

        awsAccessKeyId = Utils.validSringParam(awsAccessKeyId);

        if(awsAccessKeyId == null){
            ErrorResponse error = new ErrorResponse("awsAccessKeyId is mandatory");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }

        Subscriber subscriber = this.subscriberService.findByAwsAccessKeyId(awsAccessKeyId);

        if(subscriber == null){
            ErrorResponse error = new ErrorResponse("invalid awsAccessKeyId");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }

        User currentUser = sessionService.getCurrentUser();
        if(!currentUser.isAdminRole() && !currentUser.isClientRole()) {
            ErrorResponse error = new ErrorResponse("you have not permission");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }



        URL url = S3.getSignedURL(awsAccessKeyId);
        if(url == null){
            ErrorResponse error = new ErrorResponse("Error when try to connect to s3");
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.INTERNAL_SERVER_ERROR);//You many decide to return HttpStatus.NOT_FOUND
        }

        MessageResponse message = new MessageResponse(url.toString());
        return new ResponseEntity<MessageResponse>(message,HttpStatus.OK);//You many decide to return HttpStatus.NOT_FOUND
    }


    @RequestMapping(value="/s3/putSignedURL", method = RequestMethod.GET)
    public ResponseEntity<?> putSignedURL(
            @RequestParam String contentType
    ) {
        URL url = S3.putSignedURL(contentType);
        if (url == null){
            ErrorResponse error = new ErrorResponse("Error when try to connect to s3");
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.INTERNAL_SERVER_ERROR);
        }

        MessageResponse message = new MessageResponse(url.toString());
        return new ResponseEntity<MessageResponse>(message,HttpStatus.OK);
    }

}
