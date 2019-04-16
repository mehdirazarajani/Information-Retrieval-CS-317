/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkgboolean.ir;

import java.util.ArrayList;
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
//    public static Map<String, TupleInvertedIndex> invertedIndex = new HashMap<>(); 
    public static Map<String, TuplePositionalIndex> positionIndex = new HashMap<>();
    static ArrayList<storyInfo> stories = new ArrayList<>();
    static Integer totalStories = 50;

//    public static void main(String[] args) throws IOException {        
//        init_BooleanIr();
//    }
    
    static void init_BooleanIr() throws IOException{
        ArrayList<String> stopWords = new ArrayList(Arrays.asList(readStopWordFile("Stopword-List.txt").split(" ")));
        for (int i = 1; i <= totalStories; i++) {
            ArrayList<String> words = new ArrayList(Arrays.asList(readFile(fileDir+i+".txt").split(" ")));
            while (words.contains("")){
                words.remove("");
            }
            for (int j = 0; j < words.size(); j++) {
                if (!stopWords.contains(words.get(j)))
                    addWordPositiondIndex(words.get(j), i, j);                
            }
        }        
        
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
        
    static Set<Integer> evaluteProximityQuery(String str){
        str = str.toLowerCase().replaceAll("[^A-Za-z0-9 ]", " ");
        ArrayList<String> strList = new ArrayList(Arrays.asList(str.split(" ")));
        while (strList.contains(""))
            strList.remove("");
        ArrayList<Map<Integer, Set<Integer>>> expressionList = new ArrayList<>();
        if (positionIndex.get(strList.get(0)) == null) 
            return new HashSet<>();
        expressionList.add(positionIndex.get(strList.get(0)).positional_posting_list);
        for(int i = 1; i<strList.size(); i++){
            String s = strList.get(i);
            expressionList.add(positionIndex.get(s).positional_posting_list);
        }
        Map<Integer, Set<Integer>> resultant = expressionList.get(0);
        for (int i = 1; i < expressionList.size(); i++) {
            resultant = evaluteProximityQuery(resultant, expressionList.get(i),0);
            if (resultant.isEmpty())
                break;
        }
        return resultant.keySet();
    }
            
    static public ArrayList<storyInfo> evaluteQuery (String query) {
        ArrayList<storyInfo> resultStory = new ArrayList<>();
        Set<Integer> resultSet = evaluteProximityQuery(query.toLowerCase());
        resultStory.clear();
        resultSet.forEach((i) -> {
            resultStory.add(stories.get(i-1));
        });
        return resultStory;
    }
}
