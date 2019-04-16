/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkgboolean.ir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 *
 * @author Mehdi Raza Rajani
 */
class Node { 
    String value; 
    Node left, right; 

    Node(String item) { 
        value = item; 
        left = right = null; 
    } 
} 

public class postfixToExpTree {
    
    static boolean isOperator (String s){
        if ("!".equals(s) || "&&".equals(s) || "||".equals(s)) { 
            return true; 
        } 
        return false; 
    }
    
    static void inorder(Node t) { 
        if (t != null) { 
            inorder(t.left); 
            System.out.print(t.value + " "); 
            inorder(t.right); 
        }
    }

    static Node constructTree(String postfix){
        if ("INVALID EXPRESSION".equals(postfix))
            return null;
        Stack<Node> stack = new Stack<>();
        Node t, t1, t2;
        ArrayList<String> pfList = new ArrayList(Arrays.asList(postfix.split("#")));
//        System.out.println(pfList);
        for(String s : pfList){
            if(!isOperator(s)){
                t = new Node(s);
                stack.push(t);
            }
            else {
                t = new Node(s);
                t1 = stack.pop();
                t.left = t1;
                if (!stack.isEmpty() && !"!".equals(s)){
                    t2 = stack.pop();   
                    t.right = t2;
                }
//                else {
//                    t.right = new Node("");
//                }
                stack.push(t);
            }
        }
        t = stack.peek(); 
        stack.pop(); 
        return t; 
    }
    
    static String evaluteTree (Node root) {
        if (root == null)
            return "INVALID EXPRESSION";
        if ("".equals(root.value))
            return "";
        if (root.left == null && root.right == null)
            return root.value;
//        if ("".equals(root.right.value)){
//            System.out.println("/" + root.value + "/");
//            return root.value;            
//        }
        //only not operator to be done
//        if ("!".equals(root.value) && root.right == null){
//            //work to be done here
//            BooleanIR.negation(root.left.value);
//        }
        // Evaluate left subtree 
        String leftVal = "";
        if (root.left != null && !"".equals(root.left.value))
            leftVal = evaluteTree(root.left);
        String rightVal = "";
        if (root.right != null && !"".equals(root.right.value))
            rightVal = evaluteTree(root.right);
        
        if ("&&".equals(root.value)){
            Set<Integer> primary = new HashSet<>();
            Set<Integer> secondary = new HashSet<>();
            ArrayList<String> abc = new ArrayList(Arrays.asList(leftVal.split(";")));
            abc.forEach((t) -> {
                primary.add(Integer.parseInt(t));
            });
            ArrayList<String> abc1 = new ArrayList(Arrays.asList(rightVal.split(";")));
            abc1.forEach((t) -> {
                secondary.add(Integer.parseInt(t));
            });
            Set<Integer> result = BooleanIR.interset(primary, secondary);
            String resultStr = new String();
            resultStr = result.stream().map((a) -> (a + ";")).reduce(resultStr, String::concat);
            return resultStr;
        }
        else if ("||".equals(root.value)){
            Set<Integer> primary = new HashSet<>();
            Set<Integer> secondary = new HashSet<>();
            ArrayList<String> abc = new ArrayList(Arrays.asList(leftVal.split(";")));
            abc.forEach((t) -> {
                primary.add(Integer.parseInt(t));
            });
            ArrayList<String> abc1 = new ArrayList(Arrays.asList(rightVal.split(";")));
//            System.out.println(abc1);
            abc1.forEach((t) -> {
                secondary.add(Integer.parseInt(t));
            });
            Set<Integer> result = BooleanIR.union(primary, secondary);
            String resultStr = new String();
            resultStr = result.stream().map((a) -> (a + ";")).reduce(resultStr, String::concat);
            return resultStr;            
        }
        Set<Integer> primary = new HashSet<>();
        ArrayList<String> abc = new ArrayList(Arrays.asList(leftVal.split(";")));
        abc.forEach((t) -> {
            primary.add(Integer.parseInt(t));
        });
        Set<Integer> result = BooleanIR.negation(primary);
        String resultStr = new String();
        resultStr = result.stream().map((a) -> (a + ";")).reduce(resultStr, String::concat);
        return resultStr;                   
    }
    
    static String evaluateBoolExpr(String s) {
        ArrayList<String> sList = new ArrayList(Arrays.asList(s.split("#")));
        sList.remove(0);
//        sList.remove(sList.size()-1);
        int n = sList.size();
//        System.out.println(n);
//        System.out.println(sList.get(0));
//        System.out.println(sList.get(n-1));
        
        for (int i = 0; i < n; i += 2) { 
        
            // If operator next to current operand is AND. 
            if( (i + 1 < n) && (i + 2 < n) ) { 
                if ( "&&".equals(sList.get(i+1)) ) { 
                    Set<Integer> primary = new HashSet<>();
                    Set<Integer> secondary = new HashSet<>();
                    ArrayList<String> abc = new ArrayList(Arrays.asList(sList.get(i).split(";")));
                    abc.forEach((t) -> {
                        primary.add(Integer.parseInt(t));
                    });
                    ArrayList<String> abc1 = new ArrayList(Arrays.asList(sList.get(i+2).split(";")));
                    abc1.forEach((t) -> {
                        secondary.add(Integer.parseInt(t));
                    });
                    Set<Integer> result = BooleanIR.interset(primary, secondary);
                    String resultStr = new String();
                    resultStr = result.stream().map((a) -> (a + " ")).reduce(resultStr, String::concat);
                    sList.add(i+2, resultStr);
                } 
           
                // If operator next to current operand is OR. 
                else if ("||".equals(sList.get(i+1))) {
                    Set<Integer> primary = new HashSet<>();
                    Set<Integer> secondary = new HashSet<>();
                    ArrayList<String> abc = new ArrayList(Arrays.asList(sList.get(i).split(";")));
                    abc.forEach((t) -> {
                        primary.add(Integer.parseInt(t));
                    });
                    ArrayList<String> abc1 = new ArrayList(Arrays.asList(sList.get(i+2).split(";")));
                    abc1.forEach((t) -> {
                        secondary.add(Integer.parseInt(t));
                    });
                    Set<Integer> result = BooleanIR.union(primary, secondary);
                    String resultStr = new String();
                    resultStr = result.stream().map((a) -> (a + " ")).reduce(resultStr, String::concat);
                    sList.add(i+2, resultStr);
                }                   
            } 
        } 
        return sList.get(n-1);
    } 

}
