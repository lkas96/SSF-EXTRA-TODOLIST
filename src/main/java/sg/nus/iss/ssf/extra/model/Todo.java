package sg.nus.iss.ssf.extra.model;

import java.util.UUID;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

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

    @NotNull(message = "Due date must not be empty")
    // @FutureOrPresent(message = "Must be due later today or in the future.")
    private Long due;

    @NotEmpty(message = "Priority must not be empty")
    @Pattern(regexp = "low|medium|high", message = "Value must be Low, Medium, or High")
    private String prior;

    @NotEmpty(message = "Status must not be empty")
    @Pattern(regexp = "pending|started|in_progress|completed", message = "Value must be Low, Medium, or High")
    private String status;

    @NotNull(message = "Created On must not be empty")
    private Long createdOn;

    @NotNull(message = "Updated On must not be empty")
    private Long updatedOn;

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

    public Long getDue() {
        return due;
    }

    public void setDue(Long due) {
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

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }

    public Long getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Long updatedOn) {
        this.updatedOn = updatedOn;
    }

    //Meant for use when doing intial load from file
    public Todo(String id, String name, String desc, Long due, String prior, String status, Long createdOn,
            Long updatedOn) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.due = due;
        this.prior = prior;
        this.status = status;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
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
