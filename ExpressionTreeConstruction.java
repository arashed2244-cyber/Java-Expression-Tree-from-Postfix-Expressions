import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class ExpressionTreeConstruction<E> {

    static class TreeNode<E> {
        String value;
        TreeNode left;
        TreeNode right;

        TreeNode(String value) {
            this.value = value;
            this.left = null;
            this.right = null;
        }

        TreeNode(String value, TreeNode left, TreeNode right) {
            this.value = value;
            this.left = left;
            this.right = right;
        }

        boolean isLeaf() {
            return left == null && right == null;
        }
    }

    public static void main(String[] args) {
        String fileName = "in.dat";
        List<String> lines;

        try {
            lines = Files.readAllLines(Paths.get(fileName));
        } catch (IOException e) {
            System.out.println("Could not open file: " + fileName);
            return;
        }

        Scanner scanner = new Scanner(System.in);

        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }

            // original
            System.out.println("__________________________________________________________");
            System.out.println("Original Expression: " + line);

            int dollarSign = line.indexOf('$');
            if (dollarSign != -1) {
                line = line.substring(0, dollarSign).trim();
            }

            try {
                TreeNode root = buildExpressionTree(line);

                // expression tree
                System.out.println("Expression Tree:");
                printTree(root, 0);

                // fully parenthesized infix expression
                System.out.println("Fully Parenthesized Infix Expression:");
                System.out.println(toInfix(root));

                // expression value
                int evaluatedValue = evaluateTree(root);
                System.out.println("Result:");
                System.out.println(evaluatedValue);

            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
            System.out.println("\nPress <Enter> to continue ...");
            scanner.nextLine();
        }
    }

    public static TreeNode buildExpressionTree(String expr) {
        if (expr.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty expression");
        }

        String[] tokens = expr.trim().split("\\s+");
        Stack<TreeNode> stack = new Stack<>();

        for (String token : tokens) {
            if (isInteger(token)) {
                stack.push(new TreeNode(token));
            } else if (isUnaryOperator(token)) {
                TreeNode operand = stack.pop();
                stack.push(new TreeNode(token, null, operand));
            } else if (isBinaryOperator(token)) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Insufficient operands");
                }
                TreeNode right = stack.pop();
                TreeNode left = stack.pop();
                stack.push(new TreeNode(token, left, right));
            } else {
                throw new IllegalArgumentException("Invalid token: " + token);
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Malformed postfix expression (unbalanced operands/operators)");
        }

        return stack.pop();
    }

    public static int evaluateTree(TreeNode node) {
        if (node == null) {
            throw new IllegalArgumentException("Null node");
        }

        if (node.isLeaf()) {
            return Integer.parseInt(node.value);
        }

        // Unary operator
        if (node.value.equals("!")) {
            int operandVal = evaluateTree(node.right);
            return (operandVal == 0) ? 1 : 0;
        }

        // Binary operators
        int leftVal = evaluateTree(node.left);
        int rightVal = evaluateTree(node.right);

        switch (node.value) {
            case "^":
                return (int) Math.pow(leftVal, rightVal);
            case "*":
                return leftVal * rightVal;
            case "/":
                if (rightVal == 0)
                    throw new ArithmeticException("Division by zero");
                return leftVal / rightVal;
            case "%":
                if (rightVal == 0)
                    throw new ArithmeticException("Modulus by zero");
                return leftVal % rightVal;
            case "+":
                return leftVal + rightVal;
            case "-":
                return leftVal - rightVal;
            case "<":
                return (leftVal < rightVal) ? 1 : 0;
            case "<=":
                return (leftVal <= rightVal) ? 1 : 0;
            case ">":
                return (leftVal > rightVal) ? 1 : 0;
            case ">=":
                return (leftVal >= rightVal) ? 1 : 0;
            case "==":
                return (leftVal == rightVal) ? 1 : 0;
            case "!=":
                return (leftVal != rightVal) ? 1 : 0;
            case "&&":
                return (leftVal != 0 && rightVal != 0) ? 1 : 0;
            case "||":
                return (leftVal != 0 || rightVal != 0) ? 1 : 0;
            default:
                throw new IllegalArgumentException("Unsupported operator: " + node.value);
        }
    }

    // fully parenthesized infix
    public static String toInfix(TreeNode node) {
        if (node == null) {
            return "";
        }
        if (node.isLeaf()) {
            return node.value;
        }
        if (node.value.equals("!")) {
            return "! ( " + toInfix(node.right) + " )";
        }
        return "( " + toInfix(node.left) + " " + node.value + " " + toInfix(node.right) + " )";
    }

    // tree printed
    public static void printTree(TreeNode node, int depth) {
        if (node == null)
            return;

        // right
        printTree(node.right, depth + 1);

        for (int i = 0; i < depth; i++) {
            System.out.print("    ");
        }
        System.out.println(node.value);

        // left
        printTree(node.left, depth + 1);
    }

    static boolean isInteger(String token) {
        return token.matches("-?\\d+");
    }

    static boolean isUnaryOperator(String token) {
        return token.equals("!");
    }

    static boolean isBinaryOperator(String token) {
        return token.matches("(\\+|-|\\*|/|%|\\^|<|<=|>|>=|==|!=|&&|\\|\\|)");
    }
}