REPL Driven Development talk
============================
 
Project used for the talk at [Skills Matter](https://skillsmatter.com/meetups/8087-using-repl-driven-development) for the London Java Community. Slides are [here](https://github.com/IG-Group/jug-repl-driven-development-talk/blob/master/Using%20REPL%20driven%20development.key)
 
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
  2. Install the [Cursive plugin](https://cursive-ide.com/userguide/) 
  3. Connect to app as a "remote" repl in localhost:4006. See the [Cursive REPL guide](https://cursive-ide.com/userguide/repl.html)
  4. Run *(autotest)* in the REPL window 
  5. Hack
  6. Go to 5

If you feel really brave, the app also uses [Stuart Sierra's reloaded workflow](http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded)
