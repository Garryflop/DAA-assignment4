package org.harryfloppa.util;

import org.harryfloppa.graph.Graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for loading graphs from JSON files.
 */
public class GraphLoader {

    public static GraphData loadFromJSON(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder json = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            // Remove comments (lines starting with //)
            if (!line.trim().startsWith("//")) {
                json.append(line).append(" ");
            }
        }
        reader.close();
        
        String content = json.toString();
        
        // Parse JSON manually (simple approach without external libraries)
        boolean directed = content.contains("\"directed\": true") || 
                          content.contains("\"directed\":true");
        
        int n = extractInt(content, "\"n\"\\s*:\\s*(\\d+)");
        int source = extractInt(content, "\"source\"\\s*:\\s*(\\d+)");
        
        String weightModel = extractString(content, "\"weight_model\"\\s*:\\s*\"([^\"]+)\"");
        
        Graph graph = new Graph(n, directed);
        
        // Extract edges
        Pattern edgePattern = Pattern.compile("\\{\\s*\"u\"\\s*:\\s*(\\d+)\\s*,\\s*\"v\"\\s*:\\s*(\\d+)\\s*,\\s*\"w\"\\s*:\\s*(\\d+)\\s*\\}");
        Matcher matcher = edgePattern.matcher(content);
        
        while (matcher.find()) {
            int u = Integer.parseInt(matcher.group(1));
            int v = Integer.parseInt(matcher.group(2));
            int w = Integer.parseInt(matcher.group(3));
            graph.addEdge(u, v, w);
        }
        
        return new GraphData(graph, source, weightModel);
    }
    
    private static int extractInt(String content, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(content);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }
    
    private static String extractString(String content, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(content);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    public static class GraphData {
        private final Graph graph;
        private final int source;
        private final String weightModel;
        
        public GraphData(Graph graph, int source, String weightModel) {
            this.graph = graph;
            this.source = source;
            this.weightModel = weightModel;
        }
        
        public Graph getGraph() {
            return graph;
        }
        
        public int getSource() {
            return source;
        }
        
        public String getWeightModel() {
            return weightModel;
        }
    }
}
