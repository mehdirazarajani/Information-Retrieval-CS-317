/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkgboolean.ir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * @author Mehdi Raza Rajani
 */
public class BooleanIR {

    /**
     * @param args the command line arguments
     */

    static class storyInfo {
        String filename;
        String Title;
        String Author;
        @Override
        public String toString() {
            return filename + " Story \"" + Title + "\" by " + Author;
        }
    }

    
    static String fileDir = "storyFiles\\";
    public static Map<String, TupleInvertedIndex> invertedIndex = new HashMap<>(); 
    public static Map<String, TuplePositionalIndex> positionIndex = new HashMap<>();
    static ArrayList<storyInfo> stories = new ArrayList<>();
    static Integer totalStories = 50;

//    public static void main(String[] args) throws IOException {        
//        init_BooleanIr();
//    }
    
    static void init_BooleanIr() throws IOException{
        ArrayList<String> stopWords = new ArrayList(Arrays.asList(readStopWordFile(fileDir+"Stopword-List.txt").split(" ")));
        for (int i = 1; i <= totalStories; i++) {
            ArrayList<String> words = new ArrayList(Arrays.asList(readFile(fileDir+i+".txt").split(" ")));
            while (words.contains("")){
                words.remove("");
            }
            for (int j = 0; j < words.size(); j++) {
                if (!stopWords.contains(words.get(j))){
                    addWordPositiondIndex(words.get(j), i, j);
                    addWordInvertedIndex(words.get(j), i);
                }                
            }
        }        
        
        Set<String> invertedIndexKey = invertedIndex.keySet();        
        invertedIndexKey.forEach((key) -> {
            Collections.sort(invertedIndex.get(key).posting_list);
        });

    }
    
