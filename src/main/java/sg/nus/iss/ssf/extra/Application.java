package sg.nus.iss.ssf.extra;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import sg.nus.iss.ssf.extra.constant.Constant;
import sg.nus.iss.ssf.extra.repo.MapRepo;

@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired
	private MapRepo mp;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Process the todos.json intiial processing
		// First read, and store on redis
		FileReader fr = new FileReader("todos.json");
		BufferedReader br = new BufferedReader(fr);

		// Where the filedata will be stored for processing
		String jsonData;

		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}

			jsonData = sb.toString();
		} finally {
			br.close();
		}

		// Testing file has be read properly
		// and is saved to a variable for processing
		System.out.println(jsonData);

		// Now deserialize this chunk of json data and save into redis as String
		JsonReader jr = Json.createReader(new StringReader(jsonData));
		JsonArray ja = jr.readArray(); // It is an arary of todo objects

		for (Integer rowNum = 0; rowNum < ja.size(); rowNum++) {

			// For each array, read the String as JsonObject
			JsonObject entries = ja.get(rowNum).asJsonObject();

			// Convert the 3 dates into epoch format first
			// Convert the dates to epoch milliseconds
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

			String dueDateStr = entries.getString("due_date");
			String cleanedDateStr = dueDateStr.substring(5); // Remove the first 4 characters

			long dueEpoch = LocalDate.parse(cleanedDateStr, formatter)
					.atStartOfDay(ZoneId.systemDefault())
					.toInstant()
					.toEpochMilli();

			String createdOnDateStr = entries.getString("created_at");
			String cleanedDateStr2 = createdOnDateStr.substring(5); // Remove the first 4 characters
			long createdOnEpoch = LocalDate.parse(cleanedDateStr2, formatter)
			.atStartOfDay(ZoneId.systemDefault())
			.toInstant()
			.toEpochMilli();


			String updatedOnDateStr = entries.getString("updated_at");
			String cleanedDateStr3 = updatedOnDateStr.substring(5); // Remove the first 4 characters
			long updatedOnEpoch = LocalDate.parse(cleanedDateStr3, formatter)
			.atStartOfDay(ZoneId.systemDefault())
			.toInstant()
			.toEpochMilli();

			// Serialize to JSON Object then save the JsonObject as String in redis Map
			JsonObject toBuild = Json.createObjectBuilder()
					.add("id", entries.getString("id"))
					.add("name", entries.getString("name"))
					.add("desc", entries.getString("description"))
					.add("due", dueEpoch)
					.add("prior", entries.getString("priority_level"))
					.add("status", entries.getString("status"))
					.add("createdOn", createdOnEpoch)
					.add("updatedOn", updatedOnEpoch)
					.build();

					System.out.println(toBuild);

			// SAVE EACH ENTRY INTO REDIS MAP FOLLOWING FORMAT
			// REDIS KEY, HASHKEY, HASHVALUE
			mp.create(Constant.todoKey, ja.get(rowNum).asJsonObject().getString("id"), toBuild.toString());
		}

	}

}
