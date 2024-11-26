import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RegistrationSequenceAdapter {

    public RegSequence adapt(JsonNode registrationSequenceJson) {

        RegSequence sequence = new RegSequence();

        // Parse the elements array
        JsonNode jsonNodes = registrationSequenceJson.get("nodes");
        if (jsonNodes == null || !jsonNodes.isArray()) {
            throw new IllegalArgumentException("Invalid JSON: 'elements' is missing or not an array.");
        }

        for (JsonNode jNode : jsonNodes) {
            ArrayNode elements = (ArrayNode) jNode.get("elements");
            JsonNode actions =  jNode.get("actions");

            for (JsonNode element : elements) {
                String elementName = element.asText();

                if (elementName == null) {
                    continue;
                }
                // 1. Skip elements starting with "flow-display"
                if (elementName.startsWith("flow-display")) {
                    continue;
                }
                // 2. Handle "flow-block-attributes" or elements containing "attributes"
                if (elementName.startsWith("flow-block-attributes") || elementName.contains("attributes")) {
                    Node attributeNode = createAttributeCollectorNode(element);
                    sequence.addNode(attributeNode);
                    continue;
                }

                // 3. Handle "flow-action" elements
                if (elementName.startsWith("flow-action")) {
                    if (actions != null && actions.isArray()) {
                        for (JsonNode action : actions) {
                            if (action == null){
                                System.out.println("Info: Action null for " + elementName);
                                continue;
                            }
                            if (elementName.equals(action.get("id").asText())) {
                                JsonNode actionDetails = action.get("action");
                                if (actionDetails == null){
                                    System.out.println("Info: ActionDetails null for " + elementName);
                                    continue;
                                };
                                if (actionDetails.get("type") == null){
                                    System.out.println("Info: ActionDetails type null for " + elementName);
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
                                if ("EXECUTOR".equals(actionType)) {
                                    Node node = createExecutorNode(elementName, nextNodeId);
                                    sequence.addNode(node);
                                } else if ("RULE".equals(actionType)) {
                                    Node node = createRuleNode(elementName, nextNodeId);
                                    sequence.addNode(node);

                                }

                                JsonNode boundTo = actionDetails.get("boundTo");
                                if (boundTo != null) {
                                    String boundToId = boundTo.asText();
                                    Node boundToNode = sequence.getNode(boundToId);
                                    String nextNodeOfBoundNode = boundToNode.getNextNode();
                                    if (nextNodeOfBoundNode == null) {
                                        boundToNode.setNextNode(elementName);
                                    } else {
                                        System.out.println("Info: BoundToNode already has a next node. Adding " +
                                                                   "decision node.");
                                        Node decisionNode = createDecisionNode();
                                        sequence.addNode(decisionNode);
                                        boundToNode.setNextNode(decisionNode.getId());
                                        decisionNode.addNextNode(nextNodeOfBoundNode);
                                        decisionNode.addNextNode(elementName);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return sequence;
    }

    private Node createAttributeCollectorNode(JsonNode element) {

        System.out.println("Info: Create Attribute collection Node for " + element.asText());
        return new Node(element.asText(), "ATTR");
    }

    private Node createDecisionNode(){

        String id = UUID.randomUUID().toString();
        System.out.println("Info: Create Decision Node with id " + id);

        return new Node(id, "DECISION");
    }

    private Node createExecutorNode(String id, String nextId) {

        System.out.println("Info: Create Executor Node for " + id);
        Node node = new Node(id,"EXECUTOR");
        node.setNextNode(nextId);
        return node;
    }

    private Node createRuleNode(String id, String nextId) {

        System.out.println("Info: Create Rule Node for " + id);
        Node node = new Node(id, "RULE");
        node.setNextNode(nextId);
        return node;
    }

    private List<String> parseNextNodes(JsonNode action) {
        List<String> nextNodes = new ArrayList<>();
        JsonNode next = action.get("next");
        if (next != null && next.isArray()) {
            next.forEach(node -> nextNodes.add(node.asText()));
        }
        return nextNodes;
    }
}
