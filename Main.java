import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
//        if (args.length == 0) {
//            System.err.println("Please provide the path to the JSON file as an argument.");
//            return;
//        }
//        String filePath = args[0];

        tryFlow1();
        tryFlow2();
        tryFlow3();
        tryFlow4();
    }

    private static void tryFlow3() {

        RegistrationDTO sequence = null;
        try {
            sequence = convertFlow("flow3.json");
        } catch (Exception e) {
            System.err.println("An error occurred while processing the JSON file:");
        }

        assert sequence != null;
        if (sequence.getNodes().size() != 5) {
            System.out.println("ERROR: Expected 5 nodes, but found " + sequence.getNodes().size());
        } else {
            System.out.println("SUCCESS: Found 5 nodes as expected.");
        }
    }

    private static void tryFlow2() {

        RegistrationDTO sequence = null;
        try {
            sequence = convertFlow("flow2.json");
        } catch (Exception e) {
            System.err.println("An error occurred while processing the JSON file:");
        }

        assert sequence != null;
        if (sequence.getNodes().size() != 4) {
            System.out.println("ERROR: Expected 4 nodes, but found " + sequence.getNodes().size());
        } else {
            System.out.println("SUCCESS: Found 4 nodes as expected.");
        }
    }

    private static void tryFlow1() {

        RegistrationDTO sequence = null;
        try {
            sequence = convertFlow("flow1.json");
        } catch (IOException ex) {
            System.err.println("An error occurred while processing the JSON file:");
        }
        assert sequence != null;
        if (sequence.getNodes().size() != 2) {
            System.out.println("ERROR: Expected 2 nodes, but found " + sequence.getNodes().size());
        } else {
            System.out.println("SUCCESS: Found 2 nodes as expected.");
        }
    }

    private static void tryFlow4() {

        RegistrationDTO sequence = null;
        try {
            sequence = convertFlow("flow4.json");
        } catch (Exception e) {
            System.err.println("An error occurred while processing the JSON file:");
        }

        assert sequence != null;
        if (sequence.getNodes().size() != 3) {
            System.out.println("ERROR: Expected 3 nodes, but found " + sequence.getNodes().size());
        } else {
            System.out.println("SUCCESS: Found 3 nodes as expected.");
        }
    }

    private static RegistrationDTO convertFlow(String filePath) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File(filePath);

        JsonNode registrationSequenceJson = objectMapper.readTree(jsonFile);
        RegistrationSequenceAdapter adapter = new RegistrationSequenceAdapter();
        RegistrationDTO sequence = adapter.adapt(registrationSequenceJson);
        System.out.println("Registration Sequence built successfully!");
        System.out.println(sequence);
        return sequence;
    }
}
