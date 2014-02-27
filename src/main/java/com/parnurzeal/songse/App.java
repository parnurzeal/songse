package com.parnurzeal.songse;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * Hello world!
 *
 */
public class App 
{
    
    public static void listAllFiles(File folder, ArrayList<String> allFiles) throws FileNotFoundException{
      for(File fileEntry: folder.listFiles()){
        if(fileEntry.isDirectory()){
          listAllFiles(fileEntry, allFiles);
        }else{
          allFiles.add(fileEntry.getAbsolutePath());      
        }
      }
    }

    public static String readAll(String path) throws FileNotFoundException{
      Scanner sc = new Scanner(new File(path)).useDelimiter("\\A");
      return sc.hasNext()?sc.next():"";
    }
    public static void main( String[] args )
    {
      InvertedIndex idx = new InvertedIndex();
      ArrayList<String> allFiles = new ArrayList<String>();
      try{
        listAllFiles(new File(args[0]), allFiles);
        for(String path:allFiles){
          String content = readAll(path);
          idx.add(content, path);
        }
        List<String> queries = new LinkedList<String>();
        queries.add("more");
        queries.add("one");
        idx.search(queries);
      }catch(FileNotFoundException fe){
        System.out.println(fe);
      }
    }
}

class InvertedIndex{

  HashMap<String, List<Tuple>> indexName;

  private static Pattern twopart = Pattern.compile("([A-Za-z]+):\\s*(.*)");
  public InvertedIndex(){
    indexName = new HashMap<String, List<Tuple>>();
    
  }

  public HashMap<String, String> getInfo(String content){
    HashMap<String, String> hm = new HashMap<String, String>();
    Scanner sc = new Scanner(content);
    while(sc.hasNextLine()){
      String line = sc.nextLine();
      Matcher m = twopart.matcher(line);
      if(m.matches()){
        hm.put(m.group(1), m.group(2));
      }else{
        System.out.println("No match : " + line);
      }
    }
    return hm;
  }
  
  public boolean add(String content, String path){
    HashMap<String, String> hm = getInfo(content);
    String[] words = hm.get("Name").split("[^a-zA-Z0-9]+");
    for(String _w:words){
      String w = _w.toLowerCase();
      System.out.println(w);
      List<Tuple> songlist = indexName.get(w);
      if(songlist==null){
        songlist = new LinkedList<Tuple>();
        indexName.put(w, songlist);
      }
      songlist.add(new Tuple(hm.get("Name"), path));
    }
    return true; 
  }

  public void search(List<String> words){
    Set<Tuple> answer = new HashSet<Tuple>();
    for(String _w:words){
      String w = _w.toLowerCase();
      List<Tuple> songlist = indexName.get(w);
      if(songlist!=null){
        for(Tuple t:songlist){
          answer.add(t);
        }
      }
    }
    System.out.println(answer);
  }

  
  class Tuple{
    public String songid;
    public String path;
    public Tuple(String songid, String path){
      this.songid = songid;
      this.path = path;
    }
    public String toString(){
      return this.songid + "-" + this.path;
    }
  }
}
