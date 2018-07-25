package silicon.handler;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvReflectionException;
import org.supercsv.io.AbstractCsvWriter;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.MethodCache;
import org.supercsv.util.Util;

public class CsvBeanWriter2 extends CsvBeanWriter {

    private final List<Object> beanValues = new ArrayList();
    private final MethodCache cache = new MethodCache();

    public CsvBeanWriter2(Writer writer, CsvPreference preference) {
        super(writer, preference);
    }

    public void write(Object source, String... nameMapping) throws IOException {
        super.incrementRowAndLineNo();
        this.extractBeanValues(source, nameMapping);
        super.writeRow(this.beanValues);
    }

    private void extractBeanValues(Object source, String[] nameMapping) {
        if (source == null) {
            throw new NullPointerException("the bean to write should not be null");
        } else if (nameMapping == null) {
            throw new NullPointerException("the nameMapping array can't be null as it's used to map from fields to columns");
        } else {
            this.beanValues.clear();

            for(int i = 0; i < nameMapping.length; ++i) {
                String fieldName = nameMapping[i];
                if (fieldName == null) {
                    this.beanValues.add((Object)null);
                } else {
                    Method getMethod = this.cache.getGetMethod(source, fieldName);

                    try {
                        Object value = getMethod.invoke(source);
                        if(value != null && value.getClass().equals(String.class)){
                            value = Utils.validSringParam(String.valueOf(value));
                        }
                        this.beanValues.add(value);
                    } catch (Exception var7) {
                        throw new SuperCsvReflectionException(String.format("error extracting bean value for field %s", fieldName), var7);
                    }
                }
            }

        }
    }
}
