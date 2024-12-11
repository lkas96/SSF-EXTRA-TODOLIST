package sg.nus.iss.ssf.extra.service;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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

            // Date conversion epoch to Date
            JsonValue dueVal = jo.get("due");
            JsonNumber dueNum = (JsonNumber) dueVal;
            Date dueDate = new Date(dueNum.longValue());

            JsonValue crVal = jo.get("createdOn");
            JsonNumber crNum = (JsonNumber) crVal;
            Date crDate = new Date(crNum.longValue());

            JsonValue upVal = jo.get("updatedOn");
            JsonNumber upNum = (JsonNumber) upVal;
            Date upDate = new Date(upNum.longValue());

            // Instantiate a todo object from the jsonobject
            Todo td = new Todo();
            td.setId(jo.getString("id"));
            td.setName(jo.getString("name"));
            td.setDesc(jo.getString("desc"));
            td.setDue(dueDate);
            td.setPrior(jo.getString("prior"));
            td.setStatus(jo.getString("status"));
            td.setCreatedOn(crDate);
            td.setUpdatedOn(upDate);

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
                Date date = new Date(temp2.longValue());
                td.setDue(date);

                td.setPrior(jo.getString("prior"));
                td.setStatus(jo.getString("status"));

                JsonValue temp5 = jo.get("createdOn");
                JsonNumber temp6 = (JsonNumber) temp5;
                Date dateCr = new Date(temp6.longValue());
                td.setCreatedOn(dateCr);

                JsonValue temp8 = jo.get("updatedOn");
                JsonNumber temp9 = (JsonNumber) temp8;
                Date dateUp = new Date(temp9.longValue());
                td.setUpdatedOn(dateUp);

                // add to the arraylist
                filteredlist.add(td);
            }
        }

        return filteredlist;

    }

    public void createEntry(Todo todo) {

        Todo td = new Todo(todo.getId(), todo.getName(), todo.getDesc(), todo.getDue(), todo.getPrior(),
                todo.getStatus());

        //Process date for storing as JsonObject into the redis map
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date temp = td.getDue();
        String dueString = sdf.format(temp);
        long dueEpoch = LocalDate.parse(dueString, formatter)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        Date temp2 = td.getCreatedOn();
        String createdString = sdf.format(temp2);
        long createdEpoch = LocalDate.parse(createdString, formatter)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        Date temp3 = td.getUpdatedOn();
        String updatedString = sdf.format(temp3);
        long updatedEpoch = LocalDate.parse(updatedString, formatter)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        JsonObject toJson = Json.createObjectBuilder()
                .add("id", td.getId())
                .add("name", td.getName())
                .add("desc", td.getDesc())
                .add("due", dueEpoch)
                .add("prior", td.getPrior())
                .add("status", td.getStatus())
                .add("createdOn", createdEpoch)
                .add("updatedOn", updatedEpoch)
                .build();

        //Storing in the redis map
        mp.create(Constant.todoKey, td.getId().toString(), toJson.asJsonObject().toString());
    }

    public Todo findById(String todoId) {
        Object findId = mp.get(Constant.todoKey, todoId);
        // Convert object to String
        String convert = findId.toString();

        JsonReader jr = Json.createReader(new StringReader(convert));
        JsonObject jo = jr.readObject();

        JsonValue dueVal = jo.get("due");
        JsonNumber dueNum = (JsonNumber) dueVal;
        Date dueDate = new Date(dueNum.longValue());

        JsonValue crVal = jo.get("createdOn");
        JsonNumber crNum = (JsonNumber) crVal;
        Date crDate = new Date(crNum.longValue());

        JsonValue upVal = jo.get("updatedOn");
        JsonNumber upNum = (JsonNumber) upVal;
        Date upDate = new Date(upNum.longValue());

        Todo td = new Todo(jo.getString("id"),
                jo.getString("name"),
                jo.getString("desc"),
                dueDate,
                jo.getString("prior"),
                jo.getString("status"),
                crDate,
                upDate);

        return td;
    }

    public void deleteTodo(String todoId) {
        mp.delete(Constant.todoKey, todoId);
    }

    public void updateTodo(Todo found) {

        // Set new updated time to now
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDueDateString = sdf.format(new Date());
        Date updatedDate;

        try {
            updatedDate = sdf.parse(formattedDueDateString);
            found.setUpdatedOn(updatedDate);
        } catch (ParseException e) {
            //Do nothing at the momemt, no error handling
            e.printStackTrace();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Date temp = found.getDue();
        String dueString = sdf.format(temp);
        long dueEpoch = LocalDate.parse(dueString, formatter)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        Date temp2 = found.getCreatedOn();
        String createdString = sdf.format(temp2);
        long createdEpoch = LocalDate.parse(createdString, formatter)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        Date temp3 = found.getUpdatedOn();
        String updatedString = sdf.format(temp3);
        long updatedEpoch = LocalDate.parse(updatedString, formatter)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        // Create json object for storing
        JsonObject toJson = Json.createObjectBuilder()
                .add("id", found.getId())
                .add("name", found.getName())
                .add("desc", found.getDesc())
                .add("due", dueEpoch)
                .add("prior", found.getPrior())
                .add("status", found.getStatus())
                .add("createdOn", createdEpoch)
                .add("updatedOn", updatedEpoch)
                .build();

        mp.update(Constant.todoKey, found.getId(), toJson.toString());

    }
}
