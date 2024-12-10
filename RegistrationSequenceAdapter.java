import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RegistrationSequenceAdapter {

    public RegistrationDTO adapt(JsonNode registrationSequenceJson) {

        RegistrationDTO sequence = new RegistrationDTO();
        String firstNode = null;


        // Parse the elements array
        JsonNode jsonNodes = registrationSequenceJson.get("nodes");
        if (jsonNodes == null || !jsonNodes.isArray()) {
            throw new IllegalArgumentException("Invalid JSON: 'nodes' is missing or not an array.");
        }

        for (JsonNode jNode : jsonNodes) {
            JsonNode actionButtons = jNode.get("actions");
            String jnodeId = jNode.get("id").asText();

            List<NodeDTO> nextActionNodeDTOS = new ArrayList<>();

            if (actionButtons == null) {
                System.out.println("Info: Actions null for " + jnodeId);
                continue;
            }

            for (JsonNode actionButton : actionButtons) {

                String actionId = actionButton.get("id").asText();

                JsonNode action = actionButton.get("action");

                if (action == null) {
                    System.out.println("Info2: Actions null for " + jnodeId);
                    continue;
                }

                String nextNodeId = "";
                ArrayNode nextNode = (ArrayNode) actionButton.get("next");
                if (nextNode != null) {
                    for (JsonNode next : nextNode) {
                        if (next != null) {
                            String nextNodeIdValue = next.asText();
                            if ("COMPLETE".equals(nextNodeIdValue)) {
                                nextNodeId = null;
                            } else {
                                nextNodeId = nextNodeIdValue;
                            }
                            break;
                        }
                    }
                }
                if (action.get("type") == null) {
                    System.out.println("Info: ActionDetails type null for " + actionId);
                    continue;
                }
                String actionType = action.get("type").asText();

                NodeDTO nodeDTO;
                if ("EXECUTOR".equals(actionType)) {
                    ArrayNode executorNameArray = (ArrayNode) action.get("name");
                    boolean firstExecutorInArray = true;
                    NodeDTO prevNode = null;
                    for (JsonNode executorName : executorNameArray) {
                        String executorID = executorName.asText();
                        if (firstExecutorInArray) {
                            nodeDTO = createExecutorNode(actionId, nextNodeId, executorID);
                            nextActionNodeDTOS.add(nodeDTO);
                            firstExecutorInArray = false;
                            prevNode = nodeDTO;
                        } else {
                            String nextExecutorId = UUID.randomUUID().toString();
                            nodeDTO = createExecutorNode(nextExecutorId, nextNodeId, executorID);
                            prevNode.getNextNodes().remove(nextNodeId);
                            prevNode.addNextNode(nextExecutorId);
                            sequence.addNode(nodeDTO);
                        }
                    }
                } else if ("RULE".equals(actionType)) {
                    System.out.println("Info: Create Rule Node for " + actionId);
                } else if ("NEXT".equals(actionType)) {
                    String pageActionType = action.has("meta") && action.get("meta").has("actionType")
                            ? action.get("meta").get("actionType").asText()
                            : "INIT";

                    for (NodeDTO node : sequence.getNodes().values()) {
                        if (node.getNextNodes().contains(jnodeId)) {
                            System.out.println(
                                    "NEXT action found. Found node " + node.getId() + " with next node " + jnodeId);
                            node.getNextNodes().remove(jnodeId);
                            node.addNextNode(nextNodeId);
                            node.addPageIds(pageActionType, jnodeId);
                        }
                    }
                } else if ("SUBMIT".equals(actionType)) {
                    nodeDTO = createInputCollectorNode(actionId, nextNodeId);
                    nextActionNodeDTOS.add(nodeDTO);
                }
            }

            if (nextActionNodeDTOS.size() > 1) {
                NodeDTO decisionNodeDTO = createDecisionNode();
                if (firstNode == null) {
                    firstNode = decisionNodeDTO.getId();
                }
                nextActionNodeDTOS.forEach(nodeDTO -> decisionNodeDTO.addNextNode(nodeDTO.getId()));
                decisionNodeDTO.addPageIds("INIT", jnodeId);
                nextActionNodeDTOS.forEach(sequence::addNode);
                for (NodeDTO node : sequence.getNodes().values()) {
                    if (node.getNextNodes().contains(jnodeId)) {
                        System.out.println("Info: Found node " + node.getId() + " with next node " + jnodeId);
                        node.getNextNodes().remove(jnodeId);
                        node.addNextNode(decisionNodeDTO.getId());
                    }
                }
                sequence.addNode(decisionNodeDTO);
            } else if (nextActionNodeDTOS.size() == 1) {
                NodeDTO nextNodeDTO = nextActionNodeDTOS.get(0);
                if (firstNode == null) {
                    firstNode = nextNodeDTO.getId();
                }
                nextNodeDTO.addPageIds("INIT", jnodeId);
                for (NodeDTO node : sequence.getNodes().values()) {
                    if (node.getNextNodes().contains(jnodeId)) {
                        System.out.println("Info: Found node " + node.getId() + " with next node " + jnodeId);
                        node.getNextNodes().remove(jnodeId);
                        node.addNextNode(nextNodeDTO.getId());
                    }
                }
                sequence.addNode(nextNodeDTO);
            }
        }

        sequence.setFirstNodeId(firstNode);
        return sequence;
    }

    private static NodeDTO createInputCollectorNode(String nodeId, String nextNodeId) {

        System.out.println("Info: Create Input collection Node for " + nodeId);
        NodeDTO node = new NodeDTO(nodeId, "INPUT");
        node.addNextNode(nextNodeId);
        return node;
    }

    private NodeDTO createDecisionNode() {

        String id = UUID.randomUUID().toString();
        System.out.println("Info: Create Decision Node with id " + id);
        return new NodeDTO(id, "DECISION");
    }

    private static NodeDTO createExecutorNode(String id, String nextNodeId, String executorID) {

        System.out.println("Info: Create Executor Node for " + id);
        NodeDTO node = new NodeDTO(id, "EXECUTOR");
        node.addNextNode(nextNodeId);
        node.addProperty("EXECUTOR_ID", executorID);
        return node;
    }
}
