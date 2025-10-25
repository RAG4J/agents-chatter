package org.rag4j.chatter.web.api;

import java.util.List;

import org.rag4j.chatter.application.agents.AgentRegistryService;
import org.rag4j.chatter.domain.agent.AgentDescriptor;
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

    private final AgentRegistryService registryService;

    public AgentRegistryController(AgentRegistryService registryService) {
        this.registryService = registryService;
    }

    @GetMapping
    public List<AgentDescriptor> listAgents() {
        return registryService.listAgents();
    }

    @PostMapping
    public AgentDescriptor register(@RequestBody AgentDescriptor descriptor) {
        registryService.register(descriptor);
        return descriptor;
    }

    @DeleteMapping("/{agentName}")
    public HttpStatus unregister(@PathVariable("agentName") String agentName) {
        registryService.unregister(agentName);
        return HttpStatus.NO_CONTENT;
    }
}
