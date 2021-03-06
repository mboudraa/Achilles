package info.archinnov.achilles.test.parser.entity;

import info.archinnov.achilles.annotations.Order;
import java.util.List;

/**
 * CompoundKeyIncorrectType
 * 
 * @author DuyHai DOAN
 * 
 */
public class CompoundKeyIncorrectType
{
    @Order(1)
    private List<String> name;

    @Order(2)
    private int rank;

    public List<String> getName()
    {
        return name;
    }

    public void setName(List<String> name)
    {
        this.name = name;
    }

    public int getRank()
    {
        return rank;
    }

    public void setRank(int rank)
    {
        this.rank = rank;
    }
}
