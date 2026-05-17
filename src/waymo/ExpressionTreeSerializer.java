package waymo;
/*
Serialize an expression tree to an infix string with the MINIMUM number
of parentheses such that re-parsing under standard precedence + left
associativity recovers the same value.

A node is either:
  * a leaf  - a variable or a literal (any non-operator string), OR
  * an inner node - one of '+', '-', '*', '/' with a left and right child.

"Minimum parentheses" rule (the only thing that's interesting):
  When emitting an inner node `parent` with children L and R, parens are
  needed around L iff prec(L) < prec(parent), and around R iff
       prec(R) < prec(parent),  OR
       prec(R) == prec(parent) AND parent is non-commutative-from-the-right
                                      (i.e. parent is '-' or '/').
  Equivalently:
       L parens : strictly-lower precedence
       R parens : strictly-lower OR same-precedence-with-{-,/}-parent

Why "same-precedence-with-{-,/}-parent" needs parens on R:
  Left-associativity makes `a - b - c` mean `(a - b) - c`.  If a tree
  has the shape `parent='-' , R='-'(b,c)` and we drop the right parens
  we'd serialize `a - b - c`, which re-parses to `(a-b)-c` — different
  value from the original `a - (b - c)`.  Same logic for '/'.
  '+' and '*' don't have this problem because they're associative under
  ordinary arithmetic: `a + (b + c) == (a + b) + c`, so dropping the
  right parens gives an equivalent value.

This file also includes:
  * a fully-parenthesized serializer (baseline / debugging),
  * a parser that round-trips serialized strings back into trees,
  * an integer evaluator,
  * hand-crafted tests + a 200-iter random fuzz that builds random trees,
    serialises minimally, parses the result back, and confirms both trees
    evaluate to the same value under a random variable assignment.

Complexity
  serialize / serializeFull / evaluate : O(n) on tree size
  parse                                : O(n) on token count (shunting yard)
*/

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ExpressionTreeSerializer {

    /** Expression-tree node: leaf if {@code left == null && right == null}. */
    public static final class Node {
        public final String value;
        public final Node left;
        public final Node right;

        /** Leaf factory. */
        public static Node leaf(String name) {
            return new Node(name, null, null);
        }

        /** Inner-node factory. */
        public static Node op(String op, Node left, Node right) {
            if (precOrMinus(op) < 0) throw new IllegalArgumentException("not an operator: " + op);
            if (left == null || right == null) throw new IllegalArgumentException("op needs both children");
            return new Node(op, left, right);
        }

        private Node(String value, Node left, Node right) {
            this.value = value;
            this.left = left;
            this.right = right;
        }

        public boolean isLeaf() {
            return left == null && right == null;
        }

        @Override
        public String toString() {
            return isLeaf() ? value : "(" + left + " " + value + " " + right + ")";
        }
    }

    /* --------------------------- Min-parens serializer --------------------------- */

    /** Returns an infix string with the minimum parentheses needed to preserve value. */
    public String serialize(Node root) {
        if (root == null) return "";
        StringBuilder sb = new StringBuilder();
        write(root, sb);
        return sb.toString();
    }

    private void write(Node node, StringBuilder sb) {
        if (node.isLeaf()) {
            sb.append(node.value);
            return;
        }
        writeChild(node.left, node.value, /*isRight*/ false, sb);
        sb.append(' ').append(node.value).append(' ');
        writeChild(node.right, node.value, /*isRight*/ true, sb);
    }

    private void writeChild(Node child, String parentOp, boolean isRight, StringBuilder sb) {
        boolean parens = !child.isLeaf() && needsParens(child.value, parentOp, isRight);
        if (parens) sb.append('(');
        write(child, sb);
        if (parens) sb.append(')');
    }

    private static boolean needsParens(String childOp, String parentOp, boolean isRight) {
        int cp = prec(childOp);
        int pp = prec(parentOp);
        if (cp < pp) return true;
        if (cp == pp && isRight && (parentOp.equals("-") || parentOp.equals("/"))) return true;
        return false;
    }

    private static int prec(String op) {
        int p = precOrMinus(op);
        if (p < 0) throw new IllegalArgumentException("not an operator: " + op);
        return p;
    }

    /** Returns precedence (1 for + -, 2 for * /), or -1 if {@code op} isn't an operator. */
    private static int precOrMinus(String op) {
        if (op == null || op.length() != 1) return -1;
        switch (op.charAt(0)) {
            case '+':
            case '-': return 1;
            case '*':
            case '/': return 2;
            default: return -1;
        }
    }

    /* --------------------------- Fully-parenthesised baseline --------------------------- */

    /** Always wraps every inner node in parens.  Useful as a sanity reference. */
    public String serializeFull(Node root) {
        if (root == null) return "";
        if (root.isLeaf()) return root.value;
        return "(" + serializeFull(root.left) + " " + root.value + " " + serializeFull(root.right) + ")";
    }

    /* --------------------------- Parser (for round-trip testing) --------------------------- */

    /**
     * Parse an infix expression with single-char operators '+', '-', '*', '/' and
     * parentheses '(' ')'.  Variable / literal tokens are any maximal run of
     * non-whitespace, non-paren, non-operator characters.  Standard precedence,
     * left-associative.  Throws on malformed input.
     */
    public Node parse(String s) {
        List<String> tokens = tokenize(s);
        Deque<Node> operands = new ArrayDeque<>();
        Deque<String> ops = new ArrayDeque<>();
        for (String tok : tokens) {
            if (tok.equals("(")) {
                ops.push(tok);
            } else if (tok.equals(")")) {
                while (!ops.isEmpty() && !ops.peek().equals("(")) {
                    popAndCombine(operands, ops);
                }
                if (ops.isEmpty()) throw new IllegalArgumentException("unmatched ')'");
                ops.pop(); // discard '('
            } else if (precOrMinus(tok) >= 0) {
                // left-associative: pop while top has >= precedence
                while (!ops.isEmpty() && !ops.peek().equals("(") && prec(ops.peek()) >= prec(tok)) {
                    popAndCombine(operands, ops);
                }
                ops.push(tok);
            } else {
                operands.push(Node.leaf(tok));
            }
        }
        while (!ops.isEmpty()) {
            if (ops.peek().equals("(")) throw new IllegalArgumentException("unmatched '('");
            popAndCombine(operands, ops);
        }
        if (operands.size() != 1) throw new IllegalArgumentException("malformed expression");
        return operands.pop();
    }

    private static void popAndCombine(Deque<Node> operands, Deque<String> ops) {
        String op = ops.pop();
        if (operands.size() < 2) throw new IllegalArgumentException("operator missing operand: " + op);
        Node right = operands.pop();
        Node left = operands.pop();
        operands.push(Node.op(op, left, right));
    }

    private static List<String> tokenize(String s) {
        List<String> out = new ArrayList<>();
        int i = 0, n = s.length();
        while (i < n) {
            char c = s.charAt(i);
            if (Character.isWhitespace(c)) { i++; continue; }
            if (c == '(' || c == ')' || c == '+' || c == '-' || c == '*' || c == '/') {
                out.add(String.valueOf(c));
                i++;
            } else {
                int j = i;
                while (j < n) {
                    char cj = s.charAt(j);
                    if (Character.isWhitespace(cj) || cj == '(' || cj == ')'
                            || cj == '+' || cj == '-' || cj == '*' || cj == '/') break;
                    j++;
                }
                out.add(s.substring(i, j));
                i = j;
            }
        }
        return out;
    }

    /* --------------------------- Evaluator (for value-equivalence) --------------------------- */

    /**
     * Evaluate the tree as a {@code double}.  Doubles (not ints) so that the
     * "drop parens for * and / on the right" rule — which assumes real-number
     * associativity — actually holds when validating a round-trip.  Under
     * integer division it doesn't: e.g. {@code a * (b / c) != (a * b) / c}.
     */
    public double evaluate(Node node, Map<String, Double> env) {
        if (node.isLeaf()) {
            // try literal first; else look up as variable
            try { return Double.parseDouble(node.value); } catch (NumberFormatException e) { /* fall through */ }
            Double v = env.get(node.value);
            if (v == null) throw new IllegalStateException("unbound: " + node.value);
            return v;
        }
        double l = evaluate(node.left, env);
        double r = evaluate(node.right, env);
        switch (node.value) {
            case "+": return l + r;
            case "-": return l - r;
            case "*": return l * r;
            case "/": return l / r;       // double division: 0 -> +/- Infinity (no exception)
            default: throw new IllegalStateException("bad op: " + node.value);
        }
    }

    /** Equality check tolerant of floating-point reordering and NaN/Infinity propagation. */
    private static boolean approxEqual(double a, double b) {
        if (Double.isNaN(a) && Double.isNaN(b)) return true;
        if (Double.isInfinite(a) || Double.isInfinite(b)) return a == b; // same-sign infinity ok
        double diff = Math.abs(a - b);
        double scale = Math.max(Math.max(Math.abs(a), Math.abs(b)), 1.0);
        return diff <= 1e-9 * scale;
    }

    /* --------------------------- Demo + tests --------------------------- */

    public static void main(String[] args) {
        ExpressionTreeSerializer s = new ExpressionTreeSerializer();

        // Hand-crafted trees and the expected min-parens output.
        check(s, Node.leaf("a"), "a");
        check(s, op("+", "a", "b"), "a + b");

        // Higher-precedence parent forces parens on lower-precedence left child.
        check(s, Node.op("*", op("+", "a", "b"), Node.leaf("c")), "(a + b) * c");

        // Higher-precedence right child does NOT need parens.
        check(s, Node.op("+", Node.leaf("a"), op("*", "b", "c")), "a + b * c");

        // Same precedence on the left: no parens (left-associative).
        check(s, Node.op("-", op("-", "a", "b"), Node.leaf("c")), "a - b - c");

        // Same precedence on the right under '-' parent: NEEDS parens.
        check(s, Node.op("-", Node.leaf("a"), op("-", "b", "c")), "a - (b - c)");

        // Same precedence on the right under '+' parent: NO parens.
        // a + (b + c)  ==  a + b + c  (associative)
        check(s, Node.op("+", Node.leaf("a"), op("+", "b", "c")), "a + b + c");
        // a + (b - c)  ==  (a + b) - c
        check(s, Node.op("+", Node.leaf("a"), op("-", "b", "c")), "a + b - c");

        // '-' parent, '+' right child at equal prec -> NEEDS parens.
        // a - (b + c)  != (a - b) + c
        check(s, Node.op("-", Node.leaf("a"), op("+", "b", "c")), "a - (b + c)");

        // '/' parent, '*' right child at equal prec -> NEEDS parens.
        // a / (b * c)  != (a / b) * c
        check(s, Node.op("/", Node.leaf("a"), op("*", "b", "c")), "a / (b * c)");

        // '*' parent, '/' right child at equal prec -> NO parens.
        // a * (b / c)  ==  (a * b) / c (under exact rationals/integers)
        check(s, Node.op("*", Node.leaf("a"), op("/", "b", "c")), "a * b / c");

        // Deep nesting on the left with mixed precedence.
        // ((a + b) * c) - d  -> "(a + b) * c - d"
        check(s,
                Node.op("-",
                        Node.op("*",
                                op("+", "a", "b"),
                                Node.leaf("c")),
                        Node.leaf("d")),
                "(a + b) * c - d");

        // Right-leaning tree of subtractions: every right child needs parens.
        // a - (b - (c - d))
        check(s,
                Node.op("-", Node.leaf("a"),
                        Node.op("-", Node.leaf("b"),
                                op("-", "c", "d"))),
                "a - (b - (c - d))");

        // ---------- Round-trip: parse(serialize(tree)) evaluates to the same value ----------

        Random rnd = new Random(1234);
        int mismatches = 0;
        for (int t = 0; t < 200; t++) {
            Node root = randomTree(rnd, /*depth*/ 4);
            Map<String, Double> env = new HashMap<>();
            for (char v : new char[]{'a', 'b', 'c', 'd', 'e'}) {
                env.put(String.valueOf(v), (double) (1 + rnd.nextInt(9)));
            }

            String emitted = s.serialize(root);
            Node reparsed;
            try {
                reparsed = s.parse(emitted);
            } catch (Exception ex) {
                mismatches++;
                System.out.println("PARSE FAIL on '" + emitted + "': " + ex);
                continue;
            }
            double expected = s.evaluate(root, env);
            double actual = s.evaluate(reparsed, env);
            if (!approxEqual(expected, actual)) {
                mismatches++;
                System.out.println("VALUE MISMATCH:");
                System.out.println("  tree     = " + root);
                System.out.println("  emitted  = " + emitted);
                System.out.println("  expected = " + expected);
                System.out.println("  actual   = " + actual);
            }
        }
        System.out.println("Round-trip fuzz: " + (200 - mismatches) + "/200 ok");

        // ---------- Show min vs full on a couple of trees ----------
        Node demo = Node.op("-",
                Node.op("*", op("+", "a", "b"), Node.leaf("c")),
                op("/", "d", "e"));
        System.out.println();
        System.out.println("min  : " + s.serialize(demo));
        System.out.println("full : " + s.serializeFull(demo));
    }

    /* --------------------------- Tiny demo helpers --------------------------- */

    private static Node op(String o, String l, String r) {
        return Node.op(o, Node.leaf(l), Node.leaf(r));
    }

    private static void check(ExpressionTreeSerializer s, Node tree, String expected) {
        String got = s.serialize(tree);
        boolean ok = expected.equals(got);
        System.out.println((ok ? "OK   " : "FAIL ")
                + "expected='" + expected + "'  got='" + got + "'  full='" + s.serializeFull(tree) + "'");
    }

    private static Node randomTree(Random rnd, int depth) {
        if (depth == 0 || rnd.nextInt(3) == 0) {
            // leaf: use only "safe" divisors (no zeros) since the evaluator does integer division
            return Node.leaf(String.valueOf((char) ('a' + rnd.nextInt(5))));
        }
        String[] OPS = {"+", "-", "*", "/"};
        // bias slightly away from '/' to cut down on integer-division collisions in fuzz output
        String op = OPS[rnd.nextInt(OPS.length)];
        return Node.op(op, randomTree(rnd, depth - 1), randomTree(rnd, depth - 1));
    }

    /** Pretty-print helper for debugging.  Not used in tests but convenient at a REPL. */
    static String dump(Node n) {
        return Arrays.toString(new String[]{n.toString()});
    }
}
