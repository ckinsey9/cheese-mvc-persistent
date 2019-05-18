package org.launchcode.controllers;

import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.CategoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by LaunchCode
 */
@Controller
@RequestMapping("cheese")
public class CheeseController {

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private CategoryDao categoryDao;

    // Request path: /cheese
    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("cheeses", cheeseDao.findAll());
        model.addAttribute("title", "My Cheeses");

        return "cheese/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddCheeseForm(Model model) {
        model.addAttribute("title", "Add Cheese");
        model.addAttribute(new Cheese());
        model.addAttribute("categories", categoryDao.findAll());
        return "cheese/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddCheeseForm(@ModelAttribute  @Valid Cheese newCheese,
                                       Errors errors, @RequestParam int categoryId,
                                       Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Cheese");
            model.addAttribute("categories", categoryDao.findAll());

            return "cheese/add";
        }
        Category cat = categoryDao.findOne(categoryId);
        newCheese.setCategory(cat);

        cheeseDao.save(newCheese);
        return "redirect:";
    }

    @RequestMapping(value = "remove", method = RequestMethod.GET)
    public String displayRemoveCheeseForm(Model model) {
        model.addAttribute("cheeses", cheeseDao.findAll());
        model.addAttribute("title", "Remove Cheese");
        return "cheese/remove";
    }

    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public String processRemoveCheeseForm(@RequestParam(value = "cheeseIds",
            required = false, defaultValue = "") int[] cheeseIds) {

        for (int cheeseId : cheeseIds) {
            //use cheeseId to search menuDao and remove all instances
            //use a couple for loops to loop through all menus and all cheeses in them?
            cheeseDao.delete(cheeseId);
        }

        return "redirect:";
    }

    @RequestMapping(value= "/category/{categoryId}", method= RequestMethod.GET)
    public String category(@PathVariable("categoryId") int categoryId, Model model) {
        Category cat = categoryDao.findOne(categoryId);


        model.addAttribute("title", cat.getName());
        model.addAttribute("cheeses", cat.getCheeses());
        return "cheese/index";
    }

    @RequestMapping(value="edit/{cheeseId}", method = RequestMethod.GET)
    public String displayEditForm(Model model, @PathVariable int cheeseId) {
        Cheese editCheese = cheeseDao.findOne(cheeseId);
        model.addAttribute("title", "Edit Cheese " +
                editCheese.getName() + "(id=" + editCheese.getId()+ ")");
        model.addAttribute("categories", categoryDao.findAll());

        model.addAttribute(editCheese);
        return "cheese/edit";
    }

    @RequestMapping(value = "edit/{cheeseId}", method = RequestMethod.POST)
    public String processEditForm(@PathVariable int cheeseId,
                                  @ModelAttribute @Valid Cheese editCheese, Errors errors,
                                  @RequestParam
                                  int categoryId, Model model) {

        Cheese updatedCheese = cheeseDao.findOne(cheeseId);


        if (errors.hasErrors()) {

            model.addAttribute("title", "Edit Cheese " +
                    updatedCheese.getName() + "(id=" + updatedCheese.getId()+ ")");
            model.addAttribute("categories", categoryDao.findAll());

            return "cheese/edit";
        }

        updatedCheese.setName(editCheese.getName());
        updatedCheese.setDescription(editCheese.getDescription());
        updatedCheese.setCategory(categoryDao.findOne(categoryId));
        cheeseDao.save(updatedCheese);

        return "redirect:/cheese";
    }

}
