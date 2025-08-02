package hibernate;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "size")
public class Size implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "value", length = 20, nullable = false)
    private String value;

//    @ManyToMany(mappedBy = "sizes")
//    private Set<Product> products = new HashSet<>();
    public Size() {
    }

    public Size(String value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
//
//    public Set<Product> getProducts() {
//        return products;
//    }
//
//    public void setProducts(Set<Product> products) {
//        this.products = products;
//    }
}
