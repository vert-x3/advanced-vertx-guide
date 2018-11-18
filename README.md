# Advanced Vert.x guide

This is an effort that aims to document more advanced/internal about Vert.x as a guide.

[![Build Status](https://travis-ci.org/vietj/advanced-vertx-guide.svg?branch=master)](https://travis-ci.org/vietj/advanced-vertx-guide)

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

I welcome anyone wanting to contribute a chapter to this book. There is no predefined list of chapters, anyone
 is free to contribute content, given that it provides advanced/internal content about Vert.x that can be shared
 freely with the community.

I also welcome any edits, as some are not native English writes (like me), there are often mistakes that a native
English writer can correct.