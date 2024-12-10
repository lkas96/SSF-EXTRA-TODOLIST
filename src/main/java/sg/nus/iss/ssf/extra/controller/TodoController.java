package sg.nus.iss.ssf.extra.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;
import sg.nus.iss.ssf.extra.model.Todo;
import sg.nus.iss.ssf.extra.service.TodoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    @PostMapping("/add")
    public String postCreateForm(@Valid @RequestBody MultiValueMap<String, String> formData, BindingResult result, Model model) {

        //THIS METHOD HAS NO FORM VALIDATION
        //SOMEHOW IT DOES NOT CHECK EVEN WITH THE @VALID TAG
        
        String id = formData.getFirst("id");
        String name = formData.getFirst("name");
        String desc = formData.getFirst("desc");
        String due = formData.getFirst("due");
        String prior = formData.getFirst("prior");
        String status = formData.getFirst("status");

        System.out.println(id);
        System.out.println(name);
        System.out.println(desc);
        System.out.println(due);
        System.out.println(prior);
        System.out.println(status);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        @SuppressWarnings("null")
        long dueEpoch = LocalDate.parse(due.toString(), formatter)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        
                System.out.println(dueEpoch);

        // Set current time and date and add to object
        long createdOnEpoch = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long updatedOnEpoch = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        System.out.println(createdOnEpoch);
        System.out.println(updatedOnEpoch);

        Todo td = new Todo(id, name, desc, dueEpoch, prior, status, createdOnEpoch, updatedOnEpoch);

        tds.createEntry(td);

        return "redirect:/listing";
    }

    // public String postCreateForm(@Valid @ModelAttribute("todo") Todo todo, BindingResult result, Model model) {
    //     System.out.println(todo.toString());
        
    //     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    //     long dueEpoch = LocalDate.parse(todo.getDue().toString(), formatter)
    //             .atStartOfDay(ZoneId.systemDefault())
    //             .toInstant()
    //             .toEpochMilli();
        
    //     System.out.println(dueEpoch + ":testing");

    //     // Oerwrite the new formatted date as long
    //     todo.setDue(dueEpoch);

    //     // Set current time and date and add to object
    //     long createdOnEpoch = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    //     long updatedOnEpoch = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    //     todo.setCreatedOn(createdOnEpoch);
    //     todo.setUpdatedOn(updatedOnEpoch);

    //     if (result.hasErrors()) {
    //         System.out.println(result);
    //         return "add";
    //     }

    //     // Create an entry in redis/save to redis
    //     tds.createEntry(todo);

    //     return "redirect:/listing";
    // }

}
