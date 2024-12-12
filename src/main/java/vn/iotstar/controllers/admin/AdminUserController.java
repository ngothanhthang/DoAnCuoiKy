package vn.iotstar.controllers.admin;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.services.UserService;

@Controller
@RequestMapping("/admin")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/admin_users")
    public String index(@RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "keyword", required = false) String keyword,
                        Model model) {
        Page<User> userPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            userPage = userService.searchUsers(keyword, page);
            model.addAttribute("keyword", keyword);
        } else {
            userPage = userService.getUsers(page);
        }
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("currentPage", page);
        return "admin/user/index";
    }


    @GetMapping("/add-user")
    public String add(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "admin/user/add";
    }

    @PostMapping("/add-user")
    public String addUser(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          @RequestParam("email") String email,
                          @RequestParam("role") String role,
                          @RequestParam("isActive") Boolean isActive) {
        try {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);

            // Lấy đối tượng Role từ cơ sở dữ liệu dựa trên tên role
            Role userRole = roleRepository.findByName(role);
            System.out.println("User Role: " + userRole);
            if (userRole != null) {
                user.setRole(userRole);
            } else {
                throw new IllegalArgumentException("Invalid role: " + role);
            }

            user.setIsActive(isActive);

            userService.saveUser(user);
            return "redirect:/admin/admin_users";
        } catch (Exception e) {
            e.printStackTrace();
            return "admin/user/add";
        }
    }

    @GetMapping("/edit-user/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "admin/user/edit";
        }
        return "redirect:/admin/admin_users";
    }

    @PostMapping("/edit-user/{id}")
    public String updateUser(@PathVariable Long id,
                              @RequestParam("username") String username,
                              @RequestParam("password") String password,
                              @RequestParam("email") String email,
                              @RequestParam("role") Role role,
                              @RequestParam("isActive") Boolean isActive) {
        try {
            User user = userService.getUserById(id).orElseThrow(() -> new RuntimeException("User not found"));
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
			user.setRole(role);
            user.setIsActive(isActive);

            userService.saveUser(user);
            return "redirect:/admin/admin_users";
        } catch (Exception e) {
            e.printStackTrace();
            return "admin/user/edit";
        }
    }

    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            return "redirect:/admin/admin_users";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/admin_users";
        }
    }
}