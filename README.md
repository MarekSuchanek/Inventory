# Inventory

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Build Status](https://travis-ci.org/MarekSuchanek/Inventory.svg?branch=master)](https://travis-ci.org/MarekSuchanek/Inventory)

Web application (Scala-based, Play framework) for managing own inventory of various things.

## Introduction

It should be simple and flexible enough to allow capturing all needed relations within your personal 
or commercial inventory. It specializes in capturing **part-whole** relations in many levels by using: 

- atomic thing
- composite thing (parts has some functional purpose)
- container (all parts has same purpose -> being contained)

## Usage

Use [sbt](http://www.scala-sbt.org) tool!

```bash
sbt run
```

## License

This project is licensed under the MIT license - see the [LICENSE](LICENSE) file for more details.
