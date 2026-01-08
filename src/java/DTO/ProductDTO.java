package DTO;

import hibernate.Product;

/**
 *
 * @author pamii
 */
public class ProductDTO {

    private int id;
    private int colorId;
    private int categoryId;
    private int typeId;
    private int sizeId;
    private String name;
    private int qty;
    private double price;
    private String description;
    private String weight;
    private String dimension;
    private String material;
    private String category;
    private String color;
    private String size;
    private String type;

    public ProductDTO(Product p) {
        this.id = p.getId();
        this.name = p.getName();
        this.qty = p.getQty();
        this.price = p.getPrice();
        this.description = p.getDescription();
        this.weight = p.getWeight();
        this.dimension = p.getDimension();
        this.material = p.getMaterial();
        this.category = p.getCategory() != null ? p.getCategory().getCategory() : null;
        this.color = p.getColor() != null ? p.getColor().getValue() : null;
        this.size = p.getSize() != null ? p.getSize().getValue() : null;
        this.type = p.getType() != null ? p.getType().getValue() : null;

        this.colorId = p.getColor() != null ? p.getColor().getId() : 0;
        this.categoryId = p.getCategory() != null ? p.getCategory().getId() : 0;
        this.typeId = p.getType() != null ? p.getType().getId() : 0;
        this.sizeId = p.getSize() != null ? p.getSize().getId() : 0;
    }

    public String getName() {
        return name;
    }

    public int getQty() {
        return qty;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getWeight() {
        return weight;
    }

    public String getDimension() {
        return dimension;
    }

    public String getMaterial() {
        return material;
    }

    public String getCategory() {
        return category;
    }

    public String getColor() {
        return color;
    }

    public String getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

}
