/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vectorspace.ir;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author Mehdi Raza Rajani
 */
public class VectorSpaceIR {
    
    static String fileDir = "storyFiles\\";
    static Integer totalStories = 50;
    static ArrayList<storyInfo> stories = new ArrayList<>();    
    static Map<String,tupleVectorSpace> allWord = new HashMap<>();
    static ArrayList<Double> idfScore = new ArrayList<>();
    static ArrayList<String> stopWords;
    static ArrayList<Score> scores = new ArrayList<>();    
    static Double alpha = 0.005;

    static class storyInfo {
        String filename;
        String Title;
        String Author;
        Vector<Integer> vector = new Vector<>();
        Vector<Double> tf_idfScore = new Vector<>();
        Double magnitude;
        @Override
        public String toString() {
            return filename + " Story \"" + Title + "\" by " + Author + " Document Vector: " + vector.toString();
        }
        
    }    
        
    static class tupleVectorSpace {
        Integer index;
        Integer totalOccurence = 0;
        Integer totalDocOccurence = 0;
//        Double idfScore;
        Map<Integer,Integer> documnentList = new HashMap<>();

        @Override
        public String toString(){
            return "Index: " + index + " totalOcc:" + totalOccurence + " Doc:(" + totalDocOccurence + ")" + documnentList;
        }

        public tupleVectorSpace(){}
        
        public tupleVectorSpace(Integer index, Integer Doc) {
            this.index = index;
            documnentList.put(Doc,1);
            totalDocOccurence = 1;
            totalOccurence = 1;
        }
    }
    
    static class Score {
        Integer index;
        Double score;

        public Score(Integer index, Double score) {
            this.score = score;
            this.index = index;
        }

        @Override
        public String toString() {
            return "Score{" + "index=" + index + ", score=" + score + '}';
        }        
    }    
    
    static void update(tupleVectorSpace tuple, Integer Doc) {
        tuple.totalOccurence ++;
        if (!tuple.documnentList.containsKey(Doc)) {
            tuple.documnentList.put(Doc,1);
            tuple.totalDocOccurence ++;
        } else {
            tuple.documnentList.put(Doc, tuple.documnentList.get(Doc)+1);
        }
    }
    
