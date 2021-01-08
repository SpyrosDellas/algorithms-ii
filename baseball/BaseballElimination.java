/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 18/06/2020
 *
 * Description:
 * In the baseball elimination problem, there is a division consisting of n
 * teams. At some point during the season, team i has w[i] wins, l[i] losses,
 * r[i] remaining games, and g[i][j] games left to play against team j.
 * A team is mathematically eliminated if it cannot possibly finish the season
 * in (or tied for) first place. The goal is to determine exactly which teams
 * are mathematically eliminated. For simplicity, we assume that no games end
 * in a tie (as is the case in Major League Baseball) and that there are no
 * rainouts (i.e., every scheduled game is played).
 * The problem is not as easy as many sports writers would have you believe, in
 * part because the answer depends not only on the number of games won and left
 * to play, but also on the schedule of remaining games.
 *
 * A maxflow formulation.
 * We can solve the baseball elimination problem by reducing it to the maxflow
 * problem. To check whether team x is eliminated, we consider two cases.
 * - Trivial elimination. If the maximum number of games team x can win is less
 * than the number of wins of some other team i, then team x is trivially
 * eliminated. That is, if w[x] + r[x] < w[i], then team x is mathematically
 * eliminated.
 * - Nontrivial elimination. Otherwise, we create a flow network and solve a
 * maxflow problem in it. In the network, feasible integral flows correspond to
 * outcomes of the remaining schedule. There are vertices corresponding to teams
 * (other than team x) and to remaining divisional games (not involving team x).
 * Intuitively, each unit of flow in the network corresponds to a remaining
 * game. As it flows through the network from s to t, it passes from a game
 * vertex, say between teams i and j, then through one of the team vertices i
 * or j, classifying this game as being won by that team.
 *
 * More precisely, the flow network includes the following edges and capacities:
 * - We connect an artificial source vertex s to each game vertex i-j and set
 * its capacity to g[i][j]. If a flow uses all g[i][j] units of capacity on
 * this edge, then we interpret this as playing all of these games, with the
 * wins distributed between the team vertices i and j.
 * - We connect each game vertex i-j with the two opposing team vertices to
 * ensure that one of the two teams earns a win. We do not need to restrict the
 * amount of flow on such edges.
 * - Finally, we connect each team vertex to an artificial sink vertex t. We want
 * to know if there is some way of completing all the games so that team x ends
 * up winning at least as many games as team i. Since team x can win as many
 * as w[x] + r[x] games, we prevent team i from winning more than that many
 * games in total, by including an edge from team vertex i to the sink vertex
 * with capacity w[x] + r[x] - w[i].
 * If all edges in the maxflow that are pointing from s are full, then this
 * corresponds to assigning winners to all of the remaining games in such a way
 * that no team wins more games than x. If some edges pointing from s are not
 * full, then there is no scenario in which team x can win the division.
 *
 * What the min cut tells us.
 * By solving a maxflow problem, we can determine whether a given team is
 * mathematically eliminated. We would also like to explain the reason for the
 * team's elimination to a friend in nontechnical terms (using only grade-school
 * arithmetic). In fact, when a team is mathematically eliminated there always
 * exists such a convincing certificate of elimination, where R is some subset
 * of the other teams in the division. Moreover, you can always find such a
 * subset R by choosing the team vertices on the source side of a min s-t cut
 * in the baseball elimination network. Note that although we solved a
 * maxflow/mincut problem to find the subset R, once we have it, the argument
 * for a team's elimination involves only grade-school algebra.
 *
 * Corner cases.
 * The last six methods throw an IllegalArgumentException if one (or both) of
 * the input arguments are invalid teams.
 *
 * We assume that n ≥ 1 and that the input files are in the specified format
 * and internally consistent. Note that a team's number of remaining games does
 * not necessarily equal the sum of the remaining games against teams in its
 * division because a team may play opponents outside its division.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.ResizingArrayStack;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;


public final class BaseballElimination {

