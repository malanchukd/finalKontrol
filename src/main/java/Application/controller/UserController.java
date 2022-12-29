package Application.controller;

import Application.model.User;
import Application.service.RoleService;
import Application.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {
    Logger logger = LoggerFactory.getLogger(HomeController.class);
    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/create")
    public String create(Model model) {
        logger.info("Create user; getMapping.");
        model.addAttribute("user", new User());
        return "create-user";
    }

    @PostMapping("/create")
    public String create(@Validated @ModelAttribute("user") User user, BindingResult result) {
        logger.info("Create user; postMapping.");
        if (result.hasErrors()) {
            return "create-user";
        }
        user.setPassword(user.getPassword());
        user.setRole(roleService.readById(2));
        User newUser = userService.create(user);
        return "redirect:/todos/all/users/" + newUser.getId();
    }

    @GetMapping("/{id}/read")
    public String read(@PathVariable long id, Model model) {
        logger.info("Read user by ID.");
        User user = userService.readById(id);
        model.addAttribute("user", user);
        return "user-info";
    }

    @GetMapping("/{id}/update")
    public String update(@PathVariable long id, Model model) {
        logger.info("Update user; getMapping.");
        User user = userService.readById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.getAll());
        return "update-user";
    }


    @PostMapping("/{id}/update")
    public String update(@PathVariable long id, Model model, @RequestParam("roleId") long roleId, @Validated @ModelAttribute("user") User user, BindingResult result) {
        logger.info("Update user; postMapping.");
        User oldUser = userService.readById(id);
        if (result.hasErrors()) {
            user.setRole(oldUser.getRole());
            model.addAttribute("roles", roleService.getAll());
            return "update-user";
        }
        if (oldUser.getRole().getName().equals("USER")) {
            user.setRole(oldUser.getRole());
        } else {
            user.setRole(roleService.readById(roleId));
        }
        userService.update(user);
        return "redirect:/users/" + id + "/read";
    }


    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") long id) {
        logger.info("Delete user.");
        userService.delete(id);
        return "redirect:/users/all";
    }

    @GetMapping("/all")
    public String getAll(Model model) {
        logger.info("Get all users.");
        model.addAttribute("users", userService.getAll());
        return "users-list";
    }
}