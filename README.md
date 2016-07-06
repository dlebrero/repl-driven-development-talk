REPL Driven Development talk
============================
 
Project used for the talk at [Skills Matter](https://skillsmatter.com/meetups/8087-using-repl-driven-development) for the London Java User Group
 
Running
=======
 
      mvn clean install -Pdevrun
 
API
===
 
App comes with a [Swagger UI](http://localhost:8080/testservice/index.html)

Developing
==========
  
Using Intellij IDEA + Cursive
-----------------------------
 
  1. Start the app from the command line
  2. Connect to app as a "remote" repl in localhost:4006. See the [Cursive REPL guide](https://cursive-ide.com/userguide/repl.html)
  3. Run *(autotest)* in the REPL window 
  4. Hack
  5. Go to 4

If you feel really brave, the app also uses [Stuart Sierra's reloaded workflow](http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded)