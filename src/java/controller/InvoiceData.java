package controller;

import DAO.OrderDAO;
import com.google.gson.Gson;
import hibernate.City;
import hibernate.OrderItems;
import hibernate.Orders;
import hibernate.User;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "InvoiceData", urlPatterns = {"/InvoiceData"})
public class InvoiceData extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("orderId");
//        if (idParam == null || idParam.trim().isEmpty()) {
//            response.sendError(400, "Missing orderId");
//            return;
//        }

        int orderId;
        try {
            if (!idParam.matches("\\d+")) {
                idParam = idParam.replaceAll("\\D+", "");
            }
            orderId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendError(400, "Invalid orderId format");
            return;
        }

        Orders order = orderDAO.getOrderWithItems(orderId);

        if (order == null) {
            response.sendError(404, "Order not found");
            return;
        }

        // Verify logged-in user
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || order.getUser().getId() != user.getId()) {
            response.sendError(403, "Forbidden");
            return;
        }

        InvoiceResponse invoice = new InvoiceResponse();
        invoice.setOrderId(order.getId());
        invoice.setCreatedAt(order.getCreatedAt());

        User userEntity = order.getUser();
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(userEntity.getEmail());
        userDTO.setFirstName(userEntity.getFirst_name());
        userDTO.setLastName(userEntity.getLast_name());
        invoice.setUser(userDTO);

        hibernate.Address addrEntity = order.getAddress();
        AddressDTO addrDTO = new AddressDTO();
        addrDTO.setLineOne(addrEntity.getLineOne());
        addrDTO.setLineTwo(addrEntity.getLineTwo());
        addrDTO.setPostalCode(addrEntity.getPostalCode());
        addrDTO.setMobile(addrEntity.getMobile());

        City city= new City();
        city.setName(addrEntity.getCity().getName());
        addrDTO.setCity(city);

        invoice.setAddress(addrDTO);

        if (!order.getItems().isEmpty() && order.getItems().get(0).getOrderStatus() != null) {
            invoice.setOrderStatus(order.getItems().get(0).getOrderStatus().getValue());
        } else {
            invoice.setOrderStatus("Pending");
        }

        List<InvoiceItem> items = order.getItems().stream().map(item -> {
            InvoiceItem invItem = new InvoiceItem();
            invItem.setProductName(item.getProduct().getName());
            invItem.setQty(item.getQty());
            invItem.setUnitPrice(item.getProduct().getPrice());
            invItem.setColor(item.getProduct().getColor() != null ? item.getProduct().getColor().getValue() : "");
            invItem.setSize(item.getProduct().getSize() != null ? item.getProduct().getSize().getValue() : "");
            invItem.setImageUrl("product-images/" + item.getProduct().getId() + "/image1.png");
            invItem.setItemTotal(item.getItemSubtotal());
            invItem.setDiscount(item.getOrders().getDiscount());
            invItem.setDelivery(item.getDeliveryTypes().getPrice());
            invItem.setGrandTotal(item.getOrders().getGrandTotal());

            return invItem;
        }).collect(Collectors.toList());

        invoice.setItems(items);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = new Gson().toJson(invoice);
        response.getWriter().write(json);
    }

    // Inner classes for JSON mapping
    static class InvoiceResponse {

        private int orderId;
        private java.util.Date createdAt;
        private Object user;
        private Object address;
        private OrderItems orderItems;
        private List<InvoiceItem> items;

        private String orderStatus;

        public int getOrderId() {
            return orderId;
        }

        public void setOrderId(int orderId) {
            this.orderId = orderId;
        }

        public java.util.Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(java.util.Date createdAt) {
            this.createdAt = createdAt;
        }

        public Object getUser() {
            return user;
        }

        public void setUser(Object user) {
            this.user = user;
        }

        public Object getAddress() {
            return address;
        }

        public void setAddress(Object address) {
            this.address = address;
        }

        public List<InvoiceItem> getItems() {
            return items;
        }

        public void setItems(List<InvoiceItem> items) {
            this.items = items;
        }

        public OrderItems getOrderItems() {
            return orderItems;
        }

        public void setOrderItems(OrderItems orderItems) {
            this.orderItems = orderItems;
        }

        public String getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
        }
    }

    static class InvoiceItem {

        private String productName;
        private int qty;
        private double unitPrice;
        private String color;
        private String size;
        private String imageUrl;
        private double itemTotal;
        private double discount;
        private double delivery;
        private double grandTotal;

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public int getQty() {
            return qty;
        }

        public void setQty(int qty) {
            this.qty = qty;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(double unitPrice) {
            this.unitPrice = unitPrice;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public double getItemTotal() {
            return itemTotal;
        }

        public void setItemTotal(double itemTotal) {
            this.itemTotal = itemTotal;
        }

        public double getDiscount() {
            return discount;
        }

        public void setDiscount(double discount) {
            this.discount = discount;
        }

        public double getGrandTotal() {
            return grandTotal;
        }

        public void setGrandTotal(double grandTotal) {
            this.grandTotal = grandTotal;
        }

        public double getDelivery() {
            return delivery;
        }

        public void setDelivery(double delivery) {
            this.delivery = delivery;
        }
    }

    static class AddressDTO {

        private String lineOne;
        private String lineTwo;
        private String postalCode;
        private String mobile;
        private City city; 

        // Getters and setters
        public String getLineOne() {
            return lineOne;
        }

        public void setLineOne(String lineOne) {
            this.lineOne = lineOne;
        }

        public String getLineTwo() {
            return lineTwo;
        }

        public void setLineTwo(String lineTwo) {
            this.lineTwo = lineTwo;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public City getCity() {
            return city;
        }

        public void setCity(City city) {
            this.city = city;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }
    }

    static class UserDTO {

        private String email;
        private String firstName;
        private String lastName;

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }

}
