import java.util.HashMap;
import java.util.Map;

class RegSequence {
    private Map<String, Node> nodes; // Map of node IDs to Node objects

    public RegSequence() {
        this.nodes = new HashMap<>();
    }

    // Add a node to the map
    public void addNode(Node node) {
        if (node != null && node.getId() != null) {
            nodes.put(node.getId(), node);
        } else {
            System.err.println("Warning: Attempted to add a null or invalid node.");
        }
    }

    // Retrieve a node by its ID
    public Node getNode(String id) {
        return nodes.get(id);
    }

    // Retrieve all nodes as a map
    public Map<String, Node> getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RegSequence:").append(System.lineSeparator());
        for (Node node : nodes.values()) {
            sb.append(node).append(System.lineSeparator());
        }
        return sb.toString();
    }
}
