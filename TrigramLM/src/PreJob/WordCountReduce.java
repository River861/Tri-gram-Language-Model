package PreJob;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.Text;

public class WordCountReduce extends Reducer<Text, IntWritable, Text, IntWritable> {

    public enum FileRecorder{
        CorpusSizeRecorder,
    }

    public void reduce(Text key, Iterable<IntWritable> values, Context context) {
        // int count = 0;
        // for(IntWritable value: values) {
        //     count += value.get();
        // }
        // context.write(key, new IntWritable(count));
        context.getCounter(FileRecorder.CorpusSizeRecorder).increment(1);  // 计算字种数即可
    }
}
