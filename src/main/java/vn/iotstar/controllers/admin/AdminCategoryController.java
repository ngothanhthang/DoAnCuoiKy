package vn.iotstar.controllers.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.iotstar.entity.Category;
import vn.iotstar.services.CategoryService;

@Controller

@RequestMapping("/admin")
public class AdminCategoryController {
	@Autowired
	private CategoryService categoryService;
	@GetMapping("/category")
	public String index(Model model){
		
		List<Category> list = this.categoryService.getAllCategories();
		model.addAttribute("listCate", list);
		return "admin/category/index";
	}
	
	@GetMapping("/add-category")
	public String add(Model model) {
		
		Category category = new Category();
		category.setStatus(true); // set giá trị mặt định của category status là true 
		model.addAttribute("category", category);
		return "admin/category/add";
	}
	
	@PostMapping("/add-category")
	public String save(@ModelAttribute("category") Category category) {
		try {
			this.categoryService.saveCategory(category);
			return "redirect:/admin/category";
		} catch(Exception e) {
			e.printStackTrace();
		}
		return "admin/category/add";
	}
	
	@GetMapping("/edit-category/{id}")
	public String edit(Model model, @PathVariable("id") Long id) {
	    Optional<Category> optionalCategory = this.categoryService.getCategoryById(id);
	    
	    if (optionalCategory.isPresent()) {
	        Category category = optionalCategory.get();
	        model.addAttribute("category", category);
	        return "admin/category/edit";  // Trả về view chỉnh sửa danh mục
	    } else {
	        // Xử lý trường hợp không tìm thấy danh mục
	        model.addAttribute("error", "Danh mục không tồn tại!");
	        return "admin/category/index";
	    }
	}

	@PostMapping("/edit-category/{id}")
	public String update(@ModelAttribute("category") Category category) {
		try {
			this.categoryService.saveCategory(category);
			return "redirect:/admin/category";
		} catch(Exception e) {
			e.printStackTrace();
		}
		return "admin/category/add";
	}
	
	/* 
	 * @PostMapping("/edit-category/{id}") public String update(@PathVariable("id")
	 * Long id, @ModelAttribute("category") Category category, Model model) {
	 * Optional<Category> optionalCategory =
	 * this.categoryService.getCategoryById(id);
	 * 
	 * if (optionalCategory.isPresent()) { Category existingCategory =
	 * optionalCategory.get(); existingCategory.setName(category.getName());
	 * existingCategory.setStatus(category.isStatus());
	 * existingCategory.setDescription(category.getDescription()); // Thêm các
	 * trường khác cần cập nhật
	 * 
	 * try { this.categoryService.saveCategory(existingCategory); return
	 * "redirect:/admin/category"; // Sau khi lưu, chuyển về trang danh sách }
	 * catch(Exception e) { model.addAttribute("error",
	 * "Có lỗi xảy ra khi cập nhật danh mục!"); e.printStackTrace(); } } else {
	 * model.addAttribute("error", "Danh mục không tồn tại!"); } return
	 * "admin/category/edit"; }
	 */
	
	@GetMapping("/delete-category/{id}")
	public String delete(@PathVariable("id") Long id) {
		try {
			this.categoryService.deleteCategoryById(id);
			return "redirect:/admin/category";
		} catch(Exception e) {
			e.printStackTrace();
		}
		return "redirect:/admin/category";
	}
	
}
