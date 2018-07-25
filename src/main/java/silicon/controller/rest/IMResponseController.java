package silicon.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import silicon.handler.ErrorResponse;
import silicon.model.Subscriber;
import silicon.model.User;
import silicon.service.IMResponseService;
import silicon.service.SessionService;
import silicon.service.SubscriberService;

import java.util.List;

@RestController
@RequestMapping("/api/im_responses")
public class IMResponseController {

    @Autowired
    SessionService sessionService;

    @Autowired
    SubscriberService subscriberService;

    @Autowired
    IMResponseService imResponseService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> listResponses(
            @RequestParam String apiToken
    ) {
        Subscriber subscriber = subscriberService.findByApiToken(apiToken);

        if(subscriber == null){
            ErrorResponse errorResponse = new ErrorResponse("Invalid apiToken");
            return new ResponseEntity<ErrorResponse>(errorResponse,HttpStatus.BAD_REQUEST);
        }

        User currentUser = sessionService.getCurrentUser();

        if(currentUser.isSubscriberRole()){
            ErrorResponse error = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }

        if(!subscriber.getProject().havePermission(currentUser)){
            ErrorResponse errorResponse = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(errorResponse,HttpStatus.UNAUTHORIZED);
        }

        List imResponses = imResponseService.list(subscriber);

        return new ResponseEntity<List>(imResponses, HttpStatus.OK);
    }


}
