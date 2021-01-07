package PreJob;

import Util.XmlInputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class WordCount {

    public static long getCorpusSize(Configuration conf, Path inputPath, Path outputPath) throws IOException, ClassNotFoundException, InterruptedException {
        Job job = Job.getInstance(conf, "Calculate Corpus Size");
        job.setJarByClass(WordCount.class);

        FileInputFormat.addInputPath(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);
        job.setInputFormatClass(XmlInputFormat.class);
        job.setMapperClass(WordCountMap.class);
        job.setReducerClass(WordCountReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        if(job.waitForCompletion(true)) {
            return job.getCounters().findCounter(WordCountReduce.FileRecorder.CorpusSizeRecorder).getValue();
        }
        return 0;
    }
}