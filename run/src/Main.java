
import java.io.*;
import java.util.*;

//=====================================================================
class DictEntry {

    public int doc_freq = 0; // number of documents that contain the term
    public int term_freq = 0; //number of times the term is mentioned in the collection
    public HashSet<Integer> postingList;

    DictEntry() {
        postingList = new HashSet<Integer>();
    }
}

//=====================================================================
class Index {

    //--------------------------------------------
    Map<Integer, String> sources;  // store the doc_id and the file name
    HashMap<String, DictEntry> index; // THe inverted index
    //--------------------------------------------
    HashSet<String> wordsInFiles;



    Index() {
        sources = new HashMap<Integer, String>();
        index = new HashMap<String, DictEntry>();
        wordsInFiles = new HashSet<String>();

    }

    //---------------------------------------------
    public void printDictionary() {
        Iterator it = index.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            DictEntry dd = (DictEntry) pair.getValue();
            HashSet<Integer> hset = dd.postingList;// (HashSet<Integer>) pair.getValue();
            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
            Iterator<Integer> it2 = hset.iterator();
            while (it2.hasNext()) {
                System.out.print(it2.next() + ", ");
            }
            System.out.println("");
            //it.remove(); // avoids a ConcurrentModificationException
        }
        System.out.println("------------------------------------------------------");
        System.out.println("*** Number of terms = " + index.size());
    }

    //-----------------------------------------------
    public void buildIndex(String[] files) {
        int i = 0;
        for (String fileName : files) {
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                sources.put(i, fileName);
                String ln;
                while ((ln = file.readLine()) != null) {
                    String[] words = ln.split("\\W+");
                    for (String word : words) {
                        word = word.toLowerCase();

                        // check to see if the word is not in the dictionary
                        if (!index.containsKey(word)) {
                            index.put(word, new DictEntry());
                            wordsInFiles.add(word);
                        }
                        // add document id to the posting list
                        if (!index.get(word).postingList.contains(i)) {
                            index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term
                            index.get(word).postingList.add(i); // add the posting to the posting:ist
                        }
                        //set the term_fteq in the collection
                        index.get(word).term_freq += 1;
                    }
                }
                printDictionary();
            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }

            i++;
        }
    }

    //==================================================================

}

class CosineSimilarity{
    private int Vector1;
    private int Vector2;
    private double value;

    CosineSimilarity(int vector1,int vector2,double value){
        this.Vector1 = vector1;
        this.Vector2 = vector2;
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "D" + Integer.toString(Vector1+1) +
                " and D" + Integer.toString(Vector2+1) +
                " cosine similarity = " + value ;
    }
}

class Similarity{

    private  Index index;

    ArrayList<CosineSimilarity> similarities;

    Similarity() {
        index = new Index();
        similarities = new ArrayList<>();
    }

    Index getIndex(){
        return this.index;
    }

    void printArray(ArrayList array){
        for(int i=0;i<array.size();i++){
            System.out.println(array.get(i));

        }
        System.out.println();
    }

    CosineSimilarity getLargest(ArrayList<CosineSimilarity> p){

        CosineSimilarity selectedSimilarity=p.get(0);

        for(int i=0;i<p.size();i++){

            if(p.get(i).getValue()>selectedSimilarity.getValue()){
                selectedSimilarity=p.get(i);
            }
        }

        return selectedSimilarity;

    }

    void Sort(ArrayList<CosineSimilarity> p){
        int i,counter=p.size();
        ArrayList<CosineSimilarity> temp=new ArrayList<>();

        for(i=0;i<counter;i++){
            temp.add(getLargest(p));
            p.remove(getLargest(p));
        }

        for(i=0;i< temp.size();i++){
            p.add(temp.get(i));
        }

    }


    void printArray(int[] array){
        for(int i=0;i<array.length;i++){
            System.out.print(array[i]);
            System.out.print(" ");
        }
        System.out.println();
    }

