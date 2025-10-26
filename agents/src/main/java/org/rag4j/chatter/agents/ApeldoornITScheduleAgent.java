package org.rag4j.chatter.agents;
import org.rag4j.chatter.core.agent.Agent;
import jakarta.annotation.PostConstruct;
import org.rag4j.chatter.core.agent.AgentLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class ApeldoornITScheduleAgent implements Agent {

    private static final Logger logger = LoggerFactory.getLogger(ApeldoornITScheduleAgent.class);

    public static final String AGENT_NAME = "Apeldoorn IT Agent";

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final AgentLifecycleManager lifecycleManager;

    public ApeldoornITScheduleAgent(ChatClient chatClient, ChatMemory chatMemory, AgentLifecycleManager lifecycleManager) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
        this.lifecycleManager = lifecycleManager;
    }

    @PostConstruct
    public void init() {
        lifecycleManager.subscribeAgent(this);
    }

    @Override
    public String name() {
        return AGENT_NAME;
    }

    @Override
    public Mono<String> processMessage(String payload) {
        logger.debug("ApeldoornITScheduleAgent processMessage: {}", payload);

        String prompt = """
                You are an AI agent that knows everything about the schedule of the event Apeldoorn IT.
                If you see a message about the events schedule use the knowledge about the schedule to answer the question.
                If you are sure the answer is about the schedule, but you cannot answer it with the knowledge you have, respond with "I don't know".
                Always reply in short answers.
                If the message is not about Football, respond with the exact placeholder "#nothingtosay#" with no additional text.
                
                # Schedule information in json format
                
                [
                  {
                    "time": "08:00",
                    "title": "Opening Burgemeester Ton Heerts",
                    "speaker": "Ton Heerts",
                    "category": "General"
                  },
                  {
                    "time": "09:00",
                    "title": "Keynote Eric Scherder - Governance van AI, een kwestie van verstand van zaken",
                    "speaker": "Eric Scherder",
                    "category": "Artificial Intelligence"
                  },
                  {
                    "time": "09:45",
                    "title": "De toekomst is nu! Meer impact met data en AI voor onze gemeente",
                    "speaker": "Pieter Vermeij en Gerwen den Besten",
                    "category": "Artificial Intelligence"
                  },
                  {
                    "time": "10:15",
                    "title": "Workshop met inkijk in gemeente data en AI inzet om maatschappelijke vraagstukken op te lossen",
                    "speaker": "Gemeente Apeldoorn",
                    "category": "Artificial Intelligence Workshop"
                  },
                  {
                    "time": "10:30",
                    "title": "Crappy by Design: Hoe bouw ik betrouwbare overheidsregisters?",
                    "speaker": "Marc van Andel (Kadaster)",
                    "category": "Applicatie Ontwikkeling"
                  },
                  {
                    "time": "10:30",
                    "title": "Shift-left Observability - Waarom wachten tot het misgaat?",
                    "speaker": "Jeroen Timmermans (Profit4Cloud)",
                    "category": "Applicatie Ontwikkeling"
                  },
                  {
                    "time": "10:30",
                    "title": "Cybercompetentie tekorten in Europa en mogelijke oplossingen",
                    "speaker": "Stephan Corporaal (CVD/Saxion)",
                    "category": "Security"
                  },
                  {
                    "time": "10:30",
                    "title": "AI innovaties binnen de Belastingdienst",
                    "speaker": "Jeffrey Dolman (Belastingdienst)",
                    "category": "Artificial Intelligence"
                  },
                  {
                    "time": "10:30",
                    "title": "De functioneel beheerder van de toekomst - Denken in waarde, handelen vanuit mindset",
                    "speaker": "Daniel Brouwer en Ruben Opstal (VFB)",
                    "category": "Functioneel Beheer"
                  },
                  {
                    "time": "10:30",
                    "title": "Digitale Transformatie in de praktijk: Wat vraagt het van je medewerkers en hoe zet je de juiste interventies in?",
                    "speaker": "Derk-Jan Nijman en Peter Schalk (CVD/Saxion & Politie Oost-Nederland)",
                    "category": "Human aspects in IT"
                  },
                  {
                    "time": "11:15",
                    "title": "Orde in de chaos: welke security testmethode past bij jouw organisatie?",
                    "speaker": "Ramon de Rooij (YieldDD)",
                    "category": "Security"
                  },
                  {
                    "time": "11:15",
                    "title": "High Impact Teams als Fundament voor Engineering Excellence",
                    "speaker": "Cris Cadini, Donovan Tjien-Fooh en Daniel Janus (Team Rockstars)",
                    "category": "Human aspects / Applicatie Ontwikkeling"
                  },
                  {
                    "time": "11:15",
                    "title": "HAN en Kadaster innoveren rotonde detectie met AI",
                    "speaker": "Erwin Folmer en Tony Baving (HAN & Kadaster)",
                    "category": "Artificial Intelligence"
                  },
                  {
                    "time": "11:15",
                    "title": "Demo: Procesorkestratie / Business Process Management met Camunda",
                    "speaker": "Tijmen Kars (Belastingdienst)",
                    "category": "BPM"
                  },
                  {
                    "time": "11:15",
                    "title": "Low-code bij het Kadaster",
                    "speaker": "Ronnie Drosten (Kadaster)",
                    "category": "Applicatie Ontwikkeling"
                  },
                  {
                    "time": "13:00",
                    "title": "Van Verplichting naar Versterking - Risicomanagement onder NIS2 en DORA",
                    "speaker": "Julian van Sijp (Bluebird & Hawk)",
                    "category": "Risicomanagement / Security"
                  },
                  {
                    "time": "13:00",
                    "title": "Trust Rust, hands-on experience - Workshop Rust van eerste hello tot volwaardige multi-threading applicatie",
                    "speaker": "Sidney Philipsen, Koen Braham en Jeroen Jansen (Alten)",
                    "category": "Applicatie Ontwikkeling Workshop"
                  },
                  {
                    "time": "13:00",
                    "title": "AI, IT Security & Risks",
                    "speaker": "Leon Dorgelo, Jasper Floe en Melvin Zonneveld (IT Building)",
                    "category": "Artificial Intelligence / Security"
                  },
                  {
                    "time": "13:00",
                    "title": "Bring a crew to do your job",
                    "speaker": "Jettro Coenradie (Luminis)",
                    "category": "Applicatie Ontwikkeling / Human aspects"
                  },
                  {
                    "time": "13:00",
                    "title": "LLMs beveiligen - voorkom een Log Lek Model",
                    "speaker": "Jelle Fremery (Betabit)",
                    "category": "Artificial Intelligence / Security"
                  },
                  {
                    "time": "14:00",
                    "title": "Mijn (Cloud) leverancier heeft ISO 27001 - Veilig om zijn software te gebruiken toch? …Hackers lachen erom...",
                    "speaker": "Peter Stegeman (Achmea)",
                    "category": "Security"
                  },
                  {
                    "time": "14:00",
                    "title": "Ontwikkelen van toptalenten in je organisatie",
                    "speaker": "Bert Ertman (Luminis)",
                    "category": "Human aspects in IT"
                  },
                  {
                    "time": "14:00",
                    "title": "Interne documenten en externe data toegankelijk met chatbots en agents",
                    "speaker": "Wim Florijn, Fabian Frank en Janneke Michielsen (Kadaster)",
                    "category": "Artificial Intelligence / Applicatie Ontwikkeling"
                  },
                  {
                    "time": "14:00",
                    "title": "Putting Hexagonal Architecture into practice",
                    "speaker": "Laurens Rouw (Craftmens)",
                    "category": "Applicatie Ontwikkeling"
                  },
                  {
                    "time": "14:00",
                    "title": "Softwarekwaliteit voor niet-ontwikkelaars",
                    "speaker": "Piet Bonnema en Ben Louman (Belastingdienst)",
                    "category": "Applicatie Ontwikkeling"
                  },
                  {
                    "time": "15:00",
                    "title": "Mkb’ers helpen met cyberincident - een Apeldoornse primeur",
                    "speaker": "Jan-Peter Soenveld en Carlyn van Amersfoort (Saxion)",
                    "category": "Security"
                  },
                  {
                    "time": "16:00",
                    "title": "Keynote Pier Eringa",
                    "speaker": "Pier Eringa",
                    "category": "Keynote / Human aspects"
                  },
                  {
                    "time": "17:00",
                    "title": "Keynote De Vrije Denkers",
                    "speaker": "De Vrije Denkers",
                    "category": "Human aspects / Closing"
                  },
                  {
                    "time": "17:30",
                    "title": "Afsluiting en borrel",
                    "speaker": "",
                    "category": "General"
                  }
                ]
                """;

        String userMessage = String.format("""
                Here is the message to answer:
                %s
                """, payload);

        return Mono.fromCallable(() -> chatClient.prompt()
                .system(prompt)
                .user(userMessage)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).conversationId(AGENT_NAME).build())
                .call()
                .content()).subscribeOn(Schedulers.boundedElastic());
    }
}
