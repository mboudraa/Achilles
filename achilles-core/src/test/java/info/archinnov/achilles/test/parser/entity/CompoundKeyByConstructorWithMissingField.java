package info.archinnov.achilles.test.parser.entity;

import info.archinnov.achilles.annotations.Order;
import javax.persistence.Column;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class CompoundKeyByConstructorWithMissingField {

    @Column(name = "primaryKey")
    private Long id;

    @JsonCreator
    public CompoundKeyByConstructorWithMissingField(@JsonProperty("name") @Order(2) String name,
            @JsonProperty("id") @Order(1) Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

}
