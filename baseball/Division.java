/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 19/06/2020
 *
 * Description:
 * Supporting class for BaseballElimination.java
 * Contains the statistics and supports queries for a baseball division
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;

import java.util.Arrays;
import java.util.HashMap;


public final class Division {

    private final int numberOfTeams;          // number of teams in the division
    private final String leader;              // leading team
    private final int leaderWins;             // leading team wins
    private final HashMap<String, Integer> index;  // teams[index.get(team)] = team
    private final String[] teams;           // teams[i] -> ith team in the division
    private final int[][] stats;                   // team stats
    private final int[][] remaining;  // number of remaining games between teams


    public Division(String filename) {

        In in = new In(filename);
        numberOfTeams = Integer.parseInt(in.readLine());
        index = new HashMap<>();
        teams = new String[numberOfTeams];
        stats = new int[numberOfTeams][3];
        remaining = new int[numberOfTeams][numberOfTeams];

        // parse the entries in the input file
        String[] entries = in.readAllLines();
        int wins = 0;
        String best = "";
        for (int i = 0; i < numberOfTeams; i++) {
            String[] cols = entries[i].trim().split("\\s+");
            index.put(cols[0], i);
            teams[i] = cols[0];
            stats[i][0] = Integer.parseInt(cols[1]);  // wins
            stats[i][1] = Integer.parseInt(cols[2]);  // losses
            stats[i][2] = Integer.parseInt(cols[3]);  // remaining games
            if (stats[i][0] > wins) {
                wins = stats[i][0];
                best = cols[0];
            }
            for (int j = 4; j < cols.length; j++) {
                remaining[i][j - 4] = Integer.parseInt(cols[j]);
            }
        }
        leaderWins = wins;
        leader = best;

    }


    public boolean contains(String team) {
        return index.containsKey(team);
    }


    public int numberOfTeams() {
        return numberOfTeams;
    }


    public String leader() {
        return leader;
    }


    public int leaderWins() {
        return leaderWins;
    }


    public int indexOf(String team) {
        if (!contains(team))
            throw new IllegalArgumentException("Invalid team name: " + team);

        return index.get(team);
    }


    public String team(int i) {
        return teams[i];
    }


    public Iterable<String> teams() {
        Queue<String> q = new Queue<>();
        for (String team : teams)
            q.enqueue(team);
        return q;
    }


    public int wins(String team) {
        if (!contains(team))
            throw new IllegalArgumentException("Invalid team name: " + team);

        return stats[indexOf(team)][0];
    }


    public int losses(String team) {
        if (!contains(team))
            throw new IllegalArgumentException("Invalid team name: " + team);

        return stats[indexOf(team)][1];
    }


    public int remaining(String team) {
        if (!contains(team))
            throw new IllegalArgumentException("Invalid team name: " + team);

        return stats[indexOf(team)][2];
    }


    public int against(String team1, String team2) {
        if (!contains(team1))
            throw new IllegalArgumentException("Invalid team name: " + team1);
        if (!contains(team2))
            throw new IllegalArgumentException("Invalid team name: " + team2);

        return remaining[indexOf(team1)][indexOf(team2)];
    }


    public static void main(String[] args) {

        Division division = new Division("teams24.txt");
        for (String team : division.teams()) {
            System.out.println(team + ": " + division.wins(team) + " "
                                       + division.losses(team) + " "
                                       + division.remaining(team));
        }
        for (int[] row : division.remaining) {
            System.out.println(Arrays.toString(row));
        }

    }

}
