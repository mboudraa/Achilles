package info.archinnov.achilles.configuration;

/**
 * ThriftConfigurationParameters
 * 
 * @author DuyHai DOAN
 * 
 */
public interface ThriftConfigurationParameters extends ConfigurationParameters
{

	String HOSTNAME_PARAM = "achilles.cassandra.host";
	String CLUSTER_NAME_PARAM = "achilles.cassandra.cluster.name";
	String KEYSPACE_NAME_PARAM = "achilles.cassandra.keyspace.name";

	String CLUSTER_PARAM = "achilles.cassandra.cluster";
	String KEYSPACE_PARAM = "achilles.cassandra.keyspace";

}
