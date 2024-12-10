package sg.nus.iss.ssf.extra.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import sg.nus.iss.ssf.extra.constant.Constant;
import sg.nus.iss.ssf.extra.model.Todo;
import sg.nus.iss.ssf.extra.repo.MapRepo;

@Service
public class TodoService {

    @Autowired
    MapRepo mp;

    public List<Todo> getEntries() {

        // Get all entries
        Map<Object, Object> todosObject = mp.getEntries(Constant.todoKey);

        List<Todo> todolist = new ArrayList<>();

        for (Entry<Object, Object> entry : todosObject.entrySet()) {

            // Each entry line, we want the hashvalue
            String hashvalue = entry.getValue().toString();

            // hashvalue is a string, we convert back to json object
            JsonReader jr = Json.createReader(new StringReader(hashvalue));
            JsonObject jo = jr.readObject();

            // Instantiate a todo object from the jsonobject
            Todo td = new Todo();
            td.setId(jo.getString("id"));
            td.setName(jo.getString("name"));
            td.setDesc(jo.getString("desc"));

            JsonValue temp = jo.get("due");
            JsonNumber temp2 = (JsonNumber) temp;
            td.setDue(temp2.longValue());

            td.setPrior(jo.getString("prior"));
            td.setStatus(jo.getString("status"));

            JsonValue temp5 = jo.get("createdOn");
            JsonNumber temp6 = (JsonNumber) temp5;
            td.setCreatedOn(temp6.longValue());

            JsonValue temp8 = jo.get("updatedOn");
            JsonNumber temp9 = (JsonNumber) temp8;
            td.setUpdatedOn(temp9.longValue());

            // add to the arraylist
            todolist.add(td);
        }

        // return list
        return todolist;

    }

    public List<Todo> getFiltered(String filter) {
        List<Todo> filteredlist = new ArrayList<>();

        Map<Object, Object> allEntries = mp.getEntries(Constant.todoKey);

        for (Entry<Object, Object> entry : allEntries.entrySet()) {
            // Each entry line, we want the hashvalue
            String hashvalue = entry.getValue().toString();

            // hashvalue is a string, we convert back to json object
            JsonReader jr = Json.createReader(new StringReader(hashvalue));
            JsonObject jo = jr.readObject();

            // If status matches the user selected filter
            // Add to the filterestList
            if (jo.getString("status").equalsIgnoreCase(filter) || filter.equalsIgnoreCase("all")) {

                Todo td = new Todo();
                td.setId(jo.getString("id"));
                td.setName(jo.getString("name"));
                td.setDesc(jo.getString("desc"));

                JsonValue temp = jo.get("due");
                JsonNumber temp2 = (JsonNumber) temp;
                td.setDue(temp2.longValue());

                td.setPrior(jo.getString("prior"));
                td.setStatus(jo.getString("status"));
                
                JsonValue temp5 = jo.get("createdOn");
                JsonNumber temp6 = (JsonNumber) temp5;
                td.setCreatedOn(temp6.longValue());

                JsonValue temp8 = jo.get("updatedOn");
                JsonNumber temp9 = (JsonNumber) temp8;
                td.setUpdatedOn(temp9.longValue());

                // add to the arraylist
                filteredlist.add(td);
            }
        }

        return filteredlist;

    }

    public void createEntry(Todo td) {

        JsonObject toJson = Json.createObjectBuilder()
                .add("id", td.getId())
                .add("name", td.getName())
                .add("desc", td.getDesc())
                .add("due", td.getDue())
                .add("prior", td.getPrior())
                .add("status", td.getStatus())
                .add("createdOn", td.getCreatedOn())
                .add("updatedOn", td.getUpdatedOn())
                .build();

        mp.create(Constant.todoKey, td.getId().toString(), toJson.asJsonObject().toString());
    }

    public Todo findById(String todoId) {
        Object findId = mp.get(Constant.todoKey, todoId);
        //Convert object to String
        String convert = findId.toString();

        JsonReader jr = Json.createReader(new StringReader(convert));
        JsonObject jo = jr.readObject();

        Todo td = new Todo( jo.getString("id"), 
                            jo.getString("name"), 
                            jo.getString("desc"), 
                            jo.getJsonNumber("due").longValue(), 
                            jo.getString("prior"), 
                            jo.getString("status"), 
                            jo.getJsonNumber("createdOn").longValue(),
                            jo.getJsonNumber("updatedOn").longValue()
        );

        return td;
    }

    public void deleteTodo(String todoId) {
        mp.delete(Constant.todoKey, todoId);
    }

    public void updateTodo(Todo found) {

        //Create json object for storing
        JsonObject toJson = Json.createObjectBuilder()
        .add("id", found.getId())
        .add("name", found.getName())
        .add("desc", found.getDesc())
        .add("due", found.getDue())
        .add("prior", found.getPrior())
        .add("status", found.getStatus())
        .add("createdOn", found.getCreatedOn())
        .add("updatedOn", found.getUpdatedOn())
        .build();

        mp.update(Constant.todoKey, found.getId(), toJson.toString());

    }
}
