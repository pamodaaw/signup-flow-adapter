import java.util.HashMap;
import java.util.Map;

class RegistrationDTO {

    private Map<String, NodeDTO> nodes;
    private Map<String, String> pageIds;
    private String firstNodeId = null;

    public RegistrationDTO() {
        this.nodes = new HashMap<>();
        this.pageIds = new HashMap<>();
    }

    public String getFirstNodeId() {

        return firstNodeId;
    }

    public void setFirstNodeId(String firstNodeId) {

        this.firstNodeId = firstNodeId;
    }

    // Add a node to the map
    public void addNode(NodeDTO nodeDTO) {
        if (nodeDTO != null && nodeDTO.getId() != null) {
            nodes.put(nodeDTO.getId(), nodeDTO);
        } else {
            System.err.println("Warning: Attempted to add a null or invalid node.");
        }
    }

    // Retrieve a node by its ID
    public NodeDTO getNode(String id) {
        return nodes.get(id);
    }

    // Retrieve all nodes as a map
    public Map<String, NodeDTO> getNodes() {
        return nodes;
    }

    public Map<String, String> getPageIds() {

        return pageIds;
    }

    public void addPageId(String key, String value) {

        this.pageIds.put(key, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RegSequence:").append(System.lineSeparator());
        sb.append("Reg first node: ").append(firstNodeId).append(System.lineSeparator());

        for (NodeDTO nodeDTO : nodes.values()) {
            sb.append(nodeDTO).append(System.lineSeparator());
        }

        return sb.toString();
    }
}
