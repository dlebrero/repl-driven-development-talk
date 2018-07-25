FROM clojure:lein-2.7.1

COPY . /app
WORKDIR /app

RUN lein deps

CMD ./start.sh