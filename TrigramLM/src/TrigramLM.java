import Util.XmlInputFormat;
import PreJob.WordCount;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.io.Text;

public class TrigramLM {

    public static void main(String[] args)throws Exception{
        Configuration conf = new Configuration();
        conf.set("xmlinput.start", "<doc>");
        conf.set("xmlinput.end", "</doc>");

        // 获取输入输出路径
        String [] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if(otherArgs.length < 2){
            System.err.println("必须输入读取文件路径和输出路径");
            System.exit(2);
        }
        Path inputPath = new Path(otherArgs[0]);
        Path tmpPath = new Path("tmp");
        Path outputPath = new Path(otherArgs[1]);

        // 删除遗留的输出文件
        FileSystem fs =FileSystem.get(conf);
        if(fs.exists(tmpPath)){
            fs.delete(tmpPath,true);
        }
        if(fs.exists(outputPath)) {
            fs.delete(outputPath, true);
        }

         // PreJob
         long CorpusSize = WordCount.getCorpusSize(conf, inputPath, tmpPath);  // 先通过一个job来获取语料库大小（用于smoothing）
         System.out.println("CorpusSize = " + CorpusSize);

        // 开启正式任务
        if(CorpusSize > 0) {
            conf.setLong("CorpusSize", CorpusSize);
            Job job = Job.getInstance(conf, "Tri-gram Language Model");
            job.setJarByClass(TrigramLM.class);

            job.setInputFormatClass(XmlInputFormat.class);     // 设置读入格式
            FileInputFormat.addInputPath(job, inputPath);      // 设置读取文件的路径，从HDFS中读取
            FileOutputFormat.setOutputPath(job, outputPath);   // 设置mapreduce程序的输出路径

            job.setMapperClass(TrigramLMMap.class);            // 设置实现了map函数的类
            job.setCombinerClass(TrigramLMCombiner.class);     // 设置实现了combiner函数的类
            job.setReducerClass(TrigramLMReduce.class);        // 设置实现了reduce函数的类
            job.setOutputKeyClass(Text.class);                 // 设置reduce函数的key值
            job.setOutputValueClass(MyMapWritable.class);        // 设置reduce函数的value值

            System.exit(job.waitForCompletion(true) ? 0 : 1);
        }
        else {
            System.exit(1);
        }
    }
}
