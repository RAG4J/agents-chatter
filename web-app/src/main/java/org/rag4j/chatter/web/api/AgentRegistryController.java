package org.rag4j.chatter.web.api;

import java.util.List;

import org.rag4j.chatter.application.port.in.AgentDiscoveryPort;
import org.rag4j.chatter.application.port.in.AgentRegistrationUseCase;
import org.rag4j.chatter.domain.agent.AgentDescriptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agents")
public class AgentRegistryController {

    private final AgentRegistrationUseCase registrationUseCase;
    private final AgentDiscoveryPort discoveryPort;

    public AgentRegistryController(
            AgentRegistrationUseCase registrationUseCase,
            @Qualifier("agentDiscoveryPort") AgentDiscoveryPort discoveryPort) {
        this.registrationUseCase = registrationUseCase;
        this.discoveryPort = discoveryPort;
    }

    @GetMapping
    public List<AgentDescriptor> listAgents() {
        return discoveryPort.listAgents();
    }

    @PostMapping
    public AgentDescriptor register(@RequestBody AgentDescriptor descriptor) {
        registrationUseCase.register(descriptor);
        return descriptor;
    }

    @DeleteMapping("/{agentName}")
    public HttpStatus unregister(@PathVariable("agentName") String agentName) {
        registrationUseCase.unregister(agentName);
        return HttpStatus.NO_CONTENT;
    }
}
