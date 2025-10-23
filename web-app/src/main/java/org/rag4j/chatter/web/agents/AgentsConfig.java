package org.rag4j.chatter.web.agents;

import org.rag4j.chatter.web.messages.MessageService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentsConfig {

    @Bean
    public EchoAgent echoAgent(ChatMemory chatMemory, ChatModel chatModel, MessageService messageService) {
        return new EchoAgent(
                ChatClient.builder(chatModel).build(),
                chatMemory,
                messageService
        );
    }

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().build();
    }

//    @Bean
//    ChatModel chatModel(OpenAiApi openAiApi) {
//        return OpenAiChatModel.builder().openAiApi(openAiApi).defaultOptions(
//                OpenAiChatOptions.builder().model(OpenAiApi.ChatModel.GPT_5_MINI).build()
//        ).build();
//    }

//    @Bean
//    public OpenAiApi openAIOkHttpClient() {
//        var openAIApiKey = System.getenv("OPENAI_API_KEY");
//        if (openAIApiKey == null || openAIApiKey.isEmpty()) {
//            throw new IllegalArgumentException("We need the OPENAI_API_KEY environment variable set");
//        }
//        return OpenAiApi.builder()
//                .apiKey(openAIApiKey)
//                .build();
//    }

}
