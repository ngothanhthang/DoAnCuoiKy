package vn.iotstar.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeControllerHieu {

    @GetMapping("/")
    public String homePage(Model model) {
        // Thêm dữ liệu vào model (nếu cần)
        model.addAttribute("userName", "Trung Hếu");
        return "index"; // Trả về view index.html
    }
}
