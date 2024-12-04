package vn.iotstar.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminController {
	
	@RequestMapping("/admin") 
	public String admin() {
		
		return "layout/index"; // url là /admin thì sẽ mở thư mục admin và chạy file index.html
	}
}
