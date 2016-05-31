package edu.columbia.ccls.util;


import java.util.HashMap;
import java.util.TreeMap;

public class GraphFeatureExplorer {
	
	private int documentId;
	private String word;
	private String outFilename;
	//private Fragment graph;
	private Graph graph;
	private int h = 1;
	private StringBuilder currentFeatureSB = new StringBuilder();
	private HashMap<String, HashMap<Integer, Integer>> wordDocsMap = new HashMap<String, HashMap<Integer, Integer>>();
	private TreeMap<Integer, TreeMap<String, Integer>> docWordFreqMap = new TreeMap<Integer, TreeMap<String,Integer>>();
	
	public String getOutFilename() {
		return outFilename;
	}

	public void setOutFilename(String outFilename) {
		this.outFilename = outFilename;
	}

	//public Fragment getGraph() {
	public Graph getGraph() {
		return graph;
	}

	//public void setGraph(Fragment graph) {
	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public int getDocumentId() {
		return documentId;
	}

	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	protected void explore(int p, int node) {
		if (p == 0) {
			currentFeatureSB.setLength(0);
			currentFeatureSB.append(graph.getAttributes().get(node));
		} else {
			currentFeatureSB.append("-").append(graph.getAttributes().get(node));
		}
		
		word = currentFeatureSB.toString();
		if (!wordDocsMap.containsKey(word)) {
			wordDocsMap.put(word, new HashMap<Integer, Integer>());
		}
		if (!wordDocsMap.get(word).containsKey(documentId)) {
			wordDocsMap.get(word).put(documentId, 1);
		} else {
			wordDocsMap.get(word).put(documentId, wordDocsMap.get(word).get(documentId) + 1);
		}
		
		if (p == h) {
			return;
		}
		
		for (int neighbor : graph.getGraph().get(node)) {
			explore(p + 1, neighbor);
			currentFeatureSB.setLength(currentFeatureSB.lastIndexOf("-"));
		}
		
	}
	
	public void exploreGraph() {
		for (int node = 0; node < graph.getGraph().size(); node++) {
			explore(0, node);
		}
	}
	
	public void writeOutput() {
		for (String word : wordDocsMap.keySet()) {
			HashMap<Integer, Integer> docs = wordDocsMap.get(word);
			for (int doc : docs.keySet()) {
				int freq = docs.get(doc);
				if (!docWordFreqMap.containsKey(doc)) {
					docWordFreqMap.put(doc, new TreeMap<String, Integer>());
				}
				docWordFreqMap.get(doc).put(word, freq);
			}
		}
		
		StringBuilder sb = new StringBuilder();
		for (int doc : docWordFreqMap.keySet()) {
			sb.append(doc);
			TreeMap<String, Integer> wordFreqMap = docWordFreqMap.get(doc);
			for (String word : wordFreqMap.keySet()) {
				int freq = wordFreqMap.get(word);
				sb.append(" " + word + ":" + freq);
			}
			sb.append("\n");
		}
		Tools.write(outFilename, sb.toString());
	}
	
	
	public static void main(String[] args) throws Exception {

		if (args.length != 2) {
			System.out.println("Wrong usage!");
			System.out.println("java GraphFeatureExplorer <input-graph-file> <output-linearized-graph>");
			System.exit(0);
		}

		String filename = args[0]; 
		String outFilename = args[1]; 
		int h = 1;
		
		GraphFeatureExplorer gfe = new GraphFeatureExplorer();
		gfe.setOutFilename(outFilename);
		gfe.setH(h);
		
		String[] lines = Tools.read(filename).split("\n");	
		for (int i = 0; i < lines.length; i++) {
			//Graph Fragment graph = new Graph Fragment(lines[i]);	
			Graph graph = new Graph(lines[i]);
			gfe.setDocumentId(i);
			gfe.setGraph(graph);
			gfe.exploreGraph();
		}
		gfe.writeOutput();
	}
}