    void printMatrix(int[][] array){
        for(int i=0;i<array.length;i++){
            System.out.print("D"+Integer.toString(i+1)+" | ");
            printArray(array[i]);
        }
    }
    //===================================================================
    int[][] constructMatrix(){
        int i;
        int[][] result = new int[index.sources.size()][index.wordsInFiles.size()];

        System.out.println("\n/*******************************************/");
        System.out.println("Number of Words : " +index.wordsInFiles.size());
        System.out.println("Number of files : " +index.sources.size());
        System.out.println("/*******************************************/\n");

        Object[] array = index.wordsInFiles.toArray();
        Object[] filesArray = index.sources.keySet().toArray();

        //printArray(filesData);

        /*
        if (index.index.get("the").postingList.contains(1)){
            System.out.println("Worked");
        }

         */

        System.out.print(" ");
        for(i=0;i<index.wordsInFiles.size();i++){
            if(array[i].equals("")){
                System.out.print("''");
            }
            System.out.print(array[i]+" ");
        }
        System.out.println();

        for(i=0;i<index.wordsInFiles.size();i++){

            System.out.print(" __");
        }
        System.out.println();
        for(i=0;i<index.sources.size();i++){


            for(int j=0;j< index.wordsInFiles.size();j++){
                if (index.index.get(array[j]).postingList.contains(filesArray[i])) {
                    result[i][j]=1;
                    //System.out.print("1 ");
                }else{
                    result[i][j]=0;
                    //System.out.print("0 ");
                }
            }
            //System.out.println();
        }


        printMatrix(result);
        return result;


    }

    int calculateDotProduct(int[] D1,int[] D2) {
        int result = 0;
        if(D1.length==D2.length) {
            for (int i = 0; i <D1.length; i++) {
                result += D1[i]*D2[i];
            }
        }
        //System.out.println(result);
        return result;
    }

    double calculateVectorNorm(int[] D1) {
        int sumOfSquered = 0;
        double result = 0.0f;

        for (int i = 0; i <D1.length; i++) {
            sumOfSquered += D1[i]*D1[i];
        }
        result = Math.sqrt(sumOfSquered);
        //System.out.println(result);
        return result;
    }
    double calculateCosineSimilarity(int[] D1,int[] D2){

        int dotProduct = calculateDotProduct(D1,D2);
        double D1Norm = calculateVectorNorm(D1);
        double D2Norm = calculateVectorNorm(D2);
        double result = dotProduct/(D1Norm*D2Norm);

        //System.out.println(result);
        return result;
    }


    void calculateCosineSimilarityForAll(){
        int [][] matrix = constructMatrix();
        Object[] filesArray = index.sources.keySet().toArray();
        for(int i=0;i<filesArray.length;i++){

            for(int j=i+1;j<filesArray.length;j++){
                //System.out.println(calculateCosineSimilarity(matrix[i],matrix[j]));
                //System.out.println(Integer.toString(i) + Integer.toString(j));
                CosineSimilarity similarity = new CosineSimilarity(i,j,calculateCosineSimilarity(matrix[i],matrix[j]));
                similarities.add(similarity);
            }
        }

        //printArray(similarities);
        printResult();
    }


    void printResult(){
        System.out.println("\n/***** Cosine Similarity *****/\n");
        Sort(similarities);
        printArray(similarities);
    }


}

//==================================================================

public class Main {
    public static void main(String[] args){

        Similarity similarity = new Similarity();
        similarity.getIndex().buildIndex(new String[]{

                "C:\\Users\\LENOVO\\Downloads\\docs\\100.txt", // change it to your path e.g. "c:\\tmp\\100.txt"
                "C:\\Users\\LENOVO\\Downloads\\docs\\101.txt",
                "C:\\Users\\LENOVO\\Downloads\\docs\\102.txt",
                "C:\\Users\\LENOVO\\Downloads\\docs\\103.txt",
                "C:\\Users\\LENOVO\\Downloads\\docs\\104.txt",
                "C:\\Users\\LENOVO\\Downloads\\docs\\105.txt",
                "C:\\Users\\LENOVO\\Downloads\\docs\\106.txt",
                "C:\\Users\\LENOVO\\Downloads\\docs\\107.txt"



            }
        );

        //System.out.println(index.wordsInFiles);
        //similarity.printMatrix(similarity.constructMatrix());

        similarity.calculateCosineSimilarityForAll();
    }
}
