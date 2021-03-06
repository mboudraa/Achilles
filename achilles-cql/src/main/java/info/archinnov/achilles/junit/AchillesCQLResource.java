package info.archinnov.achilles.junit;

import static info.archinnov.achilles.embedded.AchillesEmbeddedServer.CASSANDRA_TEST_KEYSPACE_NAME;
import info.archinnov.achilles.embedded.CQLEmbeddedServer;
import info.archinnov.achilles.entity.manager.CQLEntityManager;
import info.archinnov.achilles.entity.manager.CQLEntityManagerFactory;
import org.apache.commons.lang.StringUtils;
import com.datastax.driver.core.Session;

/**
 * AchillesCQLResource
 * 
 * @author DuyHai DOAN
 * 
 */
public class AchillesCQLResource extends AchillesTestResource {

    private final CQLEntityManagerFactory factory;
    private final CQLEntityManager em;
    private final CQLEmbeddedServer server;
    private final Session session;

    /**
     * Initialize a new embedded Cassandra server
     * 
     * @param entityPackages
     *            packages to scan for entity discovery, comma separated
     * @param tables
     *            list of tables to truncate before and after tests
     */
    public AchillesCQLResource(String entityPackages, String... tables) {
        super(tables);
        if (StringUtils.isEmpty(entityPackages))
            throw new IllegalArgumentException("Entity packages should be provided");

        server = new CQLEmbeddedServer(true, entityPackages, CASSANDRA_TEST_KEYSPACE_NAME);
        factory = server.getEmf();
        em = server.getEm();
        session = em.getNativeSession();
    }

    /**
     * Initialize a new embedded Cassandra server
     * 
     * @param entityPackages
     *            packages to scan for entity discovery, comma separated
     * 
     * @param cleanUpSteps
     *            when to truncate tables for clean up. Possible values are : Steps.BEFORE_TEST, Steps.AFTER_TEST and
     *            Steps.BOTH (Default value) <br/>
     * <br/>
     * 
     * @param tables
     *            list of tables to truncate before, after or before and after tests, depending on the 'cleanUpSteps'
     *            parameters
     */
    public AchillesCQLResource(String entityPackages, Steps cleanUpSteps, String... tables) {
        super(cleanUpSteps, tables);
        if (StringUtils.isEmpty(entityPackages))
            throw new IllegalArgumentException("Entity packages should be provided");

        server = new CQLEmbeddedServer(true, entityPackages, CASSANDRA_TEST_KEYSPACE_NAME);
        factory = server.getEmf();
        em = server.getEm();
        session = em.getNativeSession();
    }

    /**
     * Return a singleton CQLEntityManagerFactory
     * 
     * @return
     *         CQLEntityManagerFactory singleton
     */
    public CQLEntityManagerFactory getFactory() {
        return factory;
    }

    /**
     * Return a singleton CQLEntityManager
     * 
     * @return
     *         CQLEntityManager singleton
     */
    public CQLEntityManager getEm() {
        return em;
    }

    /**
     * Return a native CQL3 Session
     * 
     * @return
     *         native CQL3 Session
     */
    public Session getNativeSession()
    {
        return session;
    }

    @Override
    protected void truncateTables() {
        if (tables != null)
        {
            for (String table : tables)
            {
                server.truncateTable(table);
            }
        }
    }

}
