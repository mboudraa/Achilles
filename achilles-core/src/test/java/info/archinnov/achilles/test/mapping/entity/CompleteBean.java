package info.archinnov.achilles.test.mapping.entity;

import info.archinnov.achilles.annotations.Lazy;
import info.archinnov.achilles.annotations.Order;
import info.archinnov.achilles.type.Counter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * CompleteBean
 * 
 * @author DuyHai DOAN
 * 
 */
@Entity
public class CompleteBean
{
    @Id
    private Long id;

    @Column
    private String name;

    private String label;

    @Column(name = "age_in_years")
    private Long age;

    @Lazy
    @Column
    private List<String> friends;

    @Column
    private Set<String> followers;

    @Column
    private Map<Integer, String> preferences;

    @Column
    private Map<Integer, UserBean> usersMap;

    @ManyToOne
    @JoinColumn
    private UserBean user;

    @Column
    private Counter count;

    public CompleteBean() {
    }

    public CompleteBean(Long id) {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public List<String> getFriends()
    {
        return friends;
    }

    public void setFriends(List<String> friends)
    {
        this.friends = friends;
    }

    public Set<String> getFollowers()
    {
        return followers;
    }

    public void setFollowers(Set<String> followers)
    {
        this.followers = followers;
    }

    public Map<Integer, String> getPreferences()
    {
        return preferences;
    }

    public void setPreferences(Map<Integer, String> preferences)
    {
        this.preferences = preferences;
    }

    public Long getAge()
    {
        return age;
    }

    public void setAge(Long age)
    {
        this.age = age;
    }

    public UserBean getUser()
    {
        return user;
    }

    public void setUser(UserBean user)
    {
        this.user = user;
    }

    public Counter getCount()
    {
        return count;
    }

    public void setCount(Counter count) {
        this.count = count;
    }

    public Map<Integer, UserBean> getUsersMap()
    {
        return usersMap;
    }

    public void setUsersMap(Map<Integer, UserBean> usersMap)
    {
        this.usersMap = usersMap;
    }

    public static class UserTweetKey
    {
        @Order(1)
        private String user;

        @Order(2)
        private UUID tweet;

        public UserTweetKey() {
        }

        public UserTweetKey(String user, UUID tweet) {
            super();
            this.user = user;
            this.tweet = tweet;
        }

        public String getUser()
        {
            return user;
        }

        public void setUser(String user)
        {
            this.user = user;
        }

        public UUID getTweet()
        {
            return tweet;
        }

        public void setTweet(UUID tweet)
        {
            this.tweet = tweet;
        }

    }
}
