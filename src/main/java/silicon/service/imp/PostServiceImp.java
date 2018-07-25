package silicon.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import silicon.model.Client;
import silicon.model.Post;
import silicon.model.Project;
import silicon.repository.PostDAO;
import silicon.service.PostService;

import java.util.List;

@Service
public class PostServiceImp implements PostService {

    @Autowired
    private PostDAO postDAO;


    @Transactional
    public void save(Post post) {
        postDAO.save(post);
    }

    public void delete(Post post){
        post.setDeleted(true);
        postDAO.save(post);
    }

    @Override
    public Post findById(Integer id) {
        return postDAO.findById(id);
    }

    @Override
    public List list(Project project) {
        return postDAO.list(project);
    }


}