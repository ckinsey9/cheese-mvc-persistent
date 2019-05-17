package org.launchcode.controllers;


import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.jws.WebParam;
import javax.validation.Valid;

@Controller
@RequestMapping(value= "menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value= "")
    public String index(Model model) {

        model.addAttribute("title", "Menus");
        model.addAttribute("menus", menuDao.findAll());

        return "menu/index";
    }

    @RequestMapping(value= "add", method= RequestMethod.GET)
    public String add(Model model) {
        model.addAttribute(new Menu());
        model.addAttribute("title", "Add Menu");

        return "menu/add";
    }

    @RequestMapping(value="add", method = RequestMethod.POST)
    public String add(@ModelAttribute @Valid Menu newMenu,
                      Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(newMenu);
        return "redirect:view/" + newMenu.getId();
    }

    @RequestMapping(value= "view/{Id}", method= RequestMethod.GET)
    public String viewMenu(@PathVariable("Id") int Id, Model model) {
        Menu aMenu = menuDao.findOne(Id);
        model.addAttribute(aMenu);
        model.addAttribute("title", aMenu.getName());

        return "menu/view";
    }

    @RequestMapping(value="add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(@PathVariable("menuId") int menuId,
                          Model model) {
        Menu aMenu = menuDao.findOne(menuId);

        AddMenuItemForm addMenu = new AddMenuItemForm(aMenu, cheeseDao.findAll());
        model.addAttribute("form", addMenu);
        model.addAttribute("title",
                "Add item to menu: " + aMenu.getName());
        return "menu/add-item";
    }

    @RequestMapping(value= "add-item", method = RequestMethod.POST)
    public String addItem(@ModelAttribute @Valid AddMenuItemForm
                          addMenuItemForm, Errors errors,
                          Model model) {
        if (errors.hasErrors()) {
            return "redirect:add-item/" + addMenuItemForm.getMenuId();
        }

        Cheese addCheese = cheeseDao.findOne(addMenuItemForm.getCheeseId());
        Menu theMenu = menuDao.findOne(addMenuItemForm.getMenuId());

        theMenu.addItem(addCheese);
        menuDao.save(theMenu);
        return "redirect:view/" + theMenu.getId();
    }


}
