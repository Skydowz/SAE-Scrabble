import java.util.Random;

public class Scrabble {

    private Joueur[] joueurs;
    private int numJoueur; // joueur courant (entre 0 et joueurs.length-1)
    private Plateau plateau;
    private MEE sac;
    private static final int[] nbPointsJeton = { 1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 10, 1, 2, 1, 1, 3, 8, 1, 1, 1, 1, 4, 10,
            10, 10, 10 };

    public Scrabble(String[] nomsJoueurs) {
        Random random = new Random();
        int[] occSac = { 9, 2, 2, 3, 15, 2, 2, 2, 1, 1, 5, 3, 6, 6, 2, 1, 6, 6, 6, 6, 2, 1, 1, 1, 1, 1 };
        int[] tabStart = new int[nomsJoueurs.length];
        this.joueurs = new Joueur[nomsJoueurs.length];
        for (int i = 0; i < nomsJoueurs.length; i++) {
            this.joueurs[i] = new Joueur(nomsJoueurs[i]);
        }
        this.numJoueur = 0;
        for (int i = 0; i<this.joueurs.length;i++){
            int lettre;
            do{
                lettre = random.nextInt(26);
            }while (occSac[lettre]<1);
            tabStart[i] = lettre;
            occSac[lettre] --;
            if(tabStart[i]<tabStart[this.numJoueur]) this.numJoueur = i;
        }
        this.plateau = new Plateau();
        this.sac = new MEE(occSac);
    }

    public String toString() {
        return this.plateau.toString() + "\n\n" + this.joueurs[this.numJoueur].toString();
    }

    public void partie() {
        for (int i = 0; i < this.joueurs.length; i++) {
            this.joueurs[i].prendJetons(this.sac, 7);
        }
        int followedPass = 0;
        boolean continuer = true;
        while (followedPass < this.joueurs.length && continuer) {
            Utils.sleep(2500);
            System.out.print("\n\n" + this);
            int action = this.joueurs[numJoueur].joue(this.plateau, this.sac, nbPointsJeton);
            if (action == -1) followedPass++;
            else if (action == 1 && this.sac.estVide()) continuer = false;
            else if (action == 1) {
                this.joueurs[numJoueur].prendJetons(this.sac, 7);
                followedPass = 0;
            } else followedPass = 0;

            // Choix prochain joueur.
            if (numJoueur == this.joueurs.length - 1)
                numJoueur = 0;
            else
                numJoueur++;
        }

        if (followedPass >= this.joueurs.length) {
            for (Joueur j : this.joueurs) {
                j.ajouteScore(-j.nbPointsChevalet(nbPointsJeton));
            }
        } else {
            for (int i = 0; i < this.joueurs.length; i++) {
                if (i != this.numJoueur) {
                    this.joueurs[i].ajouteScore(-this.joueurs[i].nbPointsChevalet(nbPointsJeton));
                }
                this.joueurs[this.numJoueur].ajouteScore(this.joueurs[i].nbPointsChevalet(nbPointsJeton));
            }
        }

        this.printWinners();
    }

    private void printWinners() {
        int scoreMax = this.joueurs[0].getScore();
        for (int i = 0; i < this.joueurs.length; i++) {
            if (this.joueurs[i].getScore() > scoreMax)
                scoreMax = this.joueurs[i].getScore();
        }

        System.out.println(Utils.BLUE + "Gagnants: \n" + Utils.RESET);
        for (int i = 0; i < this.joueurs.length; i++) {
            if (this.joueurs[i].getScore() == scoreMax) System.out.println(this.joueurs[i].toString());
        }   
    }
}
