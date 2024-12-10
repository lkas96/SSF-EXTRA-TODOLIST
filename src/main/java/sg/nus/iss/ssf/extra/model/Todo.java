package sg.nus.iss.ssf.extra.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Todo {

    // Json data format for one entry
    // "id": "00978839-39e1-4919-94a6-b08275a5efa9",
    // "name": "Join a software development forum",
    // "description": "Velit magnam et possimus esse corporis voluptas. Vitae rerum
    // impedit ut excepturi eius necessitatibus. Architecto repudiandae
    // reprehenderit qui voluptatem exercitationem repudiandae.",
    // "due_date": "Sun, 10/22/2024",
    // "priority_level": "high",
    // "status": "completed",
    // "created_at": "Sun, 10/15/2023",
    // "updated_at": "Sun, 10/15/2023"

    @NotEmpty(message = "ID must not be empty")
    private String id;

    @NotEmpty(message = "Name must not be empty")
    @Size(min = 10, max = 50, message = "Name has to be 10-15 characters long.")
    private String name;

    @NotEmpty(message = "Description must not be empty")
    @Size(max = 255, message = "Maximum length is 255 characters.")
    private String desc;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    // @FutureOrPresent(message = "Must be due later today or in the future.")
    private Date due;

    @NotEmpty(message = "Priority must not be empty")
    @Pattern(regexp = "low|medium|high", message = "Value must be Low, Medium, or High")
    private String prior;

    @NotEmpty(message = "Status must not be empty")
    @Pattern(regexp = "pending|started|in_progress|completed", message = "Value must be Low, Medium, or High")
    private String status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdOn;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updatedOn;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getDue() {
        return due;
    }

    public void setDue(Date due) {
        this.due = due;
    }

    public String getPrior() {
        return prior;
    }

    public void setPrior(String prior) {
        this.prior = prior;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    // Meant for use when doing intial load from file
    public Todo(String id, String name, String desc, Date due, String prior, String status, Date createdOn,
            Date updatedOn) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.due = due;
        this.prior = prior;
        this.status = status;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }

    //For creating a new entry
    //Add form
    public Todo(String id, String name, String desc, Date due, String prior, String status) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.due = due;
        this.prior = prior;
        this.status = status;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDueDateString = formatter.format(new Date());

        try {
            Date createdDate = formatter.parse(formattedDueDateString);
            this.createdOn = createdDate;
            this.updatedOn = createdOn;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

    }

    public Todo() {
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return id + "," + name + "," + desc + "," + due + "," + prior + ","
                + status + "," + createdOn + "," + updatedOn;
    }

}
