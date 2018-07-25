package silicon.repository;

import silicon.model.Project;
import silicon.model.Post;

import java.util.List;

public interface PostDAO {
    void save(Post post);
    List<Post> list(Project project);
    Post findById(Integer id);
}
