package org.eluder.spring.security.mongodb.authentication;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import com.mongodb.MongoClient;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

public class MongoTokenRepositoryImplTest {
    
    private static MongoTemplate mongo;
    
    private MongoTokenRepositoryImpl mongoTokenRepository;
    private PersistentRememberMeToken fixture;
    
    @BeforeClass
    public static void init() throws Exception {
        mongo = new MongoTemplate(new MongoClient(), "spring_sec_mongodb_test");
    }
    
    @AfterClass
    public static void destroy() throws Exception {
        mongo.getDb().dropDatabase();
        mongo = null;
    }
    
    @Before
    public void before() {
        mongo.remove(new Query(), PersistentMongoToken.class);
        mongoTokenRepository = new MongoTokenRepositoryImpl(mongo);
        fixture = new PersistentMongoToken("user", "12345", "abcdefg", new Date());
    }
    
    @Test
    public void testCreateNewToken() throws Exception {
        mongoTokenRepository.createNewToken(fixture);
        List<PersistentMongoToken> tokens = mongo.findAll(PersistentMongoToken.class);
        assertEquals(1, tokens.size());
        assertEquals("12345", tokens.get(0).getSeries());
    }

    @Test
    public void testUpdateToken() throws Exception {
        mongoTokenRepository.createNewToken(fixture);
        mongoTokenRepository.updateToken(fixture.getSeries(), "XYZFG", new Date());
        List<PersistentMongoToken> tokens =  mongo.findAll(PersistentMongoToken.class);
        assertEquals(1, tokens.size());
        assertEquals("XYZFG", tokens.get(0).getTokenValue());
        
    }

    @Test
    public void testGetTokenForSeries() throws Exception {
        mongoTokenRepository.createNewToken(fixture);
        PersistentRememberMeToken token = mongoTokenRepository.getTokenForSeries("12345");
        assertEquals(fixture.getTokenValue(), token.getTokenValue());
    }

    @Test
    public void testRemoveUserTokens() throws Exception {
        mongoTokenRepository.createNewToken(new PersistentMongoToken("user", "123", "abc", new Date()));
        mongoTokenRepository.createNewToken(new PersistentMongoToken("user", "456", "efg", new Date()));
        mongoTokenRepository.removeUserTokens("user");
        List<PersistentMongoToken> tokens = mongo.findAll(PersistentMongoToken.class);
        assertEquals(0, tokens.size());
    }
}
