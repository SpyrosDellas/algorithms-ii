/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 24/07/2020
 *
 * Description:
 * You are given a collection of strings w₁, w₂, …, wₖ whose combined total
 * length is n.
 * Design an O(n)-time algorithm that determines whether any of these strings
 * are prefixes of one another. As usual, argue that your algorithm is correct
 * and meets this runtime bound.
 *
 * Solution:
 * - Create and sort the suffix array of the string:
 *     $w1$w2$...$wk
 *   using the SA-IS algorithm
 * - Calculate the lcp array again in linear time from the suffix array using
 *   Kasai's algorithm
 * - Scan the lcp array once and determine if the longest common prefix of
 *   two adjacent suffixes (starting with $) in the suffix array equals the
 *   length of one of the strings in the given collection
 *
 **************************************************************************** */

public class PrefixWords {

    private String s;    // the single string
    private int n;       // the length of the single string
    private int[] sa;    // the suffix array of the single string
    private int[] lcp;   // the lcp array of the single string
    private boolean[] isSeparator; // the positions of the separating characters


    public PrefixWords(String[] collection) {

        // Join the words into a single string using a unique separating
        // sentinel string of the form $xxx$, where xxx is a unique numerical id
        // The $ is used as a unique sentinel character, smaller than any other
        // character in the input collection alphabet
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for (String word : collection) {
            sb.append('$');
            sb.append(counter++);
            sb.append('$');
            sb.append(word.toLowerCase());
        }
        sb.append('$');
        sb.append(counter);
        sb.append('$');
        s = sb.toString();

        n = s.length();   // the length of the single string

        // Scan the single string once and mark the positions of the separating
        // characters
        isSeparator = new boolean[s.length()];
        boolean isChar = true;
        for (int i = 0; i < n; i++) {
            if (s.charAt(i) == '$') {
                isSeparator[i] = true;
                isChar = !isChar;
                continue;
            }
            if (!isChar)
                isSeparator[i] = true;
        }

        // Create the suffix and lcp arrays in O(n) time using SA-IS and Kasai's
        // algorithms respectively
        SAIS sais = new SAIS(s, 255);
        sa = sais.sa();
        lcp = sais.lcp();
    }


    public void findPrefixWords() {

        // Scan the lcp array once and find all words that are prefixes of
        // another word
        for (int i = 0; i < lcp.length; i++) {
            int start = sa[i];

            if (start >= n - 1 || isSeparator[start + 1])
                continue;

            if (s.charAt(start) != '$')
                break;

            int longest = lcp[i];
            if (longest <= 1)
                continue;

            int end = start + longest - 1;

            if (isSeparator[end]) {
                while (s.charAt(end) != '$')
                    end--;
                String word1 = s.substring(start + 1, end);

                int start2 = sa[i + 1];
                int end2 = start2 + longest - 1;
                if (isSeparator[end2]) {
                    while (s.charAt(end2) != '$')
                        end2--;
                }
                else {
                    while (s.charAt(end2) != '$')
                        end2++;
                }
                String word2 = s.substring(start2 + 1, end2);
                System.out.println(word1 + " IS A PREFIX OF " + word2);
            }
            else if (s.charAt(end + 1) == '$') {
                String word1 = s.substring(start + 1, end + 1);

                int start2 = sa[i + 1];
                int end2 = start2 + longest - 1;
                if (isSeparator[end2]) {
                    while (s.charAt(end2) != '$')
                        end2--;
                }
                else {
                    while (s.charAt(end2) != '$')
                        end2++;
                }
                String word2 = s.substring(start2 + 1, end2);
                System.out.println(word1 + " IS A PREFIX OF " + word2);
            }
        }
    }


    public static void main(String[] args) {

        String s = "Circumambulate the city of a dreamy Sabbath afternoon. Go from\n"
                + "Corlears Hook to Coenties Slip, and from thence, by Whitehall,\n"
                + "northward. What do you see?- Posted like silent sentinels all around\n"
                + "the town, stand thousands upon thousands of mortal men fixed in ocean\n"
                + "reveries. Some leaning against the spiles; some seated upon the\n"
                + "pier-heads; some looking over the bulwarks of ships from China; some\n"
                + "high aloft in the rigging, as if striving to get a still better\n"
                + "seaward peep. But these are all landsmen; of week days pent up in\n"
                + "lath and plaster- tied to counters, nailed to benches, clinched to\n"
                + "desks. How then is this? Are the green fields gone? What do they\n"
                + "here?";

        String t = "Andy one banana is less than one and a half bananas";

        String[] words = t.trim().split("\\s+");
        PrefixWords pw = new PrefixWords(words);

        pw.findPrefixWords();

        /*
        // Print the suffix array (for debugging)
        for (int i = 1; i < sa.length; i++) {
            System.out.println(s.substring(sa[i]));
        }
        System.out.println("*************************************************");
         */

    }
}
