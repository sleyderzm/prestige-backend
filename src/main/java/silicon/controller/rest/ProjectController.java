package silicon.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import silicon.handler.ConstraintViolationExceptionHandler;
import silicon.handler.ErrorResponse;
import silicon.model.Client;
import silicon.model.Project;
import silicon.model.Role;
import silicon.model.User;
import silicon.service.ClientService;
import silicon.service.ProjectService;
import silicon.service.SessionService;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    SessionService sessionService;

    @Autowired
    ProjectService projectService;

    @Autowired
    ClientService clientService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> listProjects() {
        User currentUser = sessionService.getCurrentUser();
        if(currentUser.isSubscriberRole()){
            ErrorResponse error = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }

        List projects;

        if(currentUser.isAdminRole()){
            projects = projectService.list();
        }else{
            projects = projectService.list(currentUser.getClient());
        }
        return new ResponseEntity<List>(projects, HttpStatus.OK);
    }

    @RequestMapping(value = "/{apiToken}", method = RequestMethod.GET)
    public ResponseEntity<?> getProject(@PathVariable String apiToken) {

        Project project = projectService.findByApiToken(apiToken);
        User currentUser = sessionService.getCurrentUser();
        if (project == null) {
            ErrorResponse error = new ErrorResponse("project with apiToken " + apiToken + " not found");
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.NOT_FOUND);
        }

        if (!project.havePermission(currentUser)) {
            ErrorResponse error = new ErrorResponse("You Have Not Permission");
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Project>(project, HttpStatus.OK);
    }



    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createProject(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String awsAccessKeyId,
            @RequestParam(required = false) String website,
            @RequestParam(required = false) String whitePaper,
            @RequestParam(required = false) Integer clientId
    ) {


        User currentUser = sessionService.getCurrentUser();

        if(currentUser.isSubscriberRole()){
            ErrorResponse error = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }

        Client client = Client.getClientFromRequest(clientId, currentUser, clientService);

        if (client == null) {
            ErrorResponse error = new ErrorResponse("the Client is Not Valid");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        Project project = new Project(name, description, awsAccessKeyId, website, whitePaper, client);

        try{
            projectService.save(project);
        }catch (ConstraintViolationException ex){
            return ConstraintViolationExceptionHandler.getResponse(ex);
        }catch (Exception ex){
            ErrorResponse error = new ErrorResponse("error when try to save project");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Project>(project, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{apiToken}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateProject(@PathVariable String apiToken,
                                        @RequestParam(required = false) String name,
                                        @RequestParam(required = false) String description,
                                        @RequestParam(required = false) String website,
                                        @RequestParam(required = false) String whitePaper,
                                        @RequestParam(required = false) String awsAccessKeyId) {

        Project project = projectService.findByApiToken(apiToken);
        User currentUser = sessionService.getCurrentUser();

        if(currentUser.isSubscriberRole()){
            ErrorResponse error = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }

        if (project==null) {
            ErrorResponse error = new ErrorResponse("Project with apiToken " + apiToken + " not found");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
        }

        if (!project.havePermission(currentUser)) {
            ErrorResponse error = new ErrorResponse("You Have Not Permission");
            return new ResponseEntity<ErrorResponse>(error,HttpStatus.NOT_FOUND);
        }

        if(name != null) project.setName(name);
        if(description != null) project.setDescription(description);
        if(website != null) project.setWebsite(website);
        if(whitePaper != null) project.setWhitePaper(whitePaper);
        if(awsAccessKeyId != null) project.setAwsAccessKeyId(awsAccessKeyId);
        try{
            projectService.save(project);
        }catch (ConstraintViolationException ex){
            return ConstraintViolationExceptionHandler.getResponse(ex);
        }catch (Exception ex){
            ErrorResponse error = new ErrorResponse("error when try to save project");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Project>(project, HttpStatus.OK);
    }




}
