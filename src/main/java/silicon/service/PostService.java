package silicon.service;

import silicon.model.Post;
import silicon.model.Project;

import java.util.List;

public interface PostService {
    void save(Post post);
    List list(Project project);
    Post findById(Integer id);
    void delete(Post post);
}