    static String readStopWordFile (String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            int index = 0;
            while (line != null) {
                line = line.toLowerCase().replaceAll("[^A-Za-z0-9 ]", " ");
                if (line.length() != 0 ){
                    sb.append(line);
                    sb.append(" ");
                }
                line = br.readLine();
                index++;
            }
            return sb.toString();
        }        
    }
    
    static String readFile(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            int index = 0;
            storyInfo story = new storyInfo();
            story.Title = line;
            while (line != null) {
                if (index == 1){
                    story.Author = line.replace("by ", "");
                }
                line = line.toLowerCase().replaceAll("[^A-Za-z0-9 ]", " ");
                if (line.length() != 0 ){
                    sb.append(line);
                    sb.append(" ");
                }
                line = br.readLine();
                index++;
            }
            if (!stories.contains(story)){
                story.filename = stories.size() + 1 + ".txt";
                stories.add(story);                
            }
            return sb.toString();
        }
    }
    
    static void addWordInvertedIndex(String word, Integer docId) {
        
        if (invertedIndex.containsKey(word)){
            TupleInvertedIndex t = invertedIndex.get(word);
            t.cucumulative_frequency++;
            t.posting_list.add(docId);
            invertedIndex.put(word, t);
        } else {
            ArrayList<Integer> arraylist = new ArrayList<>();
            arraylist.add(docId);
            invertedIndex.put(word, new TupleInvertedIndex(1,arraylist));            
        }
        
    }
    
    static void addWordPositiondIndex(String word, Integer docId, Integer pos) {
        
        if (!positionIndex.containsKey(word)){
            Set<Integer> set = new HashSet<>();
            set.add(pos);
            Map<Integer,Set<Integer>> map = new HashMap<>();
            map.put(docId, set);
            positionIndex.put(word, new TuplePositionalIndex(1,map));            
        }
        else if (!positionIndex.get(word).positional_posting_list.containsKey(docId)) {
            Set<Integer> set = new HashSet<>();
            set.add(pos);
            positionIndex.get(word).positional_posting_list.put(docId, set);
            positionIndex.get(word).cucumulative_frequency ++;
        }
        else {
            positionIndex.get(word).cucumulative_frequency++;
            positionIndex.get(word).positional_posting_list.get(docId).add(pos);
        }
        
    }

    static Set<Integer> interset(String word1,String word2) {
        Set<Integer> intersecting = new HashSet<>();
        Set<Integer> primary = new HashSet<>();
        Set<Integer> secondary = new HashSet<>();
        if ( invertedIndex.get(word1).cucumulative_frequency > invertedIndex.get(word2).cucumulative_frequency ) {
            primary.addAll(invertedIndex.get(word1).posting_list);
            secondary.addAll(invertedIndex.get(word2).posting_list);
        } else {
            primary.addAll(invertedIndex.get(word2).posting_list);
            secondary.addAll(invertedIndex.get(word1).posting_list);
        }
        System.out.println(word1+": "+invertedIndex.get(word1));
        System.out.println(word2+": "+invertedIndex.get(word2));
        primary.stream().filter((p) -> (secondary.contains(p))).forEachOrdered((p) -> {
            intersecting.add(p);
        });
        
        return intersecting;
    }
    
    static Set<Integer> interset(Set<Integer> primary , Set<Integer> secondary) {
        Set<Integer> intersecting = new HashSet<>();
        primary.stream().filter((p) -> (secondary.contains(p))).forEachOrdered((p) -> {
            intersecting.add(p);
        });
        return intersecting;
    }

    static Map<Integer, Set<Integer>> evaluteProximityQuery(Map<Integer, Set<Integer>>  primary , Map<Integer, Set<Integer>>  secondary, Integer gap) {
        Map<Integer, Set<Integer>> intersecting = new HashMap<>();
        Set<Integer> primaryKey = primary.keySet();
//        Set<Integer> secondaryKey = secondary.keySet();
        for(Integer k : primaryKey){
            if (secondary.get(k) != null) {
                for(Integer k1 : primary.get(k)){
                    if (secondary.get(k).contains(k1 + gap + 1)) {
                        
                        if (!intersecting.containsKey(k)){
                            Set<Integer> set = new HashSet<>();
                            set.add(k1+gap+1);
                            intersecting.put(k, set);
                        }
                        else
                            intersecting.get(k).add(k1+gap+1);
//                        break;
                    }
                }
            }
        }
        return intersecting;
    }
    
    static Set<Integer> union(String word1,String word2) {
        Set<Integer> unionSet = new HashSet<>();
        Set<Integer> primary = new HashSet<>();
        Set<Integer> secondary = new HashSet<>();
        primary.addAll(invertedIndex.get(word1).posting_list);
        secondary.addAll(invertedIndex.get(word2).posting_list);
        System.out.println(word1+": "+invertedIndex.get(word1));
        System.out.println(word2+": "+invertedIndex.get(word2));
        unionSet.addAll(primary);
        unionSet.addAll(secondary);
        return unionSet;
    }

    static Set<Integer> union(Set<Integer> primary , Set<Integer> secondary) {
        Set<Integer> unionSet = new HashSet<>();
        if (!primary.contains(-1))
            unionSet.addAll(primary);
        if (!secondary.contains(-1))
            unionSet.addAll(secondary);
        return unionSet;
    }
    
    static Set<Integer> negation(String word) {
        Set<Integer> neg = new HashSet<>();
        for (int i = 0; i < totalStories; i++) {
            if (!invertedIndex.get(word).posting_list.contains(i)) 
                neg.add(i);
        }
        return neg;
    }
    
    static Set<Integer> negation(Set<Integer> word) {
        Set<Integer> neg = new HashSet<>();
        for (int i = 1; i <= totalStories; i++)
            neg.add(i);
        word.forEach((i) -> {
            neg.remove(i);
        });
        return neg;
    }

    static String queryParser(String st){
        String st1 = new String();
        st = st.toLowerCase();
        if(st.startsWith("not")){
            st1 = "#!";
            st = st.substring(4);
        }
        ArrayList<String> stList = new ArrayList(Arrays.asList(st.split("((?=[() ])|(?<=[() ]))")));        
        for(String s : stList){
            if (" ".equals(s)) continue;
            if(!"and".equals(s) && !"not".equals(s) && !"or".equals(s) && !"(".equals(s) && !")".equals(s)){
                st1 += "#";
                if (invertedIndex.get(s) != null ){
                    ArrayList<Integer> a2 = invertedIndex.get(s).posting_list;
                    st1 = a2.stream().map((a) -> (a + ";")).reduce(st1, String::concat);
                    st1 = st1.substring(0, st1.length() - 1);        
                    st1 += "#";
                } else {
                    st1 += "-1#";
                }
            }
            else if ("(".equals(s))
                st1 += ("#"+s);
            else if (")".equals(s))
                st1 += (s+"#");
            else if (!st1.endsWith("#"))
                st1 += ("# " + s + " ");
            else 
                st1 += (" " + s + " ");                
        }
        st1 = st1.replace(" and ", "&&").replace(" or ", "||").replace(" not ", "!");
        return st1;
    }
    
    static Set<Integer> evaluteProximityQuery(String str){
        ArrayList<String> strList = new ArrayList(Arrays.asList(str.split(" ")));
        while (strList.contains(""))
            strList.remove("");
        ArrayList<Map<Integer, Set<Integer>>> expressionList = new ArrayList<>();
        ArrayList<Integer> Gaplist = new ArrayList<>();
        expressionList.add(positionIndex.get(strList.get(0)).positional_posting_list);
        for(int i = 1; i<strList.size(); i++){
            String s = strList.get(i);
            if (s.startsWith("/")) 
                Gaplist.add(Integer.valueOf(s.replace("/", "")));
            else if (!strList.get(i-1).startsWith("/")){
                Gaplist.add(0);
                expressionList.add(positionIndex.get(s).positional_posting_list);
            }
            else
                expressionList.add(positionIndex.get(s).positional_posting_list);
        }
        Map<Integer, Set<Integer>> resultant = expressionList.get(0);
        for (int i = 1; i < expressionList.size(); i++) {
            resultant = evaluteProximityQuery(resultant, expressionList.get(i), Gaplist.get(i));
            if (resultant.isEmpty())
                break;
        }
        return resultant.keySet();
    }
    
    // 0 simple - 1 inverted filename - 2 proximity -1 - invalide
    static Integer identifyQueryType(String st){
        String s = st.toLowerCase();
        if (!st.contains(" "))
            return 0;
        if ((s.contains(" and ") || s.contains(" or ") || s.contains(" not ") || s.startsWith("not ")) && !(s.contains("/") || isConsectiveString(s)))
            return 1;
        if ((s.contains("/") || isConsectiveString(s)) && !(s.contains(" and ") || s.contains(" or ") || s.contains(" not ") || s.startsWith("not ")))
            return 2;
        return -1;
    }
    
    static private Boolean isConsectiveString (String s){
//        if (s.contains(" and ") || s.contains(" or ") || s.contains(" not ") || s.startsWith("not ") || !s.contains(" "))
//            return false;
//        if (!s.contains("/"))
//            return false;
        ArrayList<String> strList = new ArrayList(Arrays.asList(s.split(" ")));
        for (int i = 0; i < strList.size()-1; i++) {
            if (!strList.get(i).contains("/") && !strList.get(i+1).contains("/") && !(strList.get(i).equals("and") || strList.get(i).equals("or") || strList.get(i).equals("not")) && !(strList.get(i+1).equals("and") || strList.get(i+1).equals("or") || strList.get(i+1).equals("not")))
                return true;
        }
        return false;
    }
    
    static ArrayList<storyInfo> evaluteQuery (String query) {
        ArrayList<storyInfo> resultStory = new ArrayList<>();
        Integer type = identifyQueryType(query);
        if (null == type) {
        } else switch (type) {
            case 0:
                resultStory.clear();
                Set<Integer> set = new HashSet<>();
                if (!invertedIndex.containsKey(query.toLowerCase()))
                    return resultStory;
                invertedIndex.get(query.toLowerCase()).posting_list.forEach((i) -> {
                    set.add(i);
                });
                set.forEach((i) -> {
                    resultStory.add(stories.get(i-1));
                }); break;
            case 1:
                resultStory.clear();
                ArrayList<String> resultList = new ArrayList(Arrays.asList(postfixToExpTree.evaluteTree(postfixToExpTree.constructTree(inffixToPostfix.infixToPostfix(queryParser(query)))).split(";")));
                if ("INVALID EXPRESSION".equals(resultList.get(0))){
                    System.out.println("abc");
                    return resultStory;                    
                }
                if (resultList.size() != 1 && !"".equals(resultList.get(0)))
                    resultList.forEach((i) -> {
                        resultStory.add(stories.get(Integer.valueOf(i)-1));
                    });
                break;
            case 2:
                Set<Integer> resultSet = evaluteProximityQuery(query.toLowerCase());
                resultStory.clear();
                resultSet.forEach((i) -> {
                    resultStory.add(stories.get(i-1));
                }); break;
            default:
                System.out.println("Invalid Query");
                break;
        }
        return resultStory;
    }
}
