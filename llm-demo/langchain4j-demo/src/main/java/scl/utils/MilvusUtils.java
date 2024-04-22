package scl.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.internal.Utils;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.RelevanceScore;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.exception.ParamException;
import io.milvus.grpc.QueryResults;
import io.milvus.param.R;
import io.milvus.param.dml.QueryParam;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.SearchResultsWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import scl.langchain4j.constants.MilvusConstants;

import java.math.BigDecimal;
import java.util.*;
import static dev.langchain4j.internal.Utils.isNullOrEmpty;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * @author sichaolong
 * @createdate 2024/4/20 09:31
 */
@Slf4j
public class MilvusUtils {

    public static List<String> generateRandomIds(int size) {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ids.add(Utils.randomUUID());
        }

        return ids;
    }

    public static List<String> generateEmptyScalars(int size) {
        String[] arr = new String[size];
        Arrays.fill(arr, "");

        return Arrays.asList(arr);
    }

    public static List<JSONObject> generateEmptyJsons(int size) {
        List<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(new JSONObject(new HashMap<>()));
        }
        return list;
    }


    public static List<List<Float>> toVectors(List<Embedding> embeddings) {
        return embeddings.stream()
            .map(Embedding::vectorAsList)
            .collect(toList());
    }

    public static List<String> toScalars(List<TextSegment> textSegments, int size) {
        return isNullOrEmpty(textSegments) ? generateEmptyScalars(size) : textSegmentsToScalars(textSegments);
    }

    public static List<JSONObject> toMetadataJsons(List<TextSegment> textSegments, int size) {
        return isNullOrEmpty(textSegments) ? generateEmptyJsons(size) : textSegments.stream()
            .map(segment -> new JSONObject(segment.metadata().toMap()))
            .collect(toList());
    }

    public static List<String> textSegmentsToScalars(List<TextSegment> textSegments) {
        return textSegments.stream()
            .map(TextSegment::text)
            .collect(toList());
    }

    public static List<EmbeddingMatch<TextSegment>> toEmbeddingMatches(MilvusServiceClient milvusClient,
                                                                       SearchResultsWrapper resultsWrapper,
                                                                       String collectionName,
                                                                       ConsistencyLevelEnum consistencyLevel,
                                                                       boolean queryForVectorOnSearch) {
        List<EmbeddingMatch<TextSegment>> matches = new ArrayList<>();

        Map<String, Embedding> idToEmbedding = new HashMap<>();
        if (queryForVectorOnSearch) {
            try {
                List<String> rowIds = (List<String>)resultsWrapper.getFieldWrapper(MilvusConstants.Field.ID).getFieldData();
                idToEmbedding.putAll(queryEmbeddings(milvusClient, collectionName, rowIds, consistencyLevel));
            } catch (ParamException e) {
                // There is no way to check if the result is empty or not.
                // If the result is empty, the exception will be thrown.
            }
        }

        for (int i = 0; i < resultsWrapper.getRowRecords().size(); i++) {
            SearchResultsWrapper.IDScore idScore = resultsWrapper.getIDScore(0).get(i);
            double score = idScore.getScore();
            String rowId = StringUtils.isEmpty(idScore.getStrID()) ? String.valueOf(idScore.getLongID()) : idScore.getStrID();
            Embedding embedding = idToEmbedding.get(rowId);
            TextSegment textSegment = toTextSegment(resultsWrapper.getRowRecords().get(i));
            EmbeddingMatch<TextSegment> embeddingMatch = new EmbeddingMatch<>(
                RelevanceScore.fromCosineSimilarity(score),
                rowId,
                embedding,
                textSegment
            );
            matches.add(embeddingMatch);
        }

        return matches;
    }


    public static TextSegment toTextSegment(QueryResultsWrapper.RowRecord rowRecord) {
        if (!rowRecord.getFieldValues().containsKey(MilvusConstants.Field.METADATA)) {
            return null;
        }
        JSONObject metadata = (JSONObject) rowRecord.get(MilvusConstants.Field.METADATA);
        log.info("recall rowRecord fieldValues: {}", rowRecord.getFieldValues());
        String content = (String) rowRecord.getFieldValues().get(MilvusConstants.Field.QUESTION_CONTENT);
        return TextSegment.from(content,toMetadata(metadata));
    }

    private static Metadata toMetadata(JSONObject metadata) {
        Map<String, Object> metadataMap = metadata.getInnerMap();
        metadataMap.forEach((key, value) -> {
            if (value instanceof BigDecimal) {
                // It is safe to convert. No information is lost, the "biggest" type allowed in Metadata is double.
                metadataMap.put(key, ((BigDecimal) value).doubleValue());
            }
        });
        return Metadata.from(metadataMap);
    }

    private static Map<String, Embedding> queryEmbeddings(MilvusServiceClient milvusClient,
                                                          String collectionName,
                                                          List<String> rowIds,
                                                          ConsistencyLevelEnum consistencyLevel) {
        QueryResultsWrapper queryResultsWrapper = queryForVectors(
            milvusClient,
            collectionName,
            rowIds,
            new Metadata(),
            consistencyLevel
        );

        Map<String, Embedding> idToEmbedding = new HashMap<>();
        for (QueryResultsWrapper.RowRecord row : queryResultsWrapper.getRowRecords()) {
            String id = row.get(MilvusConstants.Field.ID).toString();
            List<Float> vector = (List<Float>) row.get(MilvusConstants.Field.EIGENVALUES);
            idToEmbedding.put(id, Embedding.from(vector));
        }

        return idToEmbedding;
    }

    public static QueryResultsWrapper queryForVectors(MilvusServiceClient milvusClient,
                                                      String collectionName,
                                                      List<String> ids,
                                                      Metadata metadata,
                                                      ConsistencyLevelEnum consistencyLevel) {
        QueryParam request = buildQueryRequest(collectionName, ids, metadata, consistencyLevel);
        R<QueryResults> response = milvusClient.query(request);
        MilvusUtils.checkResponseNotFailed(response);
        return new QueryResultsWrapper(response.getData());
    }

    public static <T> void checkResponseNotFailed(R<T> response) {
        if (response == null) {
            throw new RuntimeException("Request to Milvus DB failed. Response is null");
        } else if (response.getStatus() != R.Status.Success.getCode()) {
            String message = format("Request to Milvus DB failed. Response status:'%d'.%n", response.getStatus());
            throw new RuntimeException(message, response.getException());
        }
    }


    public static QueryParam buildQueryRequest(String collectionName,
                                               List<String> ids,
                                               Metadata metadata,
                                               ConsistencyLevelEnum consistencyLevel) {
        return QueryParam.newBuilder()
            .withCollectionName(collectionName)
            .withExpr(buildQueryFilterExpression(ids, metadata))
            .withConsistencyLevel(consistencyLevel)
            .withOutFields(Arrays.asList(MilvusConstants.Field.ID,
                MilvusConstants.Field.EIGENVALUES,
                MilvusConstants.Field.METADATA,MilvusConstants.Field.QUESTION_CONTENT))
            .build();
    }


    /**
     * 构建非向量字段查询条件
     * 如：(id == 449166796404446634) && (metadata["qid"] == "3476004932208640" && metadata["content"] == "sichaolong")
     * @param ids
     * @param metadata
     * @return
     */
    private static String buildQueryFilterExpression(List<String> ids, Metadata metadata) {

        StringBuffer sb = new StringBuffer();

        if (CollectionUtils.isNotEmpty(ids)) {
            String idsCondition = ids.stream()
                .map(id -> format("%s == '%s'", MilvusConstants.Field.ID, id))
                .collect(joining(" || "));
            sb.append(String.format("(%s)", idsCondition));
        }

        String metadataCondition = metadata.toMap().entrySet().stream()
            .map(entry -> format("%s == %s", MilvusMetadataFilterUtils.formatKey(entry.getKey()), MilvusMetadataFilterUtils.formatValue(entry.getValue())))
            .collect(joining(" && "));
        if(StringUtils.isNotEmpty(metadataCondition)){
            if (sb.length() > 0) {
                sb.append(" && ");
            }
            sb.append(String.format("(%s)", metadataCondition));
        }

        log.info("query filter condition expression:{}", sb.toString());
        return sb.toString();
    }
}


