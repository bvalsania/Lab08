package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	private SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafo;
	private Map<Integer, Airport> idMap;
	private ExtFlightDelaysDAO dao;
	
	
	public Model() {
	
		idMap = new HashMap<Integer, Airport>();
		dao= new ExtFlightDelaysDAO();
		dao.loadAllAirports(idMap);
		
	}
	
	
	public void creaGrafo(int distanzaMedia) {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//aggiungere i vertici
		Graphs.addAllVertices(grafo, idMap.values());
		
		for(Rotta r: dao.getRotta(idMap, distanzaMedia)) {
			DefaultWeightedEdge edge = grafo.getEdge(r.getA1(), r.getA2());
			
			//se l'arco non c' è ancora vado ad aggiungerlo al grafo
			if(edge == null) {
				Graphs.addEdge(grafo, r.getA1(), r.getA2(), r.getPeso());
			}else {
				//vado a prendere il peso dal grafo
				double peso = grafo.getEdgeWeight(edge);
				double newPeso = (peso + r.getPeso())/2;
				grafo.setEdgeWeight(edge, newPeso);
				}
		}
	}
	
	public int numVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int numArchi() {
		return this.grafo.edgeSet().size();
	}

	
	public List<Rotta> getRotta(){
		List<Rotta> rotte = new ArrayList<>();
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			rotte.add(new Rotta(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), this.grafo.getEdgeWeight(e)));
			
		}
		return rotte;
	}
}
