{:ct-tomcat-url     "http://localhost:{tomcat.port}/testservice"
 :fake.servers.port 8999
 :nrepl.port        4006
 :tomcat.port       8080
 :cs                {:prefix-url "http://localhost:{fake.servers.port}/cs"}
 :mt                {:prefix-url "http://localhost:{fake.servers.port}/mt"}}