    private final Division division;     // division team names and stats
    private final HashMap<String, ResizingArrayStack<String>> certificates;


    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {

        division = new Division(filename);
        certificates = new HashMap<>();

        if (division.numberOfTeams() == 1) {
            return;
        }

        eliminate();

        for (String team : division.teams()) {
            if (!certificates.containsKey(team)) {
                maxFlowEliminate(team);
            }
        }
    }


    private void maxFlowEliminate(String team) {

        int thisIndex = division.indexOf(team);
        int thisPotential = division.wins(team) + division.remaining(team);

        int n = division.numberOfTeams();
        int V = n * (n - 1) / 2 + n + 2; // number of vertices in the flow network
        int s = 0;                // source vertex
        int t = V - 1;            // sink vertex
        int teamsStart = n * (n - 1) / 2 + 1;

        FlowNetwork fn = new FlowNetwork(V);
        int maxFlow = 0;

        for (int i = 0; i < n; i++) {
            if (i == thisIndex)
                continue;
            String team1 = division.team(i);

            for (int j = i + 1; j < n; j++) {
                if (j == thisIndex)
                    continue;
                String team2 = division.team(j);
                int pairVertex = vertex(i, j);
                int gamesAgainst = division.against(team1, team2);

                if (gamesAgainst == 0)
                    continue;

                maxFlow += gamesAgainst;
                FlowEdge e1 = new FlowEdge(s, pairVertex, gamesAgainst);
                FlowEdge e2 = new FlowEdge(pairVertex, teamsStart + i, Double.POSITIVE_INFINITY);
                FlowEdge e3 = new FlowEdge(pairVertex, teamsStart + j, Double.POSITIVE_INFINITY);
                fn.addEdge(e1);
                fn.addEdge(e2);
                fn.addEdge(e3);

            }
            FlowEdge e4 = new FlowEdge(teamsStart + i, t, thisPotential - division.wins(team1));
            fn.addEdge(e4);
        }


        FordFulkerson ff = new FordFulkerson(fn, s, t);

        if (ff.value() == maxFlow) {
            return;
        }

        ResizingArrayStack<String> certificate = new ResizingArrayStack<>();
        for (int i = teamsStart; i < V - 1; i++) {
            if (ff.inCut(i)) {
                certificate.push(division.team(i - teamsStart));
            }
        }
        certificates.put(team, certificate);
    }


    // calculates the index of the vertex corresponding to the (row, col) pair
    // of teams in the flow network
    private int vertex(int row, int col) {
        int size = division.numberOfTeams();
        int v = 0;
        for (int i = 0; i < row; i++) {
            v += (size - 1 - i);
        }
        v += col - row;
        return v;
    }


    // carry out trivial elimination check for all teams in order to to avoid
    // costly max flow elimination checks for teams that can be trivially
    // eliminated
    private void eliminate() {

        ResizingArrayStack<String> certificate = new ResizingArrayStack<>();

        String leader = division.leader();
        int leaderWins = division.leaderWins();
        certificate.push(leader);

        for (String team : division.teams()) {
            if (division.wins(team) + division.remaining(team) < leaderWins) {
                certificates.put(team, certificate);
            }
        }
    }


    // number of teams
    public int numberOfTeams() {
        return division.numberOfTeams();
    }


    // all teams
    public Iterable<String> teams() {
        return division.teams();
    }


    // number of wins for given team
    public int wins(String team) {
        return division.wins(team);
    }


    // number of losses for given team
    public int losses(String team) {
        return division.losses(team);
    }


    // number of remaining games for given team
    public int remaining(String team) {
        return division.remaining(team);
    }


    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        return division.against(team1, team2);
    }


    // is given team eliminated?
    public boolean isEliminated(String team) {

        if (!division.contains(team))
            throw new IllegalArgumentException("Invalid team name: " + team);

        return certificates.containsKey(team);
    }


    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {

        if (!division.contains(team))
            throw new IllegalArgumentException("Invalid team name: " + team);

        // the team is not eliminated
        if (!certificates.containsKey(team))
            return null;

        return certificates.get(team);
    }


    // test client
    public static void main(String[] args) {

        BaseballElimination division = new BaseballElimination(args[0]);

        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }

    }
}
