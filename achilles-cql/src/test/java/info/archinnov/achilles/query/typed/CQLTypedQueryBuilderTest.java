package info.archinnov.achilles.query.typed;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import info.archinnov.achilles.context.CQLDaoContext;
import info.archinnov.achilles.context.CQLPersistenceContext;
import info.archinnov.achilles.context.CQLPersistenceContextFactory;
import info.archinnov.achilles.entity.CQLEntityMapper;
import info.archinnov.achilles.entity.metadata.EntityMeta;
import info.archinnov.achilles.entity.metadata.PropertyMeta;
import info.archinnov.achilles.entity.metadata.PropertyType;
import info.archinnov.achilles.entity.operations.CQLEntityProxifier;
import info.archinnov.achilles.test.builders.PropertyMetaTestBuilder;
import info.archinnov.achilles.test.mapping.entity.CompleteBean;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.SimpleStatement;

/**
 * CQLTypedQueryBuilderTest
 * 
 * @author DuyHai DOAN
 * 
 */

@RunWith(MockitoJUnitRunner.class)
public class CQLTypedQueryBuilderTest {

    private CQLTypedQueryBuilder<CompleteBean> builder;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CQLDaoContext daoContext;

    @Mock
    private CQLEntityMapper mapper;

    @Mock
    private CQLEntityProxifier proxifier;

    @Mock
    private CQLPersistenceContextFactory contextFactory;

    @Mock
    private CQLPersistenceContext context;

    @Mock
    private Row row;

    @Captor
    private ArgumentCaptor<Set<Method>> alreadyLoadedCaptor;

    private Class<CompleteBean> entityClass = CompleteBean.class;

    private CompleteBean entity = new CompleteBean();

    @Before
    public void setUp() throws Exception
    {

    }

    @Test
    public void should_get_all_managed_with_select_star() throws Exception
    {
        PropertyMeta idMeta = PropertyMetaTestBuilder
                .completeBean(Void.class, Long.class)
                .field("id")
                .type(PropertyType.ID)
                .accessors()
                .build();

        PropertyMeta nameMeta = PropertyMetaTestBuilder
                .completeBean(Void.class, String.class)
                .field("name")
                .type(PropertyType.SIMPLE)
                .accessors()
                .build();

        EntityMeta meta = buildEntityMeta(idMeta, nameMeta);

        String queryString = "select * from test";
        initBuilder(queryString, meta, meta.getPropertyMetas(), true);

        when(daoContext.execute(any(SimpleStatement.class)).all()).thenReturn(Arrays.asList(row));
        when(mapper.mapRowToEntity(eq(entityClass), eq(row), any(Map.class))).thenReturn(entity);
        when(contextFactory.newContext(entity)).thenReturn(context);
        when(proxifier.buildProxy(eq(entity), eq(context), alreadyLoadedCaptor.capture())).thenReturn(entity);

        List<CompleteBean> actual = builder.get();

        assertThat(actual).containsExactly(entity);

        assertThat(alreadyLoadedCaptor.getValue()).contains(idMeta.getGetter(), nameMeta.getGetter());
    }

    @Test
    public void should_get_all_managed_with_normal_select() throws Exception
    {
        PropertyMeta idMeta = PropertyMetaTestBuilder
                .completeBean(Void.class, Long.class)
                .field("id")
                .type(PropertyType.ID)
                .accessors()
                .build();

        PropertyMeta nameMeta = PropertyMetaTestBuilder
                .completeBean(Void.class, String.class)
                .field("name")
                .type(PropertyType.SIMPLE)
                .accessors()
                .build();

        PropertyMeta ageMeta = PropertyMetaTestBuilder
                .completeBean(Void.class, Long.class)
                .field("age")
                .type(PropertyType.SIMPLE)
                .accessors()
                .build();

        EntityMeta meta = buildEntityMeta(idMeta, nameMeta, ageMeta);

        String queryString = " select id, name   from  test";
        initBuilder(queryString, meta, meta.getPropertyMetas(), true);

        when(daoContext.execute(any(SimpleStatement.class)).all()).thenReturn(Arrays.asList(row));
        when(mapper.mapRowToEntity(eq(entityClass), eq(row), any(Map.class))).thenReturn(entity);
        when(contextFactory.newContext(entity)).thenReturn(context);
        when(proxifier.buildProxy(eq(entity), eq(context), alreadyLoadedCaptor.capture())).thenReturn(entity);

        List<CompleteBean> actual = builder.get();

        assertThat(actual).containsExactly(entity);

        assertThat(alreadyLoadedCaptor.getValue()).contains(idMeta.getGetter(), nameMeta.getGetter());
    }

    @Test
    public void should_get_all_skipping_null_entity() throws Exception
    {
        EntityMeta meta = buildEntityMeta();
        initBuilder("select * from test", meta, meta.getPropertyMetas(), true);

        when(daoContext.execute(any(SimpleStatement.class)).all()).thenReturn(Arrays.asList(row));
        when(mapper.mapRowToEntity(eq(entityClass), eq(row), any(Map.class))).thenReturn(null);

        List<CompleteBean> actual = builder.get();

        assertThat(actual).isEmpty();
    }