    public static void main(String args[]) throws IOException {
         
        initVectorSpaceModel();
        
        String query = "crowd busy";
        Vector<Integer> queryVector = queryParser(query);
        Vector<Double> queryTFIDFVector = new Vector<>();
        
        for (int j = 0; j < queryVector.size(); j++)
            queryTFIDFVector.add((queryVector.get(j))*(idfScore.get(j)));
                
        Double queryMagnitude = magnitudeVector(queryTFIDFVector);
        
        
        for (int i = 0; i < totalStories; i++) 
//            scores.add(new Score(i, (dotProduct(queryVector, stories.get(i).vector))/(queryMagnitude*stories.get(i).magnitude)));
//            scores.add(new Score(i, dotProduct1(queryVector, stories.get(i).tf_idfScore)));
            scores.add(new Score(i, (dotProduct1(queryVector, stories.get(i).tf_idfScore))/(queryMagnitude*stories.get(i).magnitude)));
                
        Collections.sort(scores, new Comparator<Score>() {
            @Override
            public int compare(Score s1, Score s2) {
                 return s2.score.compareTo(s1.score);
            }
        });
        
        for (Score sc:scores){
            if (sc.score  > alpha )
                System.out.println(stories.get(sc.index).filename + " " + sc.score);
//            else 
//                System.out.println(sc.score);

        }
        
//        System.out.println(scores);
        
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

    static void initVectorSpaceModel() throws IOException{
        stopWords = new ArrayList(Arrays.asList(readStopWordFile("Stopword-List.txt").split(" ")));

        for (int i = 1; i <= totalStories; i++) {
            ArrayList<String> words = new ArrayList(Arrays.asList(readFile(fileDir+i+".txt").split(" ")));
            Vector<Integer> vector = new Vector<>();
            for (int k = 0 ; k < allWord.size() ; k++) 
                vector.add(0);            
            while (words.contains("")){
                words.remove("");
            }
            for (int j = 0; j < words.size(); j++) {
                if (!stopWords.contains(words.get(j))){
                    String currentWord = words.get(j);
                    if (allWord.containsKey(currentWord)){
                        vector.set(allWord.get(currentWord).index, vector.get(allWord.get(currentWord).index) + 1 );
                        update(allWord.get(currentWord), i-1);
                    }
                    else {
                        allWord.put(currentWord, new tupleVectorSpace(allWord.size(), i -1));
                        vector.add(0);
                    }
                }
            }
            stories.get(i-1).vector = vector;
        }

        for (int i = 0; i < totalStories - 1; i++) {
            for (int j =stories.get(i).vector.size(); j <stories.get(totalStories-1).vector.size(); j++)
                stories.get(i).vector.add(0);
        }

        for(int i=0; i<stories.get(totalStories-1).vector.size(); i++)
            idfScore.add(Double.NaN);
        
        Set<String> words = allWord.keySet();
        for(String word : words){
//            System.out.println(word + " " + allWord.get(word).index + " " + allWord.get(word).totalDocOccurence + " " + totalStories + " " + Math.log(totalStories / allWord.get(word).totalDocOccurence ) );
//            allWord.get(word).idfScore = Math.log(allWord.get(word).totalDocOccurence / totalStories);
            idfScore.set(allWord.get(word).index,Math.log(totalStories / allWord.get(word).totalDocOccurence));
        }        
        
        for (int i = 0; i < totalStories; i++) {
            Vector<Integer> curVect = stories.get(i).vector;
            Double totalWords = 0.0;
            for (int j = 0; j < curVect.size(); j++)
                totalWords +=  Double.valueOf(curVect.get(j));
            for (int j = 0; j < curVect.size(); j++)
//                stories.get(i).tf_idfScore.add((curVect.get(j)/totalWords)*(idfScore.get(j)));
                stories.get(i).tf_idfScore.add((curVect.get(j))*(idfScore.get(j)));
            stories.get(i).magnitude = magnitudeVector(stories.get(i).tf_idfScore);            
        }
        
    }
    
    static public Double dotProduct(Vector<Integer> v1, Vector<Integer> v2) {
        if (v1.size() != v2.size())
            throw new IllegalArgumentException("vectors of different dimenssions can not be operated.");
        Double sum = 0.0;
        for (int i = 0; i < v1.size(); i++)
            sum += (v1.get(i) * v2.get(i));
        return sum;
    }
    static public Double dotProduct1(Vector<Integer> v1, Vector<Double> v2) {
        if (v1.size() != v2.size())
            throw new IllegalArgumentException("vectors of different dimenssions can not be operated.");
        Double sum = 0.0;
        for (int i = 0; i < v1.size(); i++)
            sum += (v1.get(i) * v2.get(i));
        return sum;
    }
    static public Double dotProduct2(Vector<Double> v1, Vector<Double> v2) {
        if (v1.size() != v2.size())
            throw new IllegalArgumentException("vectors of different dimenssions can not be operated.");
        Double sum = 0.0;
        for (int i = 0; i < v1.size(); i++)
            sum += (v1.get(i) * v2.get(i));
        return sum;
    }
    
    static public double magnitudeVector(Vector<Double> v) {
        return Math.sqrt(dotProduct2(v, v));
    }

    static Vector<Integer> queryParser(String query){
        ArrayList<String> words = new ArrayList(Arrays.asList(query.split(" ")));
        Vector<Integer> vector = new Vector<>();
        for (int k = 0 ; k < allWord.size() ; k++) 
            vector.add(0);            
        while (words.contains("")){
            words.remove("");
        }
        for (int j = 0; j < words.size(); j++) {
            if (!stopWords.contains(words.get(j))){
                String currentWord = words.get(j);
                if (allWord.containsKey(currentWord)){
                    vector.set(allWord.get(currentWord).index, vector.get(allWord.get(currentWord).index) + 1 );
                }
            }
        }
        return vector;
    }
    
    
    static ArrayList<storyInfo> evaluteQuery (String query){
        ArrayList<storyInfo> resultStory = new ArrayList<>();
        Vector<Integer> queryVector = queryParser(query.toLowerCase());
        Vector<Double> queryTFIDFVector = new Vector<>();
        
        scores.clear();
        
        for (int j = 0; j < queryVector.size(); j++)
            queryTFIDFVector.add((queryVector.get(j))*(idfScore.get(j)));
                
        Double queryMagnitude = magnitudeVector(queryTFIDFVector);
                
        for (int i = 0; i < totalStories; i++) {
            scores.add(new Score(i, (dotProduct1(queryVector, stories.get(i).tf_idfScore))/(queryMagnitude*stories.get(i).magnitude)));
        }
        
        Collections.sort(scores, new Comparator<Score>() {
            @Override
            public int compare(Score s1, Score s2) {
                 return s2.score.compareTo(s1.score);
            }
        });
        
        for (Score sc:scores){
            if (sc.score  > alpha )
                resultStory.add(stories.get(sc.index));
        }
        
        return resultStory;
    }
}