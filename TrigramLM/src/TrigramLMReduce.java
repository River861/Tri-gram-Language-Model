import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Map.Entry;

public class TrigramLMReduce extends Reducer<Text, MyMapWritable, Text, MyMapWritable> {

    static long CorpusSize;

    /**
     * 获取语料库大小，用于Smoothing
     * */
    public void setup(Context context) {
        CorpusSize = context.getConfiguration().getLong("CorpusSize", 0);
    }
    /**
     * 对mapper的结果进行合并，即将values这个map数组的对应项相加、合并, 同时计算sum，用来计算概率
     * */
    public void reduce(Text key, Iterable<MyMapWritable> values, Context context) throws IOException, InterruptedException{
        MyMapWritable val_map = new MyMapWritable();
        int sum = 0;
        for(MyMapWritable value : values){
            for(Entry<Writable, Writable> entry : value.entrySet()) {
                update(val_map, (Text) entry.getKey(), (IntWritable) entry.getValue());
                sum += ((IntWritable) entry.getValue()).get();
            }
        }
        normalize(val_map, sum);
        context.write(key, val_map);
    }

    /**
     * 实现map的更新
     * */
    private void update(MyMapWritable val_map, Text key, IntWritable val) {
        double old_val = (val_map.containsKey(key) ? ((DoubleWritable) val_map.get(key)).get(): 0);
        val_map.put(key, new DoubleWritable(old_val + val.get()));
    }

    /**
     * 做除法，将结果转化为 P(W1|W2,W3)
     * */
    private void normalize(MyMapWritable val_map, int sum) {
        val_map.replaceAll((k, v) -> new DoubleWritable((((DoubleWritable) v).get() + 1) / (sum + CorpusSize)));
    }
}
