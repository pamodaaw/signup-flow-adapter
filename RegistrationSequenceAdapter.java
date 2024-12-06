import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.ArrayList;
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
            JsonNode actions = jNode.get("actions");
            String jnodeId = jNode.get("id").asText();
            List<String> prevNodeIds =  new ArrayList<>();

            for (NodeDTO node : sequence.getNodes().values()) {
                if (node.getPageIds().containsValue(jnodeId)) {
                    prevNodeIds.add(node.getId());
                    System.out.println("Info: Found node " + node.getId() + " with pageId " + jnodeId);
                }
            }

            List<NodeDTO> nextActionNodeDTOS = new ArrayList<>();

            if (actions == null) {
                System.out.println("Info: Actions null for " + jnodeId);
                continue;
            }

            for (JsonNode action : actions) {

                String actionId = action.get("id").asText();

                JsonNode actionDetails = action.get("action");
                if (actionDetails == null) {
                    System.out.println("Info: ActionDetails null for " + actionId);
                    continue;
                }
                if (actionDetails.get("type") == null) {
                    System.out.println("Info: ActionDetails type null for " + actionId);
                    if ("submit".equals(action.get("type").asText())) {
                        NodeDTO inputNodeDTO = createInputCollectorNode(actionId, jnodeId);
                        if (firstNode == null) {
                            firstNode = inputNodeDTO.getId();
                        }
                        for (String prevNodeId : prevNodeIds) {
                            System.out.println("Info1: Adding next node as " + inputNodeDTO.getId() + " for " + prevNodeId);
                            sequence.getNode(prevNodeId).addNextNode(inputNodeDTO.getId());
                            removePageId(sequence.getNode(prevNodeId).getPageIds(), jnodeId);
                        }
                        sequence.addNode(inputNodeDTO);
                    }
                    continue;
                }
                String nextNodeId = "";
                ArrayNode nextNode = (ArrayNode) action.get("next");
                if (nextNode != null) {
                    for (JsonNode next : nextNode) {
                        if (next != null) {
                            nextNodeId = next.asText();
                            break;
                        }
                    }
                }
                String actionType = actionDetails.get("type").asText();
                String nextPageType = null;
                if (actionDetails.get("ref") != null) {
                    nextPageType = actionDetails.get("ref").asText();
                    System.out.println("Info: Next page type is " + nextPageType);
                }

                if ("EXECUTOR".equals(actionType)) {
                    NodeDTO nodeDTO = createExecutorNode(actionId, nextPageType, nextNodeId);
                    nextActionNodeDTOS.add(nodeDTO);
                } else if ("RULE".equals(actionType)) {
                    System.out.println("Info: Create Rule Node for " + actionId);
                } else if ("NEXT".equals(actionType)) {
                    for (String prevNodeId : prevNodeIds) {
                        sequence.getNode(prevNodeId).addPageIds(nextPageType, nextNodeId);
                        System.out.println("Info2: Adding next node as " + nextNodeId + " for " + prevNodeId);
                    }
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
                for (String prevNodeId : prevNodeIds) {
                    sequence.getNode(prevNodeId).addNextNode(decisionNodeDTO.getId());
                }
                sequence.addNode(decisionNodeDTO);
            } else if (nextActionNodeDTOS.size() == 1) {
                NodeDTO nextNodeDTO = nextActionNodeDTOS.get(0);
                if (firstNode == null) {
                    firstNode = nextNodeDTO.getId();
                }
                for (String prevNodeId : prevNodeIds) {
                    sequence.getNode(prevNodeId).addNextNode(nextNodeDTO.getId());
                }
                sequence.addNode(nextNodeDTO);
            }
        }

        sequence.setFirstNodeId(firstNode);
        return sequence;
    }

    private static NodeDTO createInputCollectorNode(String nodeId, String pageId) {

        System.out.println("Info: Create Input collection Node for " + nodeId);
        NodeDTO node = new NodeDTO(nodeId, "INPUT");
        node.addPageIds("1", pageId);
        return node;
    }

    private NodeDTO createDecisionNode() {

        String id = UUID.randomUUID().toString();
        System.out.println("Info: Create Decision Node with id " + id);
        return new NodeDTO(id, "DECISION");
    }

    private static NodeDTO createExecutorNode(String id, String nexPageType, String nextId) {

        System.out.println("Info: Create Executor Node for " + id);
        NodeDTO node = new NodeDTO(id, "EXECUTOR");
        if (nexPageType != null) {
            node.addPageIds(nexPageType, nextId);
        } else {
            node.addPageIds("INIT", nextId);
        }
        return node;
    }

    private static void removePageId(Map<String, String> map, String valueToRemove) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(valueToRemove)) {
                map.remove(entry.getKey());
                break; // Stop after removing the first matching entry
            }
        }
    }
}