    @Test
    public void should_get_all_raw_entities() throws Exception
    {
        PropertyMeta idMeta = PropertyMetaTestBuilder
                .completeBean(Void.class, Long.class)
                .field("id")
                .type(PropertyType.ID)
                .accessors()
                .build();

        PropertyMeta nameMeta = PropertyMetaTestBuilder
                .completeBean(Void.class, String.class)
                .field("name")
                .type(PropertyType.SIMPLE)
                .accessors()
                .build();

        EntityMeta meta = buildEntityMeta(idMeta, nameMeta);

        String queryString = "select * from test";
        initBuilder(queryString, meta, meta.getPropertyMetas(), false);

        when(daoContext.execute(any(SimpleStatement.class)).all()).thenReturn(Arrays.asList(row));
        when(mapper.mapRowToEntity(eq(entityClass), eq(row), any(Map.class))).thenReturn(entity);

        List<CompleteBean> actual = builder.get();

        assertThat(actual).containsExactly(entity);

        verifyZeroInteractions(contextFactory, proxifier);
    }

    @Test
    public void should_get_first_managed_entity() throws Exception
    {
        PropertyMeta idMeta = PropertyMetaTestBuilder
                .completeBean(Void.class, Long.class)
                .field("id")
                .type(PropertyType.ID)
                .accessors()
                .build();

        PropertyMeta nameMeta = PropertyMetaTestBuilder
                .completeBean(Void.class, String.class)
                .field("name")
                .type(PropertyType.SIMPLE)
                .accessors()
                .build();

        EntityMeta meta = buildEntityMeta(idMeta, nameMeta);

        String queryString = "select id from test";
        initBuilder(queryString, meta, meta.getPropertyMetas(), true);

        when(daoContext.execute(any(SimpleStatement.class)).one()).thenReturn(row);
        when(mapper.mapRowToEntity(eq(entityClass), eq(row), any(Map.class))).thenReturn(entity);
        when(contextFactory.newContext(entity)).thenReturn(context);
        when(proxifier.buildProxy(eq(entity), eq(context), alreadyLoadedCaptor.capture())).thenReturn(entity);

        CompleteBean actual = builder.getFirst();

        assertThat(actual).isSameAs(entity);
        assertThat(alreadyLoadedCaptor.getValue()).contains(idMeta.getGetter());
    }

    @Test
    public void should_get_first_raw_entity() throws Exception
    {
        PropertyMeta idMeta = PropertyMetaTestBuilder
                .completeBean(Void.class, Long.class)
                .field("id")
                .type(PropertyType.ID)
                .accessors()
                .build();

        PropertyMeta nameMeta = PropertyMetaTestBuilder
                .completeBean(Void.class, String.class)
                .field("name")
                .type(PropertyType.SIMPLE)
                .accessors()
                .build();

        EntityMeta meta = buildEntityMeta(idMeta, nameMeta);
        String queryString = "select id from test";
        initBuilder(queryString, meta, meta.getPropertyMetas(), false);

        when(daoContext.execute(any(SimpleStatement.class)).one()).thenReturn(row);
        when(mapper.mapRowToEntity(eq(entityClass), eq(row), any(Map.class))).thenReturn(entity);

        CompleteBean actual = builder.getFirst();

        assertThat(actual).isSameAs(entity);

        verifyZeroInteractions(contextFactory, proxifier);
    }

    @Test
    public void should_return_null_when_null_row() throws Exception
    {
        EntityMeta meta = buildEntityMeta();
        String queryString = "select id from test";
        initBuilder(queryString, meta, meta.getPropertyMetas(), false);
        when(daoContext.execute(any(SimpleStatement.class)).one()).thenReturn(null);
        CompleteBean actual = builder.getFirst();

        assertThat(actual).isNull();

        verifyZeroInteractions(contextFactory, proxifier);

    }

    @Test
    public void should_return_null_when_cannot_map_entity() throws Exception
    {
        EntityMeta meta = buildEntityMeta();
        String queryString = "select id from test";
        initBuilder(queryString, meta, meta.getPropertyMetas(), false);
        when(daoContext.execute(any(SimpleStatement.class)).one()).thenReturn(row);
        when(mapper.mapRowToEntity(eq(entityClass), eq(row), any(Map.class))).thenReturn(null);

        CompleteBean actual = builder.getFirst();

        assertThat(actual).isNull();

        verifyZeroInteractions(contextFactory, proxifier);

    }

    private EntityMeta buildEntityMeta(PropertyMeta... pms) {
        EntityMeta meta = new EntityMeta();
        Map<String, PropertyMeta> propertyMetas = new HashMap<String, PropertyMeta>();
        List<Method> eagerMetas = new ArrayList<Method>();
        for (PropertyMeta pm : pms)
        {
            propertyMetas.put(pm.getPropertyName(), pm);
            eagerMetas.add(pm.getGetter());
        }

        meta.setPropertyMetas(propertyMetas);
        meta.setEagerGetters(eagerMetas);
        return meta;
    }

    private void initBuilder(String queryString, EntityMeta meta, Map<String, PropertyMeta> propertyMetas,
            boolean managed) {
        builder = new CQLTypedQueryBuilder<CompleteBean>(entityClass, daoContext, queryString, meta, contextFactory,
                managed);

        Whitebox.setInternalState(builder, String.class, queryString);
        Whitebox.setInternalState(builder, Class.class, (Object) entityClass);
        Whitebox.setInternalState(builder, Map.class, propertyMetas);
        Whitebox.setInternalState(builder, CQLEntityMapper.class, mapper);
        Whitebox.setInternalState(builder, CQLPersistenceContextFactory.class, contextFactory);
        Whitebox.setInternalState(builder, CQLEntityProxifier.class, proxifier);
    }
}
