package silicon.repository.imp;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import silicon.handler.Pagination;
import silicon.model.Project;
import silicon.model.Post;
import silicon.model.User;
import silicon.repository.PostDAO;

import java.util.List;

@Repository
@Transactional
public class PostDAOImp implements PostDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Post post) {
        if(post.getId() != null){
            sessionFactory.getCurrentSession().update(post);
        }else{
            sessionFactory.getCurrentSession().save(post);
        }
    }


    @Override
    public List<Post> list(Project project) {
        Query query;
        Integer projectId = project.getId();
        if(projectId != null){
            query = sessionFactory.getCurrentSession().createQuery("FROM Post WHERE project_id = :projectId AND deleted IS FALSE  ORDER BY created_at DESC")
                    .setParameter("projectId", projectId);
        }else{
            query = sessionFactory.getCurrentSession().createQuery("FROM Post WHERE deleted IS FALSE ORDER BY created_at DESC");
        }
        final List<Post> list = query.list();
        return list;
    }

    @Override
    public Post findById(Integer id) {
        Session session = sessionFactory.getCurrentSession();
        return (Post)session.get(Post.class,id);
    }

}