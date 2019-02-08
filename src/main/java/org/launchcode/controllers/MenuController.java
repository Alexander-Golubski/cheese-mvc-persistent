package org.launchcode.controllers;

import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model){
        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menu List");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(Model model){
        model.addAttribute(new Menu());
        model.addAttribute("title", "Add Menu");

        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String add(Model model, @ModelAttribute @Valid Menu menu, Errors errors){
        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(menu);
        int thisMenuId = menu.getId();
        return "redirect:view/" + thisMenuId;
    }

    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable(value = "menuId") int id){

        Menu menu = menuDao.findOne(id);
        String menuName = menu.getName();

        model.addAttribute("menu", menu);
        model.addAttribute("title", menuName);

        return "menu/view";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable(value = "menuId") int id){
        model.addAttribute("menu", menuDao.findOne(id));
        model.addAttribute("title", "Add item to menu: " + menuDao.findOne(id).getName());


        AddMenuItemForm newAddMenuItemForm = new AddMenuItemForm(menuDao.findOne(id), cheeseDao.findAll());

        model.addAttribute("form", newAddMenuItemForm);

        return "menu/add-item";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String addItem(Model model, @ModelAttribute @Valid AddMenuItemForm itemForm,
                          @RequestParam int menuId, @RequestParam int cheeseId){

        Menu menu = menuDao.findOne(menuId);
        Cheese theCheese = cheeseDao.findOne(cheeseId);

        menu.addItem(theCheese);

        menuDao.save(menu);

        return "redirect:view/" + menuId;
    }

    @RequestMapping(value = "remove/{menuId}", method = RequestMethod.GET)
    public String displayRemoveMenuForm(Model model, @PathVariable(value = "menuId") int id) {
        model.addAttribute("menu", menuDao.findOne(id));
        model.addAttribute("title", "Remove menu: " + menuDao.findOne(id).getName());

        return "menu/remove";
    }

    @RequestMapping(value = "remove/{menuId}", method = RequestMethod.POST)
    public String processRemoveMenuForm(@PathVariable(value = "menuId") int id) {

        //Menu menu = menuDao.findOne(id);

        menuDao.delete(id);

        return "redirect:/menu";
    }
}

//    Category cat = categoryDao.findOne(categoryId);
//    newCheese.setCategory(cat);
//    cheeseDao.save(newCheese);