FROM hseeberger/scala-sbt:11.0.11_1.5.4_2.13.6

COPY . .

RUN sbt compile

EXPOSE 9000
