package info.archinnov.achilles.table;

import static com.datastax.driver.core.DataType.*;
import static info.archinnov.achilles.counter.AchillesCounter.*;
import static info.archinnov.achilles.cql.CQLTypeMapper.toCQLType;
import info.archinnov.achilles.entity.metadata.EntityMeta;
import info.archinnov.achilles.entity.metadata.PropertyMeta;
import info.archinnov.achilles.validation.Validator;
import java.util.List;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ColumnMetadata;
import com.datastax.driver.core.DataType.Name;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.TableMetadata;

/**
 * CQLTableValidator
 * 
 * @author DuyHai DOAN
 * 
 */
public class CQLTableValidator {

    private Cluster cluster;
    private String keyspaceName;

    public CQLTableValidator(Cluster cluster, String keyspaceName) {
        this.cluster = cluster;
        this.keyspaceName = keyspaceName;
    }

    public void validateForEntity(EntityMeta entityMeta, TableMetadata tableMetadata) {
        PropertyMeta idMeta = entityMeta.getIdMeta();
        if (entityMeta.isClusteredCounter())
        {

        }
        validateTable(entityMeta, tableMetadata, idMeta);

    }

    private void validateTable(EntityMeta entityMeta, TableMetadata tableMetadata, PropertyMeta idMeta) {
        if (idMeta.isEmbeddedId())
        {
            List<String> componentNames = idMeta.getComponentNames();
            List<Class<?>> componentClasses = idMeta.getComponentClasses();
            for (int i = 0; i < componentNames.size(); i++)
            {
                validateColumn(tableMetadata, componentNames.get(i).toLowerCase(), componentClasses.get(i));
            }
        }
        else
        {
            validateColumn(tableMetadata, idMeta);
        }

        for (PropertyMeta pm : entityMeta.getAllMetasExceptIdMeta())
        {
            switch (pm.type())
            {
                case SIMPLE:
                case LAZY_SIMPLE:
                case JOIN_SIMPLE:
                    validateColumn(tableMetadata, pm);
                    break;
                case LIST:
                case SET:
                case MAP:
                case LAZY_LIST:
                case LAZY_SET:
                case LAZY_MAP:
                case JOIN_LIST:
                case JOIN_SET:
                case JOIN_MAP:
                    validateCollectionAndMapColumn(tableMetadata, pm);
                    break;
                default:
                    break;
            }
        }
    }

    public void validateAchillesCounter() {
        KeyspaceMetadata keyspaceMetadata = cluster.getMetadata().getKeyspace(keyspaceName);
        TableMetadata tableMetadata = keyspaceMetadata.getTable(CQL_COUNTER_TABLE);
        Validator.validateTableTrue(tableMetadata != null, "Cannot find table '%s' from keyspace '%s'",
                CQL_COUNTER_TABLE, keyspaceName);

        ColumnMetadata fqcnColumn = tableMetadata.getColumn(CQL_COUNTER_FQCN);
        Validator.validateTableTrue(fqcnColumn != null, "Cannot find column '%s' from table '%s'", CQL_COUNTER_FQCN,
                CQL_COUNTER_TABLE);
        Validator.validateTableTrue(fqcnColumn.getType() == text(), "Column '%s' of type '%s' should be of type '%s'"
                , CQL_COUNTER_FQCN, fqcnColumn.getType(), text());

        ColumnMetadata pkColumn = tableMetadata.getColumn(CQL_COUNTER_PRIMARY_KEY);
        Validator.validateTableTrue(pkColumn != null, "Cannot find column '%s' from table '%s'",
                CQL_COUNTER_PRIMARY_KEY, CQL_COUNTER_TABLE);

        Validator.validateTableTrue(pkColumn.getType() == text(), "Column '%s' of type '%s' should be of type '%s'",
                CQL_COUNTER_PRIMARY_KEY, pkColumn.getType(), text());

        ColumnMetadata propertyNameColumn = tableMetadata.getColumn(CQL_COUNTER_PROPERTY_NAME);
        Validator.validateTableTrue(propertyNameColumn != null, "Cannot find column '%s' from table '%s'",
                CQL_COUNTER_PROPERTY_NAME, CQL_COUNTER_TABLE);
        Validator.validateTableTrue(propertyNameColumn.getType() == text(),
                "Column '%s' of type '%s' should be of type '%s'", CQL_COUNTER_PROPERTY_NAME,
                propertyNameColumn.getType(), text());

        ColumnMetadata counterValueColumn = tableMetadata.getColumn(CQL_COUNTER_VALUE);
        Validator.validateTableTrue(counterValueColumn != null, "Cannot find column '%s' from table '%s'",
                counterValueColumn, CQL_COUNTER_TABLE);
        Validator.validateTableTrue(counterValueColumn.getType() == counter(),
                "Column '%s' of type '%s' should be of type '%s'", counterValueColumn, counterValueColumn.getType(),
                counter());

    }

