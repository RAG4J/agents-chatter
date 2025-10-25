# Application Services

Contains framework-free application layer services and ports. These orchestrate domain rules (from `core-domain`) and define the inbound/outbound interfaces that infrastructure adapters plug into (messages, moderation, agent registry).

Guidelines:
- Keep this module free from Spring, Reactor, or transport-specific APIs.
- Expose use cases via explicit port interfaces and service classes.
- Leave persistence, messaging implementations, and controllers to higher-level modules.
