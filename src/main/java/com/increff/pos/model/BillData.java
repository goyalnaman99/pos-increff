
package com.increff.pos.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

@XmlRootElement(name="BillData")
public class BillData {

    private int orderId;
    private List<OrderItemData> orderItemData;
    private Date datetime;
    private double billAmount;
    private String name;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public List<OrderItemData> getOrderItemData() {
        return orderItemData;
    }

    public void setOrderItemData(List<OrderItemData> orderItemData) {
        this.orderItemData = orderItemData;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public double getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(double billAmount) {
        this.billAmount = billAmount;
    }
}