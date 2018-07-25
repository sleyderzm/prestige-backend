package silicon.controller.rest;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import silicon.handler.ConstraintViolationExceptionHandler;
import silicon.handler.ErrorResponse;
import silicon.model.Client;
import silicon.model.Role;
import silicon.model.Subscriber;
import silicon.model.User;
import silicon.model.Project;
import silicon.handler.Utils;
import silicon.service.*;

import javax.validation.ConstraintViolationException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    RoleService roleService;

    @Autowired
    UserService userService;

    @Autowired
    SubscriberService subscriberService;

    @Autowired
    ClientService clientService;

    @Autowired
    SessionService sessionService;

    @Autowired
    ProjectService projectService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> listAllUsers() {
        List users = userService.list();
        return new ResponseEntity<List>(users, HttpStatus.OK);
    }

    //-------------------Retrieve Single User--------------------------------------------------------

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@PathVariable("id") Integer id) {
        User user = userService.findById(id);
        if (user == null) {
            ErrorResponse error = new ErrorResponse("User with id " + id + " not found");
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.NOT_FOUND);
        }

        User currentUser = sessionService.getCurrentUser();
        if(currentUser.isSubscriberRole() && !user.getId().equals(currentUser.getId())){
            ErrorResponse error = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/subscriber", method = RequestMethod.GET)
    public ResponseEntity<?> getSubscriber(
            @RequestParam String apiToken
    ) {

        apiToken = Utils.validSringParam(apiToken);

        if(apiToken == null){
            ErrorResponse errorResponse = new ErrorResponse("invalid apiToken");
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        Project project = this.projectService.findByApiToken(apiToken);
        if(project == null){
            ErrorResponse errorResponse = new ErrorResponse("invalid apiToken");
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        User currentUser = sessionService.getCurrentUser();

        Subscriber subscriber = subscriberService.findByProjectAndUser(project, currentUser);

        if(subscriber == null){
            ErrorResponse errorResponse = new ErrorResponse("you have not a subscriber un this project");
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Subscriber>(subscriber, HttpStatus.OK);
    }

    //-------------------Create a User--------------------------------------------------------

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createUser(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String validatePassword,
            @RequestParam Integer roleId,
            @RequestParam(required = false) Integer clientId
    ) {

        User currentUser = sessionService.getCurrentUser();
        if(currentUser.isSubscriberRole()){
            ErrorResponse error = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }

        if(!validatePassword.equals(password)){
            ErrorResponse error = new ErrorResponse("the password and validation do not match");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }


        User repeatUser = userService.findByEmail(email);
        if (repeatUser != null) {
            ErrorResponse error = new ErrorResponse("the user " + email + " already exist");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.CONFLICT);
        }

        Role role = roleService.findById(roleId);
        if (role == null) {
            ErrorResponse error = new ErrorResponse("the Role not exist");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        Client client = Client.getClientFromRequest(clientId, currentUser, clientService);

        if (client == null) {
            ErrorResponse error = new ErrorResponse("the Client is Not Valid");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }


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


    //------------------- Update a User --------------------------------------------------------

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@PathVariable("id") Integer id,
           @RequestParam(required = false) String firstName,
           @RequestParam(required = false) String lastName,
           @RequestParam(required = false) String email) {

        User user = userService.findById(id);

        if (user==null) {
            ErrorResponse error = new ErrorResponse("User with id " + id + " not found");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
        }

        User currentUser = sessionService.getCurrentUser();
        if(currentUser.isSubscriberRole() && !user.getId().equals(currentUser.getId())){
            ErrorResponse error = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }

        if(firstName != null) user.setFirstName(firstName);
        if(lastName != null) user.setLastName(lastName);
        if(email != null) user.setEmail(email);

        try{
            userService.save(user);
        }catch (ConstraintViolationException ex){
            return ConstraintViolationExceptionHandler.getResponse(ex);
        }catch (Exception ex){
            ErrorResponse error = new ErrorResponse("error when try to save user");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }
}
