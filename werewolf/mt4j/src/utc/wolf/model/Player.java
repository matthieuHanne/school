package utc.wolf.model;

public class Player {
	String name;
	int nbVotes;

public Player(String name) {
	super();
	this.name = name;
}

public String getName() {
	return name;
}

public int getNbVotes() {
	return nbVotes;
}

public void setNbVotes(int nbVotes) {
	this.nbVotes = nbVotes;
}
public String toString() {
	return name;
}

}
