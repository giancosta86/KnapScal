# KnapScal

*ScalaFX didactic application for the Knapsack problem*



### Introduction

KnapScal is a didactic visual application dedicated to the [Knapsack problem](https://en.wikipedia.org/wiki/Knapsack_problem).

It can solve an instance of the problem by employing different techniques:

* **KP01 Branch & Bound** - with a few standard functions for computing the upper bound of nodes. In particular:

  * *Dantzig*
  * *Optimized Dantzig*
  * *Martello-Toth*

* **KP01 Dynamic Programming** - keeping track of both active and dominated states

* **Optimized Dynamic programming** - using recursive functions to compute just the value of the solution



### Requirements

KnapScal requires Java 8 Update 45 or later compatible version.



### Running KnapScal

The suggested way to run KnapScal is [MoonDeploy](https://github.com/giancosta86/moondeploy), as it will automatically download and launch the application - just go to the [latest release](https://github.com/giancosta86/KnapScal/releases/latest) page and open the file **App.moondeploy**.

Otherwise, to start the application:
1. Download and decompress the zip archive
2. Run the file *bin/KnapScal* (on UNIX) or *bin/KnapScal.bat* (on Windows)



### Employing the kernel

[KnapScal-core](https://github.com/giancosta86/KnapScal-core), the underlying library, can be used in other applications for the JVM.



### Special Thanks

Special thanks to [Professor Silvano Martello](http://www.or.deis.unibo.it/staff_pages/martello/cvitae.html) for his valuable advice and teaching.



### Further references

* [KnapScal-core](https://github.com/giancosta86/KnapScal-core) - Scala library for the Knapsack problem
* [Professor Silvano Martello - Didactic slides](http://www.or.deis.unibo.it/staff_pages/martello/Slides_LM_new.html)
