package silicon.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import silicon.handler.ConstraintViolationExceptionHandler;
import silicon.handler.ErrorResponse;
import silicon.handler.MailHandler;
import silicon.handler.Utils;
import silicon.model.Post;
import silicon.model.Project;
import silicon.model.User;
import silicon.service.PostService;
import silicon.service.ProjectService;
import silicon.service.SessionService;

import javax.validation.ConstraintViolationException;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    SessionService sessionService;

    @Autowired
    ProjectService projectService;

    @Autowired
    PostService postService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> listPosts(
            @RequestParam String apiToken
    ) {
        Project project = projectService.findByApiToken(apiToken);

        if(project == null){
            ErrorResponse errorResponse = new ErrorResponse("Invalid apiToken");
            return new ResponseEntity<ErrorResponse>(errorResponse,HttpStatus.BAD_REQUEST);
        }


        User currentUser = sessionService.getCurrentUser();
        if(!project.havePermission(currentUser)){
            ErrorResponse errorResponse = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(errorResponse,HttpStatus.UNAUTHORIZED);
        }

        List posts = postService.list(project);

        return new ResponseEntity<List>(posts, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createPost(
            @RequestParam String content,
            @RequestParam String apiToken
    ) {
        apiToken = Utils.validSringParam(apiToken);
        content = Utils.validSringParam(content);

        List<String> invalidParams = Utils.validateRequiredParams(
                new String[]{"apiToken", "content"},
                new Object[]{apiToken, content}
        );

        User currentUser = sessionService.getCurrentUser();
        if(currentUser.isSubscriberRole()){
            ErrorResponse error = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }

        if(invalidParams != null){
            ErrorResponse error = new ErrorResponse("Invalid params " + invalidParams.toString());
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }


        Project project = projectService.findByApiToken(apiToken);

        if (project == null) {
            ErrorResponse error = new ErrorResponse("the Project is Not Valid");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }


        if(!project.havePermission(currentUser)){
            ErrorResponse errorResponse = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(errorResponse,HttpStatus.UNAUTHORIZED);
        }

        Post post = new Post(project, content);

        try{
            postService.save(post);
        }catch (ConstraintViolationException ex){
            return ConstraintViolationExceptionHandler.getResponse(ex);
        }catch (Exception ex){
            ErrorResponse error = new ErrorResponse("error when try to save post");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        MailHandler.sendNewPost(project);

        return new ResponseEntity<Post>(post, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePost(
            @RequestParam String content,
            @PathVariable Integer id
    ) {
        content = Utils.validSringParam(content);

        List<String> invalidParams = Utils.validateRequiredParams(
                new String[]{"content"},
                new Object[]{content}
        );

        if(invalidParams != null){
            ErrorResponse error = new ErrorResponse("Invalid params " + invalidParams.toString());
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }


        Post post = postService.findById(id);

        if (post == null) {
            ErrorResponse error = new ErrorResponse("the Post is Not Valid");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }


        User currentUser = sessionService.getCurrentUser();

        if(currentUser.isSubscriberRole()){
            ErrorResponse error = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
        }

        if(currentUser.isSubscriberRole()){
            ErrorResponse errorResponse = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(errorResponse,HttpStatus.UNAUTHORIZED);
        }

        if(!post.getproject().havePermission(currentUser)){
            ErrorResponse errorResponse = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(errorResponse,HttpStatus.UNAUTHORIZED);
        }

        post.setContent(content);

        try{
            postService.save(post);
        }catch (ConstraintViolationException ex){
            return ConstraintViolationExceptionHandler.getResponse(ex);
        }catch (Exception ex){
            ErrorResponse error = new ErrorResponse("error when try to save post");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Post>(post, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePost(@PathVariable Integer id) {


        Post post = postService.findById(id);

        if (post == null) {
            ErrorResponse error = new ErrorResponse("the Post is Not Valid");
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
        }


        User currentUser = sessionService.getCurrentUser();

        if(currentUser.isSubscriberRole()){
            ErrorResponse errorResponse = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(errorResponse,HttpStatus.UNAUTHORIZED);
        }

        if(!post.getproject().havePermission(currentUser)){
            ErrorResponse errorResponse = new ErrorResponse("You have not permission");
            return new ResponseEntity<ErrorResponse>(errorResponse,HttpStatus.UNAUTHORIZED);
        }

        postService.delete(post);

        return new ResponseEntity<Post>(post, HttpStatus.OK);
    }


}
