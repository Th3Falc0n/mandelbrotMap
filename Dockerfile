FROM lolhens/sbt-graal:22.3.1-java11 as builder

COPY . .
ARG CI_VERSION
RUN sbt assembly
RUN cp "$(find target/scala-* -type f -name '*.sh.bat')" /tmp/app

FROM openjdk:20

COPY --from=builder /tmp/app /opt/app

CMD exec /opt/app
