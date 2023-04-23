package com.example.springsecurityapplication.controllers;

import com.example.springsecurityapplication.enumm.Status;
import com.example.springsecurityapplication.models.*;
import com.example.springsecurityapplication.repositories.CategoryRepository;
import com.example.springsecurityapplication.repositories.OrderRepository;
import com.example.springsecurityapplication.repositories.PersonRepository;
import com.example.springsecurityapplication.security.PersonDetails;
import com.example.springsecurityapplication.services.OrderService;
import com.example.springsecurityapplication.services.PersonService;
import com.example.springsecurityapplication.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
public class AdminController {

    private static final String[] VALID_ROLES = { "ROLE_USER", "ROLE_ADMIN" };
    private final ProductService productService;

    @Value("${upload.path}")
    private String uploadPath;

    private final CategoryRepository categoryRepository;
    private final OrderService orderService;
    private final PersonService personService;

    public AdminController(
            ProductService productService, CategoryRepository categoryRepository,
            OrderService orderService, PersonService personService
    ) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
        this.orderService = orderService;
        this.personService = personService;
    }

    @GetMapping("admin/orders")
    public String viewOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("statuses", Status.values());
        return "admin/view-orders";
    }

    @GetMapping("admin/orders/search")
    public String searchForOrder(Model model, @RequestParam String q) {
        if (q == null || q.isEmpty()) model.addAttribute("orders", orderService.getAllOrders());
        else {
            // Accessing member 'size' is forbidden for type 'class java.util.Collections$EmptyList' in Thymeleaf expressions
            // Accessing member 'size' is forbidden for type 'class java.util.ImmutableCollections$List12' in Thymeleaf expressions
            List<Order> ordersResult = new LinkedList<>();
            Order searchOrderResult = orderService.getByNumberPostfix(q);
            if (searchOrderResult != null) ordersResult.add(searchOrderResult);
            model.addAttribute("orders", ordersResult);
        }
        model.addAttribute("statuses", Status.values());
        return "admin/view-orders";
    }

    @PostMapping("admin/order/update-status/{id}")
    public String updateOrderStatus(@PathVariable int id, @RequestParam String orderStatus) {
        // отправлен существующий статус на апи
        if (Status.values().length != 0
                && Arrays.stream(Status.values()).anyMatch(t -> t.name().equals(orderStatus))) {
            Order order = orderService.getById(id);
            // если отправленный статус уже равен текущему, изменения не требуются
            if (!Objects.equals(order.getStatus().toString(), orderStatus)) {
                order.setStatus(Status.valueOf(orderStatus));
                orderService.update(order);
            }
        }
        return "redirect:/admin/orders";
    }

    @GetMapping("admin/users")
    public String viewUsers(Model model) {
        model.addAttribute("users", personService.findAll());
        model.addAttribute("roles", VALID_ROLES);
        return "admin/users";
    }

    @PostMapping("admin/user/update-role/{id}")
    public String updateUserRole(@PathVariable int id, @RequestParam String userRole) {
        if (VALID_ROLES.length != 0
                && Arrays.asList(VALID_ROLES).contains(userRole)) {
            Person person = personService.getById(id);
            if (!Objects.equals(person.getRole(), userRole)) {
                person.setRole(userRole);
                personService.update(person);
                int currentUserId = ((PersonDetails) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal()).getPerson().getId();
                if (currentUserId == person.getId()) return "redirect:/logout";
            }
        }
        return "redirect:/admin/users";
    }

    @GetMapping("admin/product/add")
    public String addProduct(Model model){
        model.addAttribute("product", new Product());
        model.addAttribute("category", categoryRepository.findAll());
        return "product/addProduct";
    }

    @PostMapping("/admin/product/add")
    public String addProduct(@ModelAttribute("product") @Valid Product product, BindingResult bindingResult, @RequestParam("file_one")MultipartFile file_one, @RequestParam("file_two")MultipartFile file_two, @RequestParam("file_three")MultipartFile file_three, @RequestParam("file_four")MultipartFile file_four, @RequestParam("file_five")MultipartFile file_five, @RequestParam("category") int category, Model model) throws IOException {
        Category category_db = (Category) categoryRepository.findById(category).orElseThrow();
        System.out.println(category_db.getName());
        if(bindingResult.hasErrors()){
            model.addAttribute("category", categoryRepository.findAll());
            return "product/addProduct";
        }

        if(file_one != null){
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file_one.getOriginalFilename();
            file_one.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);

        }

        if(file_two != null){
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file_two.getOriginalFilename();
            file_two.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }

        if(file_three != null){
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file_three.getOriginalFilename();
            file_three.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }

        if(file_four != null){
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file_four.getOriginalFilename();
            file_four.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }

        if(file_five != null){
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file_five .getOriginalFilename();
            file_five .transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }
        productService.saveProduct(product, category_db);
        return "redirect:/admin";
    }


    @GetMapping("/admin")
    public String admin(Model model)
    {
        model.addAttribute("products", productService.getAllProduct());
        return "admin";
    }

    @GetMapping("admin/product/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id){
        productService.deleteProduct(id);
        return "redirect:/admin";
    }

    @GetMapping("admin/product/edit/{id}")
    public String editProduct(Model model, @PathVariable("id") int id){
        model.addAttribute("product", productService.getProductId(id));
        model.addAttribute("category", categoryRepository.findAll());
        return "product/editProduct";
    }

    @PostMapping("admin/product/edit/{id}")
    public String editProduct(@ModelAttribute("product") @Valid Product product, BindingResult bindingResult, @PathVariable("id") int id, Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("category", categoryRepository.findAll());
            return "product/editProduct";
        }
        productService.updateProduct(id, product);
        return "redirect:/admin";
    }

}

