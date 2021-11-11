package com.graphicalcsvprocessing.graphicalcsvprocessing.models;

import org.springframework.lang.Nullable;

public class Tree<T> {

    private final TreeNode<T> top;

    public Tree(TreeNode<T> top) {
        this.top = top;
    }

    public TreeNode<T> getTop() {
        return top;
    }

    public static class TreeNode<T> {

        private final T element;
        private final TreeNode<T> left;
        private final TreeNode<T> right;

        public TreeNode(T element, @Nullable TreeNode<T> left, @Nullable TreeNode<T> right) {
            this.element = element;
            this.left = left;
            this.right = right;
        }

        public T getElement() {
            return element;
        }

        public TreeNode<T> getLeft() {
            return left;
        }

        public TreeNode<T> getRight() {
            return right;
        }
    }
}


