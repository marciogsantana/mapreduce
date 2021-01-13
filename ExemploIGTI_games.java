
package IGTI;

import java.io.*;
import java.util.*;
import java.util.Random;
import java.text.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;


public class ExemploIGTI extends Configured implements Tool 
{          
    public static void main (final String[] args) throws Exception {   
      int res = ToolRunner.run(new Configuration(), new ExemploIGTI(), args);        
      System.exit(res);           
    }   

    public int run (final String[] args) throws Exception {
      try{
           // objeto conf para empacotar o programa 
	   JobConf conf = new JobConf(getConf(), ExemploIGTI.class);
           conf.setJobName("Dados de Games");
           final FileSystem fs = FileSystem.get(conf);
           
           Path diretorioEntrada = new Path("DADOS_GAME"), diretorioSaida = new Path("pastaSaida");
           FileInputFormat.setInputPaths(conf, diretorioEntrada);
           FileOutputFormat.setOutputPath(conf, diretorioSaida);

           conf.setOutputKeyClass(Text.class);
           conf.setOutputValueClass(Text.class);

           conf.setMapperClass(MapIGTI.class);
           conf.setReducerClass(ReduceIGTI.class);

           JobClient.runJob(conf);
              
            	             	       	
          
        }
        catch ( Exception e ) {
            throw e;
        }
        return 0;
     }
 
    public static class MapIGTI extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
            
      public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter)  throws IOException {                  
        
         Text txtChave = new Text();
         Text txtValor = new Text();
         String[] dadosGame = value.toString().split(",");
        // value é usado do metodo aqui temos um vetor de Strings  que vai receber cada posiçao da linha porque esta separada  por virgula split faz a separaçao dos elementos que estao separados por virgula
         String nomeJogo = dadosGame[1]; 
         String anoLancamento = dadosGame[3];

         double vendasAmerica = Double.parseDouble(dadosGame[6]);
         double vendasEuropa = Double.parseDouble(dadosGame[7]);

         String strValor = nomeJogo + "|" + String.valueOf(vendasAmerica + vendasEuropa);

         txtChave.set(anoLancamento);
         txtValor.set(strValor);    
         output.collect(txtChave,txtValor);      

      }        
    }
 
    
    public static class ReduceIGTI extends MapReduceBase implements Reducer<Text, Text, Text, Text> {       
       public void reduce (Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {  
          
          double maiorFaturamento = 0, valorVenda = 0;
          String[] dadosVendas = new String[2];
          String nomeJogo ="";
          Text value = new Text();
          
          while (values.hasNext()){
                value = values.next();
                dadosVendas = value.toString().split("\\|"); 
                valorVenda = Double.parseDouble(dadosVendas[1]);
                
                if(valorVenda > maiorFaturamento){

                   maiorFaturamento = valorVenda;
                   nomeJogo = dadosVendas[0];

                }
        }

         String strValor = nomeJogo + "|" + String.valueOf(maiorFaturamento);
         value.set(strValor);
         output.collect(key, value);
                 
        

    }
}

   public static class MapIGTIMaior extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {

      public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter)  throws IOException {
        

            
     }
}

    public static class ReduceIGTIMaior extends MapReduceBase implements Reducer<Text, Text, Text, Text> {   
       public void reduce (Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {     
               
  }

}
}


