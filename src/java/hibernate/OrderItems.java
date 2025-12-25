package hibernate;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.google.gson.annotations.Expose;

/**
 *
 * @author pamii
 */
@Entity
@Table(name = "order_items")
public class OrderItems implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Expose
    private int id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @Expose
    private Product product;

    @Column(name = "qty", nullable = false)
    @Expose
    private int qty;

    @ManyToOne
    @JoinColumn(name = "order_status_id")
    @Expose
    private OrderStatus orderStatus;

    @ManyToOne
    @JoinColumn(name = "delivery_type_id")
    @Expose
    private DeliveryTypes deliveryTypes;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "item_subtotal", nullable = false)
    @Expose
    private double itemSubtotal;

    @ManyToOne
    @JoinColumn(name = "orders_id")
    private Orders orders;

    public OrderItems() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public DeliveryTypes getDeliveryTypes() {
        return deliveryTypes;
    }

    public void setDeliveryTypes(DeliveryTypes deliveryTypes) {
        this.deliveryTypes = deliveryTypes;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public double getItemSubtotal() {
        return itemSubtotal;
    }

    public void setItemSubtotal(double itemSubtotal) {
        this.itemSubtotal = itemSubtotal;
    }
}
