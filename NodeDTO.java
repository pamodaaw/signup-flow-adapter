import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class NodeDTO {

    private String id; // Unique identifier for the node
    private String type; // Type of the node (e.g., attributeCollectorEX, executor, rule, decision)
    private Map<String, String> pageIds = new HashMap<>();
    private List<String> nextNodes; // List of next node IDs for decision nodes

    public NodeDTO(String id, String type) {

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

    public List<String> getNextNodes() {

        return nextNodes;
    }

    public void addNextNode(String nextNodeId) {

        this.nextNodes.add(nextNodeId);
    }

    public Map<String, String> getPageIds() {

        return pageIds;
    }

    public void addPageIds(String key, String value) {

        this.pageIds.put(key, value);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Node ID: ").append(id).append(", Type: ").append(type);

        if (!nextNodes.isEmpty()) {
            sb.append(", Next Nodes: ").append(String.join(", ", nextNodes));
        } else {
            sb.append(", No Next Node");
        }

        if (pageIds != null && !pageIds.isEmpty()) {
            for (Map.Entry<String, String> entry : pageIds.entrySet()) {
                sb.append(" ")
                        .append("Key: ").append(entry.getKey())
                        .append(", Value: ").append(entry.getValue());
            }
        } else {
            sb.append(", No Page Ids");
        }
        return sb.toString();
    }
}
