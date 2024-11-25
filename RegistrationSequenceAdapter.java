import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegistrationSequenceAdapter {

    /**
     * Builds the registration sequence from the JSON input.
     */
    public RegSequence buildRegSequence(String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);

        RegSequence regSequence = new RegSequence();

        // Parse nodes from JSON and add them to RegSequence
        JsonNode jsonNodes = rootNode.get("nodes");
        if (jsonNodes != null) {
            for (JsonNode jsonNode : jsonNodes) {
                processJsonNode(jsonNode, regSequence);
            }
        }

        // Ensure there is a single starting node
       // ensureSingleStartingNode(regSequence);

        return regSequence;
    }

    /**
     * Processes each JSON node and adds it to the registration sequence.
     */
    private void processJsonNode(JsonNode jsonNode, RegSequence regSequence) {

        Node beNode = createNodeFromJson(jsonNode);
        if (beNode == null) {
            // Skip this node if its type cannot be determined
            return;
        }

        regSequence.addNode(beNode);

        // Process actions and link next nodes
        JsonNode actions = jsonNode.get("actions");
        if (actions != null) {
            for (JsonNode action : actions) {
                processAction(action, beNode, regSequence);
            }
        }
    }

    /**
     * Creates a backend node based on the JSON definition.
     */
    private Node createNodeFromJson(JsonNode jsonNode) {

        if (jsonNode == null) {
            System.err.println("Warning: Node JSON is null, skipping this node.");
            return null;
        }

        // Ensure the "id" key exists
        JsonNode idNode = jsonNode.get("id");
        if (idNode == null || idNode.asText().isEmpty()) {
            System.err.println("Warning: Node is missing 'id' field, skipping this node.");
            return null;
        }
        String jsonNodeId = idNode.asText();

        // Check for "actions" to determine the node type
        JsonNode actions = jsonNode.get("actions");
        if (actions != null) {
            for (JsonNode action : actions) {
                JsonNode actionDetail = action.get("action");
                String actionType = null;

                if (actionDetail != null) {
                    JsonNode typeNode = actionDetail.get("type");
                    if (typeNode != null) {
                        actionType = typeNode.asText();
                    }
                }

                // Fallback to outer type of the action element.
//                if (actionType == null) {
//                    JsonNode outerTypeNode = action.get("type");
//                    if (outerTypeNode != null) {
//                        actionType = outerTypeNode.asText();
//                    }
//                }

                if (actionType != null) {
                    switch (actionType.toUpperCase()) {
                        case "EXECUTOR":
                            return new Node(jsonNodeId, "executor");
                        case "RULE":
                            return new Node(jsonNodeId, "rule");
                        case "NEXT":
                            System.out.println("Info: Skipping node creation for 'NEXT' action in node " + jsonNodeId);
                            break;
                        default:
                            System.err.println("Warning: Unsupported action type: " + actionType + " in node " + jsonNodeId);
                            break;
                    }
                }
            }
        }

        // Check for "elements" if no valid action determined the type
        JsonNode elements = jsonNode.get("elements");
        if (elements != null) {
            for (JsonNode element : elements) {
                String elementId = element.asText();
                if (elementId != null && elementId.toLowerCase().contains("attribute")) {
                    return new Node(jsonNodeId, "attributeCollectorEX");
                }
            }
        }

        // Log a warning if the node type is undetermined and return null
        System.err.println("Warning: Unable to determine type for node ID: " + jsonNodeId + ", skipping this node.");
        return null;
    }

    /**
     * Processes each action in a node.
     */
    private void processAction(JsonNode action, Node beNode, RegSequence regSequence) {
        if (action == null) {
            System.err.println("Warning: Null action encountered in node " + beNode.getId());
            return;
        }

        JsonNode actionDetail = action.get("action");
        String actionType = null;

        // Prioritize the type inside the action object
        if (actionDetail != null) {
            JsonNode typeNode = actionDetail.get("type");
            if (typeNode != null) {
                actionType = typeNode.asText();
            }
        }

        // Fallback to the outer type if action.type is not available
        if (actionType == null) {
            JsonNode outerTypeNode = action.get("type");
            if (outerTypeNode != null) {
                actionType = outerTypeNode.asText();
            }
        }

        // Ensure actionType is valid
        if (actionType == null || actionType.isEmpty()) {
            System.err.println("Warning: Action type is missing for action ID: " + action.get("id"));
            return;
        }

        // Handle actions based on the determined actionType
        switch (actionType.toUpperCase()) {
            case "NEXT":
                System.out.println("Info: Skipping BE node creation for action type 'NEXT' in node " + beNode.getId());
                JsonNode next = action.get("next");
                if (next != null) {
                    for (JsonNode nextNodeId : next) {
                        String nextNodeIdText = nextNodeId.asText();
                        Node toNode = regSequence.getNode(nextNodeIdText);
                        if (toNode != null) {
                            ensureDecisionNode(beNode, toNode, regSequence);
                        } else {
                            System.err.println("Warning: Next node ID not found: " + nextNodeIdText);
                        }
                    }
                }
                return;

            case "EXECUTOR":
                processExecutorAction(action, beNode, actionDetail, regSequence);
                break;

            case "RULE":
                processRuleAction(action, beNode, regSequence);
                break;

            default:
                System.err.println("Warning: Unsupported action type: " + actionType);
                break;
        }

        // Process next nodes for other types
        JsonNode next = action.get("next");
        if (next != null) {
            for (JsonNode nextNodeId : next) {
                String nextNodeIdText = nextNodeId.asText();
                Node toNode = regSequence.getNode(nextNodeIdText);
                if (toNode != null) {
                    ensureDecisionNode(beNode, toNode, regSequence);
                } else {
                    System.err.println("Warning: Next node ID not found: " + nextNodeIdText);
                }
            }
        }
    }

    private void processExecutorAction(JsonNode action, Node beNode, JsonNode actionDetail, RegSequence regSequence) {
        // Handle executor-specific logic
        String executorNodeId = action.get("id").asText();
        Node executorNode = new Node(executorNodeId, "executor");
        regSequence.addNode(executorNode);
        beNode.setNextNode(executorNode.getId());
    }

    private void processRuleAction(JsonNode action, Node beNode, RegSequence regSequence) {
        // Handle rule-specific logic
        String ruleNodeId = action.get("id").asText();
        Node ruleNode = new Node(ruleNodeId, "rule");
        regSequence.addNode(ruleNode);
        beNode.setNextNode(ruleNode.getId());
    }

    /**
     * Ensures a decision node is created if multiple nodes lead to the same target.
     */
    private void ensureDecisionNode(Node fromNode, Node toNode, RegSequence regSequence) {
        if (fromNode.getNextNode() != null && !fromNode.getNextNode().equals(toNode.getId())) {
            String decisionNodeId = "decision-" + fromNode.getId() + "-" + toNode.getId();
            Node decisionNode = regSequence.getNode(decisionNodeId);

            // If the decision node doesn't already exist, create it
            if (decisionNode == null) {
                decisionNode = new Node(decisionNodeId, "decision");
                regSequence.addNode(decisionNode);
            }

            // Link existing `nextNode` and new `toNode` to the decision node
            decisionNode.addNextNode(fromNode.getNextNode());
            decisionNode.addNextNode(toNode.getId());

            fromNode.setNextNode(decisionNode.getId());
        } else {
            fromNode.setNextNode(toNode.getId());
        }
    }

    /**
     * Ensures there is only one starting node in the sequence.
     */
    private void ensureSingleStartingNode(RegSequence regSequence) {
        // Identify nodes that are not referenced as `nextNode`
        Set<String> referencedNodes = new HashSet<>();
        for (Node node : regSequence.getNodes().values()) {
            if (node.getNextNode() != null) {
                referencedNodes.add(node.getNextNode());
            }
            referencedNodes.addAll(node.getNextNodes());
        }

        List<Node> startingNodes = new ArrayList<>();
        for (Node node : regSequence.getNodes().values()) {
            if (!referencedNodes.contains(node.getId())) {
                startingNodes.add(node);
            }
        }

        // If more than one starting node, create a decision node
        if (startingNodes.size() > 1) {
            String decisionNodeId = "decision-starting-node";
            Node decisionNode = new Node(decisionNodeId, "decision");

            for (Node startingNode : startingNodes) {
                decisionNode.addNextNode(startingNode.getId());
            }

            regSequence.addNode(decisionNode);
        }
    }
}
