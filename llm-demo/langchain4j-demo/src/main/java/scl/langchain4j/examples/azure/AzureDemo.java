package scl.langchain4j.examples.azure;

import com.azure.ai.openai.models.ChatCompletionsJsonResponseFormat;
import com.azure.core.util.BinaryData;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolParameters;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.*;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static dev.langchain4j.data.message.ToolExecutionResultMessage.toolExecutionResultMessage;
import static dev.langchain4j.data.message.UserMessage.userMessage;

/**
 * @author sichaolong
 * @createdate 2024/4/16 17:34
 */
public class AzureDemo {

    Logger logger = LoggerFactory.getLogger(AzureDemo.class);


    void should_generate_answer_and_return_token_usage_and_finish_reason_stop(String deploymentName, String gptVersion) {

        ChatLanguageModel model = AzureOpenAiChatModel.builder()
            .endpoint(System.getenv("AZURE_OPENAI_ENDPOINT"))
            .apiKey(System.getenv("AZURE_OPENAI_KEY"))
            .deploymentName(deploymentName)
            .tokenizer(new OpenAiTokenizer(gptVersion))
            .logRequestsAndResponses(true)
            .build();

        UserMessage userMessage = userMessage("hello, how are you?");

        Response<AiMessage> response = model.generate(userMessage);
        logger.info(response.toString());
        TokenUsage tokenUsage = response.tokenUsage();
    }

    void should_generate_answer_and_return_token_usage_and_finish_reason_length(String deploymentName, String gptVersion) {

        ChatLanguageModel model = AzureOpenAiChatModel.builder()
            .endpoint(System.getenv("AZURE_OPENAI_ENDPOINT"))
            .apiKey(System.getenv("AZURE_OPENAI_KEY"))
            .deploymentName(deploymentName)
            .tokenizer(new OpenAiTokenizer(gptVersion))
            .maxTokens(3)
            .logRequestsAndResponses(true)
            .build();

        UserMessage userMessage = userMessage("hello, how are you?");

        Response<AiMessage> response = model.generate(userMessage);
        logger.info(response.toString());

    }


    void should_call_function_with_argument(String deploymentName, String gptVersion) {

        ChatLanguageModel model = AzureOpenAiChatModel.builder()
            .endpoint(System.getenv("AZURE_OPENAI_ENDPOINT"))
            .apiKey(System.getenv("AZURE_OPENAI_KEY"))
            .deploymentName(deploymentName)
            .tokenizer(new OpenAiTokenizer(gptVersion))
            .logRequestsAndResponses(true)
            .build();

        UserMessage userMessage = userMessage("What should I wear in Paris, France depending on the weather?");

        // This test will use the function called "getCurrentWeather" which is defined below.
        String toolName = "getCurrentWeather";

        ToolSpecification toolSpecification = ToolSpecification.builder()
            .name(toolName)
            .description("Get the current weather")
            .parameters(getToolParameters())
            .build();

        Response<AiMessage> response = model.generate(Collections.singletonList(userMessage), toolSpecification);

        AiMessage aiMessage = response.content();

        ToolExecutionRequest toolExecutionRequest = aiMessage.toolExecutionRequests().get(0);

        // We should get a response telling how to call the "getCurrentWeather" function, with the correct parameters in JSON format.
        logger.info(response.toString());

        // We can now call the function with the correct parameters.
        WeatherLocation weatherLocation = BinaryData.fromString(toolExecutionRequest.arguments()).toObject(WeatherLocation.class);
        int currentWeather = 0;
        currentWeather = getCurrentWeather(weatherLocation);

        String weather = String.format("The weather in %s is %d degrees %s.",
            weatherLocation.getLocation(), currentWeather, weatherLocation.getUnit());


        // Now that we know the function's result, we can call the model again with the result as input.
        ToolExecutionResultMessage toolExecutionResultMessage = toolExecutionResultMessage(toolExecutionRequest, weather);
        SystemMessage systemMessage = SystemMessage.systemMessage("If the weather is above 30 degrees celsius, recommend the user wears a t-shirt and shorts.");

        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.add(systemMessage);
        chatMessages.add(userMessage);
        chatMessages.add(aiMessage);
        chatMessages.add(toolExecutionResultMessage);

        Response<AiMessage> response2 = model.generate(chatMessages);

        logger.info(response2.toString());
    }


    void should_call_function_with_no_argument(String deploymentName, String gptVersion) {
        ChatLanguageModel model = AzureOpenAiChatModel.builder()
            .endpoint(System.getenv("AZURE_OPENAI_ENDPOINT"))
            .apiKey(System.getenv("AZURE_OPENAI_KEY"))
            .deploymentName(deploymentName)
            .tokenizer(new OpenAiTokenizer(gptVersion))
            .logRequestsAndResponses(true)
            .build();

        UserMessage userMessage = userMessage("What time is it?");

        // This test will use the function called "getCurrentDateAndTime" which takes no arguments
        String toolName = "getCurrentDateAndTime";

        ToolSpecification noArgToolSpec = ToolSpecification.builder()
            .name(toolName)
            .description("Get the current date and time")
            .build();

        Response<AiMessage> response = model.generate(Collections.singletonList(userMessage), noArgToolSpec);

        AiMessage aiMessage = response.content();

        ToolExecutionRequest toolExecutionRequest = aiMessage.toolExecutionRequests().get(0);

    }


    void should_use_json_format(String deploymentName, String gptVersion) {
        ChatLanguageModel model = AzureOpenAiChatModel.builder()
            .endpoint(System.getenv("AZURE_OPENAI_ENDPOINT"))
            .apiKey(System.getenv("AZURE_OPENAI_KEY"))
            .deploymentName(deploymentName)
            .tokenizer(new OpenAiTokenizer(gptVersion))
            .responseFormat(new ChatCompletionsJsonResponseFormat())
            .logRequestsAndResponses(true)
            .build();

        SystemMessage systemMessage = SystemMessage.systemMessage("You are a helpful assistant designed to output JSON.");
        UserMessage userMessage = userMessage("List teams in the past French presidents, with their first name, last name, dates of service.");

        Response<AiMessage> response = model.generate(systemMessage, userMessage);

        logger.info(response.toString());
    }

    private static ToolParameters getToolParameters() {
        Map<String, Map<String, Object>> properties = new HashMap<>();

        Map<String, Object> location = new HashMap<>();
        location.put("type", "string");
        location.put("description", "The city and state, e.g. San Francisco, CA");
        properties.put("location", location);

        Map<String, Object> unit = new HashMap<>();
        unit.put("type", "string");
        unit.put("enum", Arrays.asList("celsius", "fahrenheit"));
        properties.put("unit", unit);

        List<String> required = Arrays.asList("location", "unit");

        return ToolParameters.builder()
            .properties(properties)
            .required(required)
            .build();
    }

    // This is the method we offer to OpenAI to be used as a function_call.
    // For this example, we ignore the input parameter and return a simple value.
    private static int getCurrentWeather(WeatherLocation weatherLocation) {
        return 35;
    }

    // WeatherLocation is used for this sample. This describes the parameter of the function you want to use.
    private static class WeatherLocation {
        @JsonProperty(value = "unit")
        String unit;
        @JsonProperty(value = "location")
        String location;

        @JsonCreator
        WeatherLocation(@JsonProperty(value = "unit") String unit, @JsonProperty(value = "location") String location) {
            this.unit = unit;
            this.location = location;
        }

        public String getUnit() {
            return unit;
        }

        public String getLocation() {
            return location;
        }
    }
}
