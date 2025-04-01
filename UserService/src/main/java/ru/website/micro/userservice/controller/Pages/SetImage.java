package ru.website.micro.userservice.controller.Pages;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Controller()
public class SetImage {
    @GetMapping("/set-image/{id}")
    public String setImage(@PathVariable UUID id, Model model)
    {
        model.addAttribute("id",id);
        return "index";
    }
    @GetMapping("/get-image/{id}")
    public String getImage(@PathVariable UUID id, Model model)
    {
        model.addAttribute("id",id);
        return "index";
    }
}
