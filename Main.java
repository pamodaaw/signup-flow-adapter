import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
//        if (args.length == 0) {
//            System.err.println("Please provide the path to the JSON file as an argument.");
//            return;
//        }

//        String filePath = args[0];
        String filePath = "flow.json"; // Change this to match your JSON file's name


        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File jsonFile = new File(filePath);

            // Read the JSON content from the file
            JsonNode registrationSequenceJson = objectMapper.readTree(jsonFile);

            // Initialize RegistrationSequenceAdapter
            RegistrationSequenceAdapter adapter = new RegistrationSequenceAdapter();

            // Adapt JSON to nodes
            RegSequence sequence = adapter.adapt(registrationSequenceJson);

            // Print the Registration Sequence
            System.out.println("Registration Sequence built successfully!");
            for (Node node : sequence.getNodes().values()) {
                System.out.println(node);
            }
        } catch (Exception e) {
            System.err.println("An error occurred while processing the JSON file:");
            e.printStackTrace();
        }
    }
}
