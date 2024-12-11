package vn.iotstar.controllers;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import vn.iotstar.entity.Order;
import vn.iotstar.services.OrderService;
import vn.iotstar.services.impl.BankQRService;
import vn.iotstar.services.impl.QRCodeService;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private OrderService orderService; // Service xử lý đơn hàng

    @GetMapping("/qr")
    public String showPaymentQR(@RequestParam Long orderId, 
                              @RequestParam BigDecimal amount, 
                              Model model) {
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);
        return "payment_qr";
    }

    @GetMapping("/check-status")
    @ResponseBody
    public Map<String, String> checkPaymentStatus(@RequestParam Long orderId) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Giả lập việc kiểm tra thanh toán - trong thực tế sẽ tích hợp với API của cổng thanh toán
            Order order = orderService.getOrderById(orderId);
            
            // Ví dụ: cập nhật trạng thái khi nhận được callback từ cổng thanh toán
            if (order != null) {
                order.setStatus("Đã xác nhận");
                orderService.save(order);
                response.put("status", "completed");
            } else {
                response.put("status", "pending");
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    @Autowired
    private QRCodeService qrCodeService;
    @Autowired
    private BankQRService bankQRService;
    
    @GetMapping("/generate-bank-qr")
    public ResponseEntity<byte[]> generateBankQR(@RequestParam BigDecimal amount,
                                               @RequestParam String orderId) {
        // Tạo nội dung QR
        String qrContent = bankQRService.generateQRContent(amount, orderId);
        
        // Tạo mã QR
        byte[] qrImage = qrCodeService.generateQRCode(qrContent, 300, 300);
        
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrImage);
    }
    @GetMapping("/test-qr")
    @ResponseBody
    public String testGenerateQR() {
        try {
            // Test với số tiền 100,000 VND
            BigDecimal testAmount = new BigDecimal("100000");
            String testOrderId = "TEST123";
            
            String qrContent = bankQRService.generateQRContent(testAmount, testOrderId);
            byte[] qrImage = qrCodeService.generateQRCode(qrContent, 300, 300);
            String base64Image = Base64.getEncoder().encodeToString(qrImage);
            
            return "<html><body>" +
                   "<h3>Test QR Code</h3>" +
                   "<p>Amount: 100,000 VND</p>" +
                   "<p>Order ID: TEST123</p>" +
                   "<img src='data:image/png;base64," + base64Image + "'>" +
                   "<p>QR Content: " + qrContent + "</p>" +
                   "<p>Quét mã này bằng app Mobile Banking để test</p>" +
                   "</body></html>";
                   
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}