import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
//        if (args.length == 0) {
//            System.err.println("Please provide the path to the JSON file as an argument.");
//            return;
//        }

//        String filePath = args[0];
        String filePath = "flow.json"; // Change this to match your JSON file's name


        try {
            // Read the JSON content from the file
            String jsonInput = new String(Files.readAllBytes(Paths.get(filePath)));

            // Build the Registration Sequence
            RegistrationSequenceAdapter adapter = new RegistrationSequenceAdapter();
            RegSequence regSequence = adapter.buildRegSequence(jsonInput);

            // Print the Registration Sequence
            System.out.println("Registration Sequence built successfully!");
            for (Node node : regSequence.getNodes().values()) {
                System.out.println(node);
            }
        } catch (Exception e) {
            System.err.println("An error occurred while processing the JSON file:");
            e.printStackTrace();
        }
    }
}
