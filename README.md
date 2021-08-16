# Advanced Vert.x guide

This is an effort that aims to document more advanced/internal about Vert.x as a guide.

[![Build Status](https://github.com/vietj/advanced-vertx-guide/workflows/CI/badge.svg?branch=master)](https://github.com/vietj/advanced-vertx-guide/actions)

- [Latest version](http://www.julienviet.com/advanced-vertx-guide/)

## Building the book

```
> mvn package
> open target/docs/advanced-vertx-guide/index.html
```

## Project structure

- [Asciidoc sources](src/main/asciidoc/): these contain references to the [Java code](src/main/java/)
- [Java code](src/main/java/): the various code examples included in the Asciidoc sources

The book uses the [Vert.x Docgen](https://github.com/vert-x3/vertx-docgen) project to render Java code
in the Asciidoc files, allowing real source code to be included in the project with Javadoc `{@link}` tags.

## Contributing

Anyone is welcome to contribute to this guide given that it improves this guide.

This is GitHub based and integrated with Travis, that means you can fork it, build it in your own
Travis account and make pull requests.
