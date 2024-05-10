package scl.solr;

/**
 * @author sichaolong
 * @createdate 2024/5/9 17:07
 */
public interface SolrObjectDeserializer {

    Object deserializer(Object solrValue);
}

