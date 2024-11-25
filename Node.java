import java.util.ArrayList;
import java.util.List;

class Node {
    private String id; // Unique identifier for the node
    private String type; // Type of the node (e.g., attributeCollectorEX, executor, rule, decision)
    private String nextNode; // Single next node's ID for linear flows
    private List<String> nextNodes; // List of next node IDs for decision nodes

    public Node(String id, String type) {
        this.id = id;
        this.type = type;
        this.nextNodes = new ArrayList<>(); // Initialize nextNodes as an empty list
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getNextNode() {
        return nextNode;
    }

    public void setNextNode(String nextNode) {
        this.nextNode = nextNode;
    }

    public List<String> getNextNodes() {
        return nextNodes;
    }

    public void addNextNode(String nextNodeId) {
        this.nextNodes.add(nextNodeId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Node ID: ").append(id).append(", Type: ").append(type);

        if (nextNode != null) {
            sb.append(", Next Node: ").append(nextNode);
        } else if (!nextNodes.isEmpty()) {
            sb.append(", Next Nodes: ").append(String.join(", ", nextNodes));
        } else {
            sb.append(", No Next Node");
        }

        return sb.toString();
    }
}
