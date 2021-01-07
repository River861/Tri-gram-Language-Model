import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Map.Entry;

public class TrigramLMCombiner extends Reducer<Text, MyMapWritable, Text, MyMapWritable> {

    /**
     * 对mapper的结果进行合并，即将values这个map数组的对应项相加、合并
     * */
    public void reduce(Text key, Iterable<MyMapWritable> values, Context context) throws IOException, InterruptedException{
        MyMapWritable val_map = new MyMapWritable();
        for(MyMapWritable value : values){
            for(Entry<Writable, Writable> entry : value.entrySet()) {
                update(val_map, (Text) entry.getKey(), (IntWritable) entry.getValue());
            }
        }
        context.write(key, val_map);
    }

    /**
     * 实现map的更新
     * */
    private void update(MyMapWritable val_map, Text key, IntWritable val) {
        int old_val = (val_map.containsKey(key) ? ((IntWritable) val_map.get(key)).get(): 0);
        val_map.put(key, new IntWritable(old_val + val.get()));
    }
}
