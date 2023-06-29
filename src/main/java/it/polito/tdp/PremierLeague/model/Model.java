package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	PremierLeagueDAO dao;
	Graph<Player, DefaultWeightedEdge> grafo;
	List<Player> vertici;
	
	Map<Integer, Player> idMapPlayer;
	List<Arco> archi;
	
	
	//punto 2 : ricorsione
	private List<Player> dreamTeam;
	private int bestDegree;  //titolarita'
	
	public Model() {
		this.dao = new PremierLeagueDAO();
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		this.idMapPlayer = new HashMap<Integer,Player>();
	}
	
	public void loadNodes(double goal) {
		
		if (vertici.isEmpty()) {
			this.vertici = dao.getVertici(goal);
		}
	}
	
	public void clearGraph() {
		
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		this.vertici = new ArrayList<>();
	}
	
	public void creaGrafo(double goal) {
		
		clearGraph();
		loadNodes(goal);
		
		Graphs.addAllVertices(this.grafo, this.vertici);
		
		//archi --> due nodi sono connessi se sono di squadra opposta, titoli 
		//peso = differenza tra i minuti di giocata 
		// orientamente : punta sul nodo con timePlayer minore
		
		for (Player p : vertici) {
			idMapPlayer.put(p.getPlayerID(), p);
		}
		
		archi = dao.getArchi(idMapPlayer) ;
		for (Arco a : archi) {
			//controllare se i players sono tra inodi del grafo
			if (this.grafo.containsVertex(a.getP1()) && this.grafo.containsVertex(a.getP2())) {
				if (a.getPeso() >0) {
					Graphs.addEdgeWithVertices(this.grafo, a.getP1(), a.getP2(), a.getPeso());
				}
				else if (a.getPeso() < 0){
					Graphs.addEdgeWithVertices(this.grafo, a.getP2(), a.getP1(), (double)-1*a.getPeso());
				}
			}
		
			
		}
	}
	
	public int getNumVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int getNumArchi() {
		return this.grafo.edgeSet().size();
	}
	
	//top-player
	public TopPlayer getTopPlayer() {
		
		if(this.grafo == null)
			return null;
		
		Player best = null;
		Integer maxDegree = Integer.MIN_VALUE;
		for(Player p : grafo.vertexSet()) {
			if(grafo.outDegreeOf(p) > maxDegree) {
				maxDegree = grafo.outDegreeOf(p);
				best = p;
			}
		}
		
		TopPlayer topPlayer = new TopPlayer();
		topPlayer.setTopPlayer(best);
		
		List<Opponente> opponents = new ArrayList<>();
		for(DefaultWeightedEdge edge : grafo.outgoingEdgesOf(topPlayer.getTopPlayer())) {
			opponents.add(new Opponente(grafo.getEdgeTarget(edge), (int) grafo.getEdgeWeight(edge)));
		}
		Collections.sort(opponents);
		topPlayer.setOpponenti(opponents);
		return topPlayer;
		
		
	}
	
	//PUNTO 2 : RICORSIONE
	
	
	//metodo di inizializzazione della ricorsione 
	
