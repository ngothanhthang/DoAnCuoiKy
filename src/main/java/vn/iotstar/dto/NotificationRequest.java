package vn.iotstar.dto;

public class NotificationRequest 
{
	private String orderId;  // Mã đơn hàng
    public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	private String shipperId;  // ID của shipper
	public String getShipperId() {
		return shipperId;
	}
	public void setShipperId(String shipperId) {
		this.shipperId = shipperId;
	}
}
