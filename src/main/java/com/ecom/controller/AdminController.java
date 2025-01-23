package com.ecom.controller;

import com.ecom.model.Category;
import com.ecom.service.CategoryServiceIn;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private CategoryServiceIn categoryServiceIn;

    @GetMapping("/")
    public String index() {
        return "admin/index";
    }

    @GetMapping("/loadAddProduct")
    public String loadAddProduct() {
        return "admin/add_product";
    }

    @GetMapping("/category")
    public String category(Model m) {
        m.addAttribute("categories", categoryServiceIn.getAllCategory());
        return "admin/category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {

        String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
        category.setImageName(imageName);

        Boolean existsCategory = categoryServiceIn.existsCategory(category.getName());

        if (existsCategory) {
            session.setAttribute("errorMsg", "Category Name Already Exists");

        } else {
            Category saveCategory = categoryServiceIn.saveCategory(category);
            if (ObjectUtils.isEmpty(saveCategory)) {
                session.setAttribute("errorMsg", "Not Saved : Internal Server Error !!!");
            } else {
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "Category" + File.separator + file.getOriginalFilename());
                System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                session.setAttribute("successMsg", "Saved Successfully !!!");
            }
        }
        return "redirect:/admin/category";
    }


    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id, HttpSession session) {

        Boolean deleteCategory = categoryServiceIn.deleteCategory(id);
        if (deleteCategory) {
            session.setAttribute("successMsg", "category delete success");
        } else {
            session.setAttribute("errorMsg", "Something wrong on server");
        }
        return "redirect:/admin/category";
    }


    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable int id, Model model) {

        model.addAttribute("category", categoryServiceIn.getCategory(id));

        return "/admin/edit_category";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
        Category oldcategory = categoryServiceIn.getCategory(category.getId());
        String imageName = file.isEmpty() ? oldcategory.getImageName() : file.getOriginalFilename();
        if (!ObjectUtils.isEmpty(oldcategory)) {

            oldcategory.setName(category.getName());
            oldcategory.setIsActive(category.getIsActive());
            oldcategory.setImageName(imageName);
        }
        Category updateCategory = categoryServiceIn.saveCategory(oldcategory);
        if (!ObjectUtils.isEmpty(updateCategory)) {
            if(!file.isEmpty()){
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "Category" + File.separator + file.getOriginalFilename());
                System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("successMsg", "Category update success ");
        } else {
            session.setAttribute("errorMsg", "Something went wrong on server ");
        }
        return "redirect:/admin/loadEditCategory/" + category.getId();
    }
}

