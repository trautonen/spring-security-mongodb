package org.eluder.spring.security.mongodb.authentication;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import java.util.Date;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

public class MongoTokenRepositoryImpl implements PersistentTokenRepository {
    
    private final MongoOperations mongo;

    public MongoTokenRepositoryImpl(final MongoOperations mongo) {
        this.mongo = mongo;
    }

    @Override
    public void createNewToken(final PersistentRememberMeToken token) {
        final PersistentMongoToken mongoToken = new PersistentMongoToken(token);
        mongo.insert(mongoToken);
    }

    @Override
    public void updateToken(final String series, final String tokenValue, final Date lastUsed) {
        final Query query = query(where("series").is(series));
        final Update update = update("tokenValue", tokenValue).set("lastUsed", lastUsed);
        mongo.updateFirst(query, update, PersistentMongoToken.class);
    }

    @Override
    public PersistentMongoToken getTokenForSeries(final String series) {
        final Query query = query(where("series").is(series));
        return mongo.findOne(query, PersistentMongoToken.class);
    }

    @Override
    public void removeUserTokens(final String username) {
        final Query query = query(where("username").is(username));
        mongo.remove(query, PersistentMongoToken.class);
    }
}
