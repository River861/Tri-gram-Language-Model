import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Writable;

import java.util.Iterator;

public class MyMapWritable extends MapWritable {

    /**
     * 自定义输出格式，完全按照Json的要求来输出，注意key值需要用引号包围
     * */
    @Override
    public String toString()
    {
        StringBuilder output_str = new StringBuilder();
        output_str.append("{");
        Iterator<Entry<Writable, Writable>> iter = entrySet().iterator();
        while(iter.hasNext()) {
            Entry<Writable, Writable> entry = iter.next();
            output_str.append("\"").append(entry.getKey()).append("\": ").append(entry.getValue());
            if(iter.hasNext()) output_str.append(", ");
        }
        output_str.append("}");
        return output_str.toString();
    }
}
