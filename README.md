# latakia

![Tobacco leaf](https://upload.wikimedia.org/wikipedia/commons/2/2f/Tobacco_Leave.svg)

A simple web app to ease self registration by e-mail on Prosody IM (but virtually extensible to any service) originally developed for [Progetto EXIT - Riprendo la mia privacy!](https://www.3x1t.org/)

## Requirements
- Prosody IM
- JRE 8 or higher
- sudo
- expect

## Build
JDK / Clojure / [Leiningen](https://leiningen.org/#install) toolchain is required

Then get the sources:
```
$ git clone https://github.com/saidone75/latakia
```
and compile with:
```
$ cd latakia
$ lein uberjar
```
## License
Copyright (c) 2021 Saidone

Distributed under the GPL-3.0 License

Leaf draw by <a href="https://commons.wikimedia.org/wiki/File:Tobacco_Leave.svg">Angel Paez</a>, <a href="https://creativecommons.org/licenses/by/3.0">CC BY 3.0</a>, via Wikimedia Commons
