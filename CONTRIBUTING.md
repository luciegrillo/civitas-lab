# Contributing

Civitas Lab welcomes focused contributions that preserve reproducibility and
scientific claim discipline.

## Development Rules

- Discuss substantial behavioral changes in an issue first.
- Keep the mathematical core independent of I/O and third-party libraries.
- Add tests for every model rule or output contract that changes.
- Document scientific assumptions and cite the mechanism being implemented.
- Do not interpret simulation output as evidence about real societies without
  an explicit validation argument.

## Commit Style

Use Conventional Commits, for example:

```text
feat(core): implement synchronous lattice updates
fix(app): reject unknown experiment properties
docs(model): explain bounded boundary semantics
```

## Pull Requests

Describe the intent, implementation choices, tests run, and any effect on
reproducibility or scientific interpretation.
