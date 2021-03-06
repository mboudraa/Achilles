package info.archinnov.achilles.proxy.wrapper;

import info.archinnov.achilles.context.PersistenceContext;
import info.archinnov.achilles.entity.metadata.PropertyMeta;
import info.archinnov.achilles.entity.operations.EntityProxifier;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * AbstractWrapper
 * 
 * @author DuyHai DOAN
 * 
 */
public abstract class AbstractWrapper
{
    protected Map<Method, PropertyMeta> dirtyMap;
    protected Method setter;
    protected PropertyMeta propertyMeta;
    protected EntityProxifier<PersistenceContext> proxifier;
    protected PersistenceContext context;

    public Map<Method, PropertyMeta> getDirtyMap()
    {
        return dirtyMap;
    }

    public void setDirtyMap(Map<Method, PropertyMeta> dirtyMap)
    {
        this.dirtyMap = dirtyMap;
    }

    public void setSetter(Method setter)
    {
        this.setter = setter;
    }

    public void setPropertyMeta(PropertyMeta propertyMeta)
    {
        this.propertyMeta = propertyMeta;
    }

    protected void markDirty()
    {
        if (!dirtyMap.containsKey(setter))
        {
            dirtyMap.put(setter, propertyMeta);
        }
    }

    public void setProxifier(EntityProxifier<PersistenceContext> proxifier)
    {
        this.proxifier = proxifier;
    }

    protected boolean isJoin()
    {
        return this.propertyMeta.type().isJoin();
    }

    public void setContext(PersistenceContext context)
    {
        this.context = context;
    }

    protected PersistenceContext joinContext(Object joinEntity)
    {
        return context.createContextForJoin(propertyMeta.joinMeta(), joinEntity);
    }
}
