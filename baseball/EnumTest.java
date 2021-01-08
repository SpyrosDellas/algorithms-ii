/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */


import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class EnumTest {

    private static class DFA {

        private EnumMap<State, Map<String, State>> next;

        private enum State {
            START(false) {
                @Override
                public Map<String, State> transition() {
                    Map<String, State> transition = new HashMap<>();
                    transition.put("space", START);
                    transition.put("digit", INTEGER);
                    transition.put("dot", DOT);
                    transition.put("sign", SIGN);
                    return transition;
                }
            },
            SIGN(false) {
                @Override
                public Map<String, State> transition() {
                    Map<String, State> transition = new HashMap<>();
                    transition.put("digit", INTEGER);
                    transition.put("dot", DOT);
                    return transition;
                }
            },
            INTEGER(true) {
                @Override
                public Map<String, State> transition() {
                    Map<String, State> transition = new HashMap<>();
                    transition.put("dot", DECIMAL);
                    transition.put("digit", INTEGER);
                    transition.put("epsilon", EPSILON);
                    transition.put("space", END);
                    return transition;
                }
            },
            DOT(false) {
                @Override
                public Map<String, State> transition() {
                    Map<String, State> transition = new HashMap<>();
                    transition.put("digit", DECIMAL);
                    return transition;
                }
            },
            DECIMAL(true) {
                @Override
                public Map<String, State> transition() {
                    Map<String, State> transition = new HashMap<>();
                    transition.put("epsilon", EPSILON);
                    transition.put("digit", DECIMAL);
                    transition.put("space", END);
                    return transition;
                }
            },
            EPSILON(false) {
                @Override
                public Map<String, State> transition() {
                    Map<String, State> transition = new HashMap<>();
                    transition.put("digit", EXPONENT);
                    transition.put("sign", EXP_SIGN);
                    return transition;
                }
            },
            EXPONENT(true) {
                @Override
                public Map<String, State> transition() {
                    Map<String, State> transition = new HashMap<>();
                    transition.put("digit", EXPONENT);
                    transition.put("space", END);

                    return transition;
                }
            },
            EXP_SIGN(false) {
                @Override
                public Map<String, State> transition() {
                    Map<String, State> transition = new HashMap<>();
                    transition.put("digit", EXPONENT);
                    return transition;
                }
            },
            END(true) {
                @Override
                public Map<String, State> transition() {
                    Map<String, State> transition = new HashMap<>();
                    transition.put("space", END);
                    return transition;
                }
            };

            private boolean status;

            State(boolean status) {
                this.status = status;
            }

            public abstract Map<String, State> transition();
        }

        public DFA() {
            next = new EnumMap<>(State.class);
            for (State s : State.values()) {
                next.put(s, s.transition());
            }
        }

        public boolean parse(String input) {
            State currentState = State.START;
            String type;
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                if (c == ' ') {
                    type = "space";
                }
                else if (c == '.') {
                    type = "dot";
                }
                else if (c == 'e') {
                    type = "epsilon";
                }
                else if (Character.isDigit(c)) {
                    type = "digit";
                }
                else if (c == '+' || c == '-') {
                    type = "sign";
                }
                else {
                    type = "unknown";
                }
                if (type.compareTo("unknown") == 0 || !currentState.transition()
                                                                   .containsKey(type)) {
                    return false;
                }
                currentState = currentState.transition().get(type);
            }

            return currentState.status;
        }
    }

    public static void main(String[] args) {
        DFA dfa = new DFA();
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            System.out.println(dfa.parse(sc.nextLine()));
        }
        sc.close();
    }
}
