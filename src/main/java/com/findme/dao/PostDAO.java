package com.findme.dao;

import com.findme.exception.InternalServerError;
import com.findme.models.Post;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.List;

@Repository("postDAO")
@Transactional
public class PostDAO extends GeneralDAO<Post> {

    private static final String FIND_POST_BY_USER = "SELECT POST_ID FROM POST WHERE ID_USER_POSTED = ? AND ID_USER_PAGE_POSTED = ?";

    @SuppressWarnings("unchecked")
    public boolean findPostByUser(Post post)throws InternalServerError {
        List<Long> idPosts;

        try {
            NativeQuery<Long> query = (NativeQuery<Long>) getEntityManager().createNativeQuery(FIND_POST_BY_USER, Long.class);
            idPosts = query.setParameter(1, post.getUserPosted().getId())
                    .setParameter(2, post.getUserPagePosted().getId()).getResultList();
        }catch (NoResultException e){
            System.err.println(e.getMessage());
            throw e;
        }

        for (Long id : idPosts){
            if (id != null && id.equals(post.getId())){
                return false;
            }
        }
        return true;
    }
}
