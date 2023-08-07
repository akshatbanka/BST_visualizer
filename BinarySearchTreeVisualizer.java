import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class BinarySearchTreeVisualizer extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private BinarySearchTree bst;
    private TreePanel treePanel;

    public BinarySearchTreeVisualizer() {
        setTitle("Binary Search Tree Visualizer");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        bst = new BinarySearchTree();
        treePanel = new TreePanel(bst);
        add(treePanel);

        JPanel inputPanel = new JPanel();
        JTextField inputField = new JTextField(10);
        JButton insertButton = new JButton("Insert");
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputField.getText();
                try {
                    int value = Integer.parseInt(input);
                    bst.insert(value);
                    treePanel.repaint();
                    inputField.setText("");
                    inputField.requestFocus();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(BinarySearchTreeVisualizer.this,
                            "Invalid input! Please enter an integer value.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel traversalPanel = new JPanel();
        JButton preorderButton = new JButton("Preorder");
        preorderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                treePanel.updateTraversals();
                String preorder = bst.preorderTraversal();
                JOptionPane.showMessageDialog(BinarySearchTreeVisualizer.this,
                        "Preorder Traversal: " + preorder,
                        "Traversal Result", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton inorderButton = new JButton("Inorder");
        inorderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                treePanel.updateTraversals();
                String inorder = bst.inorderTraversal();
                JOptionPane.showMessageDialog(BinarySearchTreeVisualizer.this,
                        "Inorder Traversal: " + inorder,
                        "Traversal Result", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton postorderButton = new JButton("Postorder");
        postorderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                treePanel.updateTraversals();
                String postorder = bst.postorderTraversal();
                JOptionPane.showMessageDialog(BinarySearchTreeVisualizer.this,
                        "Postorder Traversal: " + postorder,
                        "Traversal Result", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        traversalPanel.add(preorderButton);
        traversalPanel.add(inorderButton);
        traversalPanel.add(postorderButton);
        add(traversalPanel, BorderLayout.NORTH);
        inputPanel.add(inputField);
        inputPanel.add(insertButton);
        add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BinarySearchTreeVisualizer::new);
    }

    private class TreePanel extends JPanel {
        private BinarySearchTree bst;
        private int nodeSize = 30;
        private int levelHeight = 80;
        private int initialX;
        private int initialY;

        private List<Integer> preorderList;
        private List<Integer> inorderList;
        private List<Integer> postorderList;

        public TreePanel(BinarySearchTree bst) {
            this.bst = bst;
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            initialX = getWidth() / 2;
            initialY = 50;

            updateTraversals();
        }

        public void updateTraversals() {
            preorderList = new ArrayList<>();
            inorderList = new ArrayList<>();
            postorderList = new ArrayList<>();
            preorderRecursive(bst.getRoot(), preorderList);
            inorderRecursive(bst.getRoot(), inorderList);
            postorderRecursive(bst.getRoot(), postorderList);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int treeWidth = calculateTreeWidth(bst.getRoot());
            initialX = (getWidth() - treeWidth) / 2;
            drawNode(g, bst.getRoot(), 0, initialX, initialY);
        }

        private void drawNode(Graphics g, BinarySearchTree.Node node, int level, int x, int y) {
            if (node == null) {
                return;
            }

            int offset = nodeSize / 2;

            g.setColor(Color.RED);
            g.fillOval(x - offset, y - offset, nodeSize, nodeSize);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            String value = String.valueOf(node.getValue());
            int stringWidth = g.getFontMetrics().stringWidth(value);
            g.drawString(value, x - stringWidth / 2, y + 5);

            if (node.getLeft() != null) {
                int leftX = x - calculateTreeWidth(node.getLeft()) / 2;
                int leftY = y + levelHeight;
                g.setColor(Color.BLACK);
                g.drawLine(x, y + offset, leftX + offset, leftY);
                drawNode(g, node.getLeft(), level + 1, leftX, leftY);
            }

            if (node.getRight() != null) {
                int rightX = x + calculateTreeWidth(node.getRight()) / 2;
                int rightY = y + levelHeight;
                g.setColor(Color.BLACK);
                g.drawLine(x, y + offset, rightX + offset, rightY);
                drawNode(g, node.getRight(), level + 1, rightX, rightY);
            }
        }

        private int calculateTreeWidth(BinarySearchTree.Node node) {
            if (node == null) {
                return 0;
            }
            int leftWidth = calculateTreeWidth(node.getLeft());
            int rightWidth = calculateTreeWidth(node.getRight());
            return leftWidth + nodeSize + rightWidth;
        }

        private void preorderRecursive(BinarySearchTree.Node node, List<Integer> list) {
            if (node != null) {
                list.add(node.getValue());
                preorderRecursive(node.getLeft(), list);
                preorderRecursive(node.getRight(), list);
            }
        }

        private void inorderRecursive(BinarySearchTree.Node node, List<Integer> list) {
            if (node != null) {
                inorderRecursive(node.getLeft(), list);
                list.add(node.getValue());
                inorderRecursive(node.getRight(), list);
            }
        }

        private void postorderRecursive(BinarySearchTree.Node node, List<Integer> list) {
            if (node != null) {
                postorderRecursive(node.getLeft(), list);
                postorderRecursive(node.getRight(), list);
                list.add(node.getValue());
            }
        }
    }

    private class BinarySearchTree {
        private Node root;

        public String preorderTraversal() {
            StringBuilder result = new StringBuilder();
            preorderRecursive(root, result);
            return result.toString();
        }

        private void preorderRecursive(Node node, StringBuilder result) {
            if (node != null) {
                result.append(node.getValue()).append(" ");
                preorderRecursive(node.getLeft(), result);
                preorderRecursive(node.getRight(), result);
            }
        }

        public String inorderTraversal() {
            StringBuilder result = new StringBuilder();
            inorderRecursive(root, result);
            return result.toString();
        }

        private void inorderRecursive(Node node, StringBuilder result) {
            if (node != null) {
                inorderRecursive(node.getLeft(), result);
                result.append(node.getValue()).append(" ");
                inorderRecursive(node.getRight(), result);
            }
        }

        public String postorderTraversal() {
            StringBuilder result = new StringBuilder();
            postorderRecursive(root, result);
            return result.toString();
        }

        private void postorderRecursive(Node node, StringBuilder result) {
            if (node != null) {
                postorderRecursive(node.getLeft(), result);
                postorderRecursive(node.getRight(), result);
                result.append(node.getValue()).append(" ");
            }
        }

        public void insert(int value) {
            root = insertRecursive(root, value);
        }

        private Node insertRecursive(Node node, int value) {
            if (node == null) {
                return new Node(value);
            }

            if (value < node.getValue()) {
                node.setLeft(insertRecursive(node.getLeft(), value));
            } else if (value > node.getValue()) {
                node.setRight(insertRecursive(node.getRight(), value));
            }

            return node;
        }

        public Node getRoot() {
            return root;
        }

        private class Node {
            private int value;
            private Node left;
            private Node right;

            public Node(int value) {
                this.value = value;
            }

            public int getValue() {
                return value;
            }

            public Node getLeft() {
                return left;
            }

            public void setLeft(Node left) {
                this.left = left;
            }

            public Node getRight() {
                return right;
            }

            public void setRight(Node right) {
                this.right = right;
            }
        }
    }
}
