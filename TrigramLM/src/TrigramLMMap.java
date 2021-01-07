import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import Util.XMLParser;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

public class TrigramLMMap extends Mapper<LongWritable, Text, Text, MyMapWritable> {

    /**
     * map阶段主要逻辑
     * */
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        StringBuilder title = new StringBuilder();
        StringBuilder content = new StringBuilder();
        XMLParser.parse(value.toString(), title, content);  // 从input中取title和content部分来作为学习语料
        solve(title.toString(), context);  // 提取键值对
        solve(content.toString(), context);
    }

    /**
     * 实现Stripes统计，即统计形如(ab，c)这样的tri-gram子串个数，记录到map中并发送
     * map的例子：ab -> {c:1, d:5, e:2,...}
     * */
    private void solve(String str, Context context) throws IOException, InterruptedException {
        HashMap<Text, MyMapWritable> map = new HashMap<>();
        for(int i = 0; i < str.length() - 2; i ++){
            Text ab = new Text(str.substring(i, i + 2)), c = new Text(str.substring(i + 2, i + 3));
            if(str.charAt(i + 2) == ' ' || str.charAt(i + 2) == '\n' || str.charAt(i + 2) == '\r') {
                continue;  // 字符c是空格或换行的不考虑
            }
            if(!map.containsKey(ab)) {
                map.put(ab, new MyMapWritable());
            }
            update(map.get(ab), c);
        }
        for(Entry<Text, MyMapWritable> entry : map.entrySet()) {
            context.write(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 实现map的更新
     * */
    private void update(MyMapWritable val_map, Text key) {
        int old_val = (val_map.containsKey(key) ? ((IntWritable) val_map.get(key)).get(): 0);
        val_map.put(key, new IntWritable(old_val + 1));
    }
}
