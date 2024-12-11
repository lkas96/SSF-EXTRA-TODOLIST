package sg.nus.iss.ssf.extra.controller;

import java.text.ParseException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;
import sg.nus.iss.ssf.extra.model.Todo;
import sg.nus.iss.ssf.extra.service.TodoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/listing")
public class TodoController {
    @Autowired
    TodoService tds;

    @GetMapping(path = { "", "all" })
    public String dispalyAllTodos(Model model) {
        List<Todo> todolist = tds.getEntries();
        model.addAttribute("todolist", todolist);

        return "listing";
    }

    // http://localhost:5123/listing/filter?filter=pending
    // http://localhost:5123/listing/filter?filter=all
    // http://localhost:5123/listing/filter?filter=whateverOption
    @GetMapping("/filter")
    public String filterBy(@RequestParam String filter, Model model) {
        List<Todo> filteredlist = tds.getFiltered(filter);
        model.addAttribute("todolist", filteredlist);
        return "listing";
    }

    @GetMapping("/add")
    public String displayTodoForm(Model model) {
        // Instantiate new todo object, means id also generated
        // Object only has ID and no other stuff
        Todo td = new Todo();

        // Now passed a todo with only these 3 attributes
        // Form will fill the remaining and pass all 8 on submit for creation
        model.addAttribute("todo", td);

        return "add";
    }

    //USING MULTI VALUE MAP FROM FORM DATA PASSED TO CONTROLLER
    // @PostMapping("/add")
    // public String postCreateForm(@Valid @RequestBody MultiValueMap<String,
    // String> formData, BindingResult result,
    // Model model) throws ParseException {

    // // THIS METHOD HAS NO FORM VALIDATION
    // // SOMEHOW IT DOES NOT CHECK EVEN WITH THE @VALID TAG
    // // I dont know how to make it work

    // String id = formData.getFirst("id");
    // String name = formData.getFirst("name");
    // String desc = formData.getFirst("desc");
    // String due = formData.getFirst("due");
    // String prior = formData.getFirst("prior");
    // String status = formData.getFirst("status");

    // // Set current time and date and add to object
    // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    // String formattedDueDateString = formatter.format(due);
    // Date dueDate = formatter.parse(formattedDueDateString);

    // Date nowLiveDate = new Date();
    // String formattedDateString = formatter.format(nowLiveDate);
    // Date createdDate = formatter.parse(formattedDateString);

    // // First entry, created date same as updated Date
    // Date updatedDate = createdDate;

    // Todo td = new Todo(id, name, desc, dueDate, prior, status, createdDate,
    // updatedDate);

    // tds.createEntry(td);

    // return "redirect:/listing";
    // }


    //USING OBJECT MODEL ATTRIBUTE TO PASS TO THE CONTROLLER
    @PostMapping("/add")
    public String postCreateForm(@Valid @ModelAttribute("todo") Todo todo, BindingResult result, Model model){
        if (result.hasErrors()) {
            System.out.println(result.getAllErrors());
            return "add"; // Return to the form view if there are validation errors
        }

        //Pass the todo object into the create method
        tds.createEntry(todo);

        return "redirect:/listing";
    }

    @GetMapping("/update/{todo-id}")
    public String getMethodName(@PathVariable("todo-id") String todoId, Model model) {
        Todo found = tds.findById(todoId);
        model.addAttribute("todo", found);

        return "update";
    }

    @PostMapping("/update")
    public String postUpdateForm(@Valid @ModelAttribute("todo") Todo todo, BindingResult result, Model model) throws ParseException {

        if (result.hasErrors()) {
            System.out.println(result.getAllErrors());
            return "update"; // Return to the form view if there are validation errors
        }

        //Pass the todo object into the Service layer for processing
        //Update the new date into redis
        tds.updateTodo(todo);

        return "redirect:/listing";
    }

    @GetMapping("/delete/{todo-id}")
    public String deleteTodo(@PathVariable("todo-id") String todoId) {
        tds.deleteTodo(todoId);

        return "redirect:/listing";
    }

}