/*	public List<Player> cercaDreamTeam(int k) {
		
		//giocatori = massimizzare il grado di titolarita' ovvero la differenza tra peso Out e peso In
		// e nella squadra non ci devono essere i giocatori che ha battutto (= lista degli avversari)
		
		//ovviamentne i giocatori da scegliere sono quelli che si trovano nel grafo 
		List<Player> possibiliSoluzioni = new ArrayList<>(this.grafo.vertexSet());
		
		//
		this.bestDegree = 0;
		//inizializzare la lista dreamTeam
		this.dreamTeam = new ArrayList<Player>();
		
		//crerae la lista delle solzuioni parziali
		List<Player> parziale = new ArrayList<Player>();
		
		//invocare il metodo ricorsivo
		ricorsione(parziale, possibiliSoluzioni ,k); //livello == k 
		
		return dreamTeam;
	}
	
	private void ricorsione(List<Player> parziale, List<Player> possibiliSoluzioni, int k) {
		
		//condizioni di terminazione 
		
		//dimensione di parziale uguale a k
		if (parziale.size() == k) {
			
			//aggiornare i dati di output 
			int degree = getDegree(parziale);
			if (degree > this.bestDegree) {
				

				//parziale coincide con sol finale 
				this.dreamTeam = new ArrayList<Player>();  //e in questo punto che si riempie la soluzione finale
				this.dreamTeam.addAll(parziale);
				bestDegree = degree;
			}
			
			
			return;
		}
		
		
		//altrimenti riempire parziale
		
		//nel parziale si mettono i giocatori con titolarita' alta e no avversari battuti --> ovvero tutti coloro che sono connessi al nodo p
		
		for (Player p : possibiliSoluzioni) {
			
			if (!parziale.contains(p)) {
				//aggiungere il primo giocatore a caso 
				parziale.add(p);
				
				//togliere dalle solzuioni possibili gli avversari battuti --> meglio copiare la lista coss' da non perdere informazioni
				List<Player> playerRimanenti = new ArrayList<>(possibiliSoluzioni);
				
				//creare la lista dei battuti
				List<Player> battuti = new ArrayList<>(Graphs.successorListOf(this.grafo, p));
				playerRimanenti.removeAll(battuti);
				
				//invocare nuovamente il metodo ricorsivo
				ricorsione(parziale, playerRimanenti, k);
				//backtracking
				parziale.remove(p);
				
			}
			
		}
		
		
	}

	private int getDegree(List<Player> parziale) {
		// TODO Auto-generated method stub
		
		int degree =0;
		
		//per tenere traccia dei pesi in e out
		int in ;
		int out;
		
		for (Player p : parziale) {
			in = 0;
			out =0;
			for (DefaultWeightedEdge inE : this.grafo.incomingEdgesOf(p)) {
				in += (int)this.grafo.getEdgeWeight(inE); 
			}
			
			for (DefaultWeightedEdge outE : this.grafo.outgoingEdgesOf(p)) {
				out += (int) this.grafo.getEdgeWeight(outE); 
			}
			
			degree += (out - in);
		}
		
		
		
		return degree;
	}
		*/
	
	public List<Player> getDreamTeam(int k){
		List<Player> possibiliSoluzioni = new ArrayList<Player>(this.grafo.vertexSet());
		this.bestDegree = 0;
		this.dreamTeam = new ArrayList<Player>();
		List<Player> partial = new ArrayList<Player>();
																			//livello == k --> man mano che si inseriscono player
		this.recursive(partial, possibiliSoluzioni, k);

		return dreamTeam;
	}
	
	public void recursive(List<Player> partial, List<Player> players, int k) {
		if(partial.size() == k) {
			int degree = this.getDegree(partial);
			if(degree > this.bestDegree) {
				dreamTeam = new ArrayList<>(partial);
				bestDegree = degree;
			}
			return;
		}
		
		for(Player p : players) {
			if(!partial.contains(p)) {
				partial.add(p);
				//i "battuti" di p non possono più essere considerati
				List<Player> remainingPlayers = new ArrayList<>(players);
				remainingPlayers.removeAll(Graphs.successorListOf(grafo, p));
				recursive(partial, remainingPlayers, k);
				partial.remove(p);
				
			}
		}
	}
	
	private int getDegree(List<Player> team) {
		int degree = 0;
		int in;
		int out;

		for(Player p : team) {
			in = 0;
			out = 0;
			for(DefaultWeightedEdge edge : this.grafo.incomingEdgesOf(p))
				in += (int) this.grafo.getEdgeWeight(edge);
			
			for(DefaultWeightedEdge edge : grafo.outgoingEdgesOf(p))
				out += (int) grafo.getEdgeWeight(edge);
		
			degree += (out-in);
		}
		return degree;
	}

	public Integer getBestDegree() {
		return bestDegree;
	}

	
	

}
