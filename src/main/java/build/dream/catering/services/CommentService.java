package build.dream.catering.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    @Autowired
    private MongoTemplate mongoTemplate;
}
