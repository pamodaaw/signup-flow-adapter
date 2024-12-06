//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ArrayNode;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//public class RegistrationPageExtractor {
//
//    public Map<String, Page> getPages(JsonNode fullJsonNode) throws Exception {
//
//        Map<String, Page> pageDetails = new HashMap<>();
//
//        JsonNode blocks = fullJsonNode.get("blocks");
//        JsonNode elements = fullJsonNode.get("elements");
//
//        JsonNode jsonNodes = fullJsonNode.get("nodes");
//        if (jsonNodes == null || !jsonNodes.isArray()) {
//            throw new IllegalArgumentException("Invalid JSON: 'nodes' is missing or not an array.");
//        }
//
//        for (JsonNode jNode : jsonNodes) {
//            ArrayNode elementsArray = (ArrayNode) jNode.get("elements");
//            String jnodeId = jNode.get("id").asText();
//            String prevNodeId = null;
//
//            for (Node node : sequence.getNodes().values()) {
//                if (node.getPageIds().containsValue(jnodeId)) {
//                    prevNodeId = node.getId();
//                    System.out.println("Info: Found node " + prevNodeId + " with pageId " + jnodeId);
//                }
//            }
//
//            List<Node> nextActionNodes = new ArrayList<>();
//
//            for (JsonNode element : elementsArray) {
//                String elementName = element.asText();
//
//                if (elementName == null) {
//                    continue;
//                }
//                // 1. Skip elements starting with "flow-display"
//                if (elementName.startsWith("flow-display")) {
//                    continue;
//                }
//                // 2. Handle "flow-block-attributes" or elements containing "attributes"
//                if (elementName.startsWith("flow-block-attributes") || elementName.contains("attributes")) {
//                    Node attributeNode = createAttributeCollectorNode(element);
//                    sequence.addNode(attributeNode);
//                    continue;
//                }
//
//                // 3. Handle "flow-action" elements
//                if (elementName.startsWith("flow-action")) {
//                    if (actions != null && actions.isArray()) {
//                        for (JsonNode action : actions) {
//                            if (action == null){
//                                System.out.println("Info: Action null for " + elementName);
//                                continue;
//                            }
//                            String actionType = action.get("type").asText();
//                            if (actionType.equals("attributeCollectorEX")) {
//                                Node attributeNode = createAttributeCollectorNode(action);
//                                sequence.addNode(attributeNode);
//                                nextActionNodes.add(attributeNode);
//                            } else if (actionType.equals("executor")) {
//                                Node executorNode = createExecutorNode(action);
//                                sequence.addNode(executorNode);
//                                nextActionNodes.add(executorNode);
//                            } else if (actionType.equals("rule")) {
//                                Node ruleNode = createRuleNode(action);
//                                sequence.addNode(ruleNode);
//                                nextActionNodes.add(ruleNode);
//                            } else if (actionType.equals("decision")) {
//                                Node decisionNode = createDecisionNode(action);
//                                sequence.addNode(decisionNode);
//                                nextActionNodes.add(decisionNode);
//                            }
//                        }
//                    }
//                }
//            }
//
//            // 4. Handle "flow-decision" elements
//            if (jNode.get("type").
//
//        }
//
//        // Find the page details
//        JsonNode selectedPage = null;
//        for (JsonNode page : flow.get("pages")) {
//            if (page.get("id").asText().equals(pageId)) {
//                selectedPage = page;
//                break;
//            }
//        }
//
//        // Collect relevant node IDs
//        Set<String> nodeIds = new HashSet<>();
//        selectedPage.get("nodes").forEach(node -> nodeIds.add(node.asText()));
//
//        // Collect relevant nodes
//        Set<String> elementIds = new HashSet<>();
//        ArrayNode relevantNodes = mapper.createArrayNode();
//        for (JsonNode node : nodes) {
//            if (nodeIds.contains(node.get("id").asText())) {
//                relevantNodes.add(node);
//                node.get("elements").forEach(element -> elementIds.add(element.asText()));
//            }
//        }
//
//        // Collect relevant elements
//        ArrayNode relevantElements = mapper.createArrayNode();
//        for (JsonNode element : elements) {
//            if (elementIds.contains(element.get("id").asText())) {
//                relevantElements.add(element);
//            }
//        }
//
//        // Collect relevant block IDs from elements
//        Set<String> blockIds = new HashSet<>();
//        for (JsonNode node : relevantNodes) {
//            if (node.has("actions")) {
//                for (JsonNode action : node.get("actions")) {
//                    if (action.has("action") && action.get("action").has("boundTo")) {
//                        blockIds.add(action.get("action").get("boundTo").asText());
//                    }
//                }
//            }
//        }
//
//        // Collect relevant blocks
//        ArrayNode relevantBlocks = mapper.createArrayNode();
//        for (JsonNode block : blocks) {
//            if (blockIds.contains(block.get("id").asText())) {
//                relevantBlocks.add(block);
//            }
//        }
//
//        // Construct the output JSON
//        ObjectNode result = mapper.createObjectNode();
//        result.put("flowId", "d13ec8d2-2d1e-11ee-be56-0242ac120002"); // Example flowId
//        result.put("flowStatus", "INCOMPLETE");
//        result.put("flowType", "REGISTRATION");
//        result.set("elements", relevantElements);
//        result.set("blocks", relevantBlocks);
//
//        // Print the result
//        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
//    }
//}