    private void validateColumn(TableMetadata tableMetadata, PropertyMeta pm)
    {
        if (pm.isJoin())
        {
            validateColumn(tableMetadata, pm.getPropertyName().toLowerCase(), pm.joinIdMeta().getValueClass());
        }
        else
        {
            validateColumn(tableMetadata, pm.getPropertyName().toLowerCase(), pm.getValueClass());
        }
    }

    private void validateColumn(TableMetadata tableMetadata, String columnName, Class<?> columnJavaType)
    {
        String tableName = tableMetadata.getName();
        ColumnMetadata columnMetadata = tableMetadata.getColumn(columnName);
        Name expectedType = toCQLType(columnJavaType);

        Validator.validateTableTrue(columnMetadata != null, "Cannot find column '%s' in the table '%s'", columnName,
                tableName);

        Name realType = columnMetadata.getType().getName();
        Validator.validateTableTrue(expectedType == realType,
                "Column '%s' of table '%s' of type '%s' should be of type '%s' indeed", columnName, tableName,
                realType, expectedType);
    }

    private void validateCollectionAndMapColumn(TableMetadata tableMetadata, PropertyMeta pm)
    {
        String columnName = pm.getPropertyName().toLowerCase();
        String tableName = tableMetadata.getName();
        ColumnMetadata columnMetadata = tableMetadata.getColumn(columnName);

        Validator.validateTableTrue(columnMetadata != null, "Cannot find column '%s' in the table '%s'", columnName,
                tableName);
        Name realType = columnMetadata.getType().getName();
        Name expectedValueType;
        if (pm.isJoin())
        {
            expectedValueType = toCQLType(pm.joinIdMeta().getValueClass());
        }
        else
        {
            expectedValueType = toCQLType(pm.getValueClass());
        }

        switch (pm.type())
        {
            case LIST:
                Validator.validateTableTrue(realType == Name.LIST,
                        "Column '%s' of table '%s' of type '%s' should be of type '%s' indeed", columnName,
                        tableName, realType, Name.LIST);
                Name realListValueType = columnMetadata.getType().getTypeArguments().get(0).getName();
                Validator.validateTableTrue(
                        realListValueType == expectedValueType,
                        "Column '%s' of table '%s' of type 'List<%s>' should be of type 'List<%s>' indeed",
                        columnName, tableName, realListValueType, expectedValueType);

                break;
            case SET:
                Validator.validateTableTrue(realType == Name.SET,
                        "Column '%s' of table '%s' of type '%s' should be of type '%s' indeed", columnName,
                        tableName, realType, Name.SET);
                Name realSetValueType = columnMetadata.getType().getTypeArguments().get(0).getName();

                Validator.validateTableTrue(
                        realSetValueType == expectedValueType,
                        "Column '%s' of table '%s' of type 'Set<%s>' should be of type 'Set<%s>' indeed", columnName,
                        tableName, realSetValueType, expectedValueType);
                break;
            case MAP:
                Validator.validateTableTrue(realType == Name.MAP,
                        "Column '%s' of table '%s' of type '%s' should be of type '%s' indeed", columnName,
                        tableName, realType, Name.MAP);

                Name expectedMapKeyType = toCQLType(pm.getKeyClass());
                Name realMapKeyType = columnMetadata.getType().getTypeArguments().get(0).getName();
                Name realMapValueType = columnMetadata.getType().getTypeArguments().get(1).getName();
                Validator.validateTableTrue(
                        realMapKeyType == expectedMapKeyType,
                        "Column %s' of table '%s' of type 'Map<%s,?>' should be of type 'Map<%s,?>' indeed",
                        columnName, tableName, realMapKeyType, expectedMapKeyType);

                Validator.validateTableTrue(
                        realMapValueType == expectedValueType,
                        "Column '%s' of table '%s' of type 'Map<?,%s>' should be of type 'Map<?,%s>' indeed",
                        columnName,
                        tableName, realMapValueType, expectedValueType);
                break;
            default:
                break;
        }
    }

}